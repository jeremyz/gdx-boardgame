package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.math.MathUtils;

import ch.asynk.gdx.boardgame.Piece;

public class BounceAnimation extends TimedAnimation implements Pool.Poolable
{
    private static final Pool<BounceAnimation> bounceAnimationPool = new Pool<BounceAnimation>()
    {
        @Override protected BounceAnimation newObject()
        {
            return new BounceAnimation();
        }
    };

    public static BounceAnimation obtain(Piece piece, float duration, float bounceFactor, int rotations)
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

    private void computeRotationDegrees(int rotations)
    {
        if (rotations == 0) {
            rotationDegrees = 0f;
        } else {
            rotationDegrees = (360 * rotations);
        }
    }

    @Override public void reset()
    {
        super.reset();
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

    @Override protected void update(float delta)
    {
        piece.setScale(this.startScale + this.bounceFactor * (float) MathUtils.sin(percent * MathUtils.PI));
        if (rotationDegrees != 0f) {
            piece.setRotation(this.startRotation + (percent * this.rotationDegrees));
        }
    }
}
