package ch.asynk.gdx.boardgame.animations;

public abstract class TimedAnimation implements Animation
{
    private float dp;
    protected float percent;
    protected float elapsed;

    abstract protected void begin();
    abstract protected void end();
    abstract protected void update(float delta);

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
            // update(delta);
            end();
            return true;
        } else {
            update(delta);
            return false;
        }
    }
}
