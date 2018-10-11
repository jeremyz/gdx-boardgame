package ch.asynk.gdx.boardgame.animations;

import java.lang.Math;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.pieces.Piece;

public class BounceAnimation extends TimedAnimation
{
    private static final Pool<BounceAnimation> bounceAnimationPool = new Pool<BounceAnimation>()
    {
        @Override protected BounceAnimation newObject()
        {
            return new BounceAnimation();
        }
    };

    public static BounceAnimation get(Piece piece, float duration, float bounceFactor)
    {
        BounceAnimation a = bounceAnimationPool.obtain();

        a.piece = piece;
        a.bounceFactor = bounceFactor;
        a.setDuration(duration);

        return a;
    }

    private Piece piece;
    private float bounceFactor;

    private BounceAnimation()
    {
    }

    public BounceAnimation(Piece piece, float duration, float bounceFactor)
    {
        this.piece = piece;
        this.bounceFactor = bounceFactor;
        this.setDuration(duration);
    }

    @Override public void dispose()
    {
        bounceAnimationPool.free(this);
    }

    @Override protected void begin()
    {
    }

    @Override protected void end()
    {
        piece.setScale(1f);
    }

    @Override protected void update(float percent)
    {
        piece.setScale(1 + bounceFactor * (float) Math.sin(percent * Math.PI));
    }

    @Override public void draw(Batch batch)
    {
        piece.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
