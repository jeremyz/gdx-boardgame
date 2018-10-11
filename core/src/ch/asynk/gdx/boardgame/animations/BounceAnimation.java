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

    public static BounceAnimation get(Piece piece, float duration, float bounceFactor, int rotations)
    {
        BounceAnimation a = bounceAnimationPool.obtain();

        a.piece = piece;
        a.bounceFactor = bounceFactor;
        a.setDuration(duration);
        a.computeRotationDegrees(rotations);

        return a;
    }

    private Piece piece;
    private float startScale;
    private float startRotation;
    private float bounceFactor;
    private float rotationDegrees;

    private BounceAnimation()
    {
    }

    public BounceAnimation(Piece piece, float duration, float bounceFactor, int rotations)
    {
        this.piece = piece;
        this.bounceFactor = bounceFactor;
        this.setDuration(duration);
        this.computeRotationDegrees(rotations);
    }

    private void computeRotationDegrees(int rotations)
    {
        if (rotations == 0) {
            rotationDegrees = 0f;
        } else {
            rotationDegrees = (360 * rotations);
        }
    }

    @Override public void dispose()
    {
        bounceAnimationPool.free(this);
    }

    @Override protected void begin()
    {
        this.startScale = piece.getScale();
        this.startRotation = piece.getRotation();
    }

    @Override protected void end()
    {
        piece.setScale(this.startScale);
        if (rotationDegrees != 0f) {
            piece.setRotation(this.startRotation);
        }
    }

    @Override protected void update(float percent)
    {
        piece.setScale(this.startScale + this.bounceFactor * (float) Math.sin(percent * Math.PI));
        if (rotationDegrees != 0f) {
            piece.setRotation(this.startRotation + (percent * this.rotationDegrees));
        }
    }

    @Override public void draw(Batch batch)
    {
        piece.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
