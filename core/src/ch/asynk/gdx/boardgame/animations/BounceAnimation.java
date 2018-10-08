package ch.asynk.gdx.boardgame.animations;

import java.lang.Math;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.Scalable;

public class BounceAnimation extends TimedAnimation
{
    private static final Pool<BounceAnimation> bounceAnimationPool = new Pool<BounceAnimation>()
    {
        @Override protected BounceAnimation newObject()
        {
            return new BounceAnimation();
        }
    };

    public static BounceAnimation get(Scalable scalable, float duration, float bounceFactor)
    {
        BounceAnimation a = bounceAnimationPool.obtain();

        a.scalable = scalable;
        a.duration = duration;
        a.bounceFactor = bounceFactor;

        return a;
    }

    private Scalable scalable;
    private float bounceFactor;

    private BounceAnimation()
    {
    }

    public BounceAnimation(Scalable scalable, float duration, float bounceFactor)
    {
        this.scalable = scalable;
        this.duration = duration;
        this.bounceFactor = bounceFactor;
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
        scalable.setScale(1f);
    }

    @Override protected void update(float percent)
    {
        scalable.setScale(1 + bounceFactor * (float) Math.sin(percent * Math.PI));
    }

    @Override public void draw(Batch batch)
    {
        scalable.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
    }
}
