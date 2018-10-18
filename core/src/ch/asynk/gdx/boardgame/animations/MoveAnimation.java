package ch.asynk.gdx.boardgame.animations;

import java.lang.Math;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.gdx.boardgame.pieces.Piece;
import ch.asynk.gdx.boardgame.Path;

public class MoveAnimation implements Animation, Pool.Poolable
{
    @FunctionalInterface
    public interface MoveAnimationCb
    {
        void onTileChange(Piece piece, Path path);
    }

    private static final Pool<MoveAnimation> moveAnimationPool = new Pool<MoveAnimation>()
    {
        @Override protected MoveAnimation newObject()
        {
            return new MoveAnimation();
        }
    };

    public static MoveAnimation obtain(Piece piece, Path path, float speed, MoveAnimationCb cb)
    {
        MoveAnimation a = moveAnimationPool.obtain();

        a.piece = piece;
        a.path = path;
        a.speed = speed;
        a.cb = cb;

        a.init();

        return a;
    }

    private Piece piece;
    private Path path;
    private MoveAnimationCb cb;
    private float speed;
    private float dp;
    private float percent;
    private boolean notify;
    private Vector3 dst = new Vector3();
    private Vector3 dt = new Vector3();

    private MoveAnimation()
    {
    }

    private void init()
    {
        path.iterator();
        setNextMove();
        percent = 0f;
        dp = 1f * speed;
    }

    private boolean setNextMove()
    {
        boolean done = path.nextPosition(piece, dst);

        float dr = (dst.z - piece.getRotation());
        if (dr > 180) {
            dr -= 360;
        } else if (dr < -180) {
            dr += 360;
        }

        dt.set(
                (dst.x - piece.getX()) * speed,
                (dst.y - piece.getY()) * speed,
                (dr) * speed
              );
        notify = (dr == 0 ? true : false);
        return done;
    }

    @Override public void reset()
    {
        this.path = null;
        this.piece = null;
        this.dst.set(0, 0, 0);
    }

    @Override public void dispose()
    {
        moveAnimationPool.free(this);
    }

    @Override public boolean completed()
    {
        return (percent >= 1f);
    }

    @Override public boolean animate(float delta)
    {
        piece.translate(dt.x * delta, dt.y * delta);
        piece.rotate(dt.z * delta);

        percent += (dp * delta);
        if (notify && cb != null && percent >= 0.5f) {
            cb.onTileChange(piece, path);
            notify = false;
        }
        if (percent >= 1f) {
            piece.setPosition(dst.x, dst.y, dst.z);
            if (!setNextMove()) {
                percent = 0f;
            }
        }

        return (percent >= 1f);
    }

    @Override public void draw(Batch batch)
    {
        piece.draw(batch);
    }
}
