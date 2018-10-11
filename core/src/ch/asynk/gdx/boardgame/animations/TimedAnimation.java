package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;

public abstract class TimedAnimation implements Animation, Pool.Poolable
{
    private float dp;
    private float percent;

    abstract protected void begin();
    abstract protected void end();
    abstract protected void update(float percent);

    public void setDuration(float duration)
    {
        dp = 1f / duration;
    }

    @Override public void reset()
    {
        percent = 0f;
    }

    @Override public boolean completed()
    {
        return (percent >= 1f);
    }

    @Override public boolean animate(float delta)
    {
        if (percent == 0) {
            begin();
        }

        percent += (dp * delta);

        if (percent >= 1f) {
            // percent = 1f;
            // update(percent);
            end();
            return true;
        } else {
            update(percent);
            return false;
        }
    }
}
