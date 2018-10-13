package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.graphics.g2d.Batch;

import ch.asynk.gdx.boardgame.Drawable;

public class DelayAnimation extends TimedAnimation
{
    private Drawable drawable;

    public DelayAnimation(Drawable drawable, float duration)
    {
        this.drawable = drawable;
        setDuration(duration);
    }

    @Override protected void begin() { }
    @Override protected void end() { }
    @Override protected void update(float percent) { }
    @Override public void dispose() { }

    @Override public void draw(Batch batch)
    {
        drawable.draw(batch);
    }
}
