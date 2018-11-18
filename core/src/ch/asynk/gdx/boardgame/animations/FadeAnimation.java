package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;

import ch.asynk.gdx.boardgame.Drawable;

public class FadeAnimation extends TimedAnimation implements Pool.Poolable
{
    private static final Pool<FadeAnimation> fadeAnimationPool = new Pool<FadeAnimation>()
    {
        @Override protected FadeAnimation newObject()
        {
            return new FadeAnimation();
        }
    };

    public static FadeAnimation obtain(Drawable drawable, float from, float to, float duration)
    {
        FadeAnimation a = fadeAnimationPool.obtain();

        a.from = from;
        a.to = to;
        a.drawable = drawable;
        a.setDuration(duration);

        return a;
    }

    private float from;
    private float to;
    private Drawable drawable;

    private FadeAnimation()
    {
    }

    @Override public void dispose()
    {
        fadeAnimationPool.free(this);
    }

    @Override public void begin()
    {
        drawable.setAlpha(from);
    }

    @Override public void end()
    {
        drawable.setAlpha(to);
    }

    @Override public void update(float delta)
    {
        drawable.setAlpha(from + ((to - from) * percent));
    }
}
