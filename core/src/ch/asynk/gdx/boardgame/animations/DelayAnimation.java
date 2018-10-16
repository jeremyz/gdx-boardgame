package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.gdx.boardgame.Drawable;

public class DelayAnimation extends TimedAnimation implements Pool.Poolable
{
    private static final Pool<DelayAnimation> delayAnimationPool = new Pool<DelayAnimation>()
    {
        @Override protected DelayAnimation newObject()
        {
            return new DelayAnimation();
        }
    };

    public static DelayAnimation obtain(Drawable drawable, float duration)
    {
        DelayAnimation a = delayAnimationPool.obtain();

        a.drawable = drawable;
        a.setDuration(duration);

        return a;
    }

    private Drawable drawable;

    private DelayAnimation()
    {
    }

    @Override public void reset()
    {
        super.reset();
    }

    @Override public void dispose()
    {
        delayAnimationPool.free(this);
    }

    @Override protected void begin() { }
    @Override protected void end() { }
    @Override protected void update(float percent) { }

    @Override public void draw(Batch batch)
    {
        drawable.draw(batch);
    }
}
