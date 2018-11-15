package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;

public class DelayAnimation extends TimedAnimation implements Pool.Poolable
{
    private static final Pool<DelayAnimation> delayAnimationPool = new Pool<DelayAnimation>()
    {
        @Override protected DelayAnimation newObject()
        {
            return new DelayAnimation();
        }
    };

    public static DelayAnimation obtain(float duration)
    {
        DelayAnimation a = delayAnimationPool.obtain();

        a.setDuration(duration);

        return a;
    }

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
    @Override protected void update(float delta) { }
}
