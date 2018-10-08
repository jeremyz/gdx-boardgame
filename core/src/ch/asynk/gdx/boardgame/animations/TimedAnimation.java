package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Pool;

public abstract class TimedAnimation implements Animation, Pool.Poolable
{
    private float time;
    private boolean started;
    private boolean completed;
    protected float duration;

    abstract protected void begin();
    abstract protected void end();
    abstract protected void update(float percent);

    @Override public void reset()
    {
        time = 0f;
        started = false;
        completed = false;
    }

    @Override public boolean completed()
    {
        return completed;
    }

    @Override public boolean animate(float delta)
    {
        if (completed) return true;

        if (!started) {
            begin();
            started = true;
        }

        time += delta;

        if (time >= duration) {
            completed = true;
            update(1);
            end();
        } else {
            update(time / duration);
        }

        return completed;
    }
}
