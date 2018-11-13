package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;

public abstract class TimedAnimation implements Animation
{
    private float dp;
    private float percent;
    private float elapsed;

    abstract protected void begin();
    abstract protected void end();
    abstract protected void update(float elapsed, float percent);

    public void setDuration(float duration)
    {
        dp = 1f / duration;
    }

    public void reset()
    {
        percent = 0f;
        elapsed = 0f;
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

        elapsed += delta;
        percent = (dp * elapsed);

        if (percent >= 1f) {
            // percent = 1f;
            // update(percent);
            end();
            return true;
        } else {
            update(elapsed, percent);
            return false;
        }
    }
}
