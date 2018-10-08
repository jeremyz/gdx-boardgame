package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.utils.Disposable;

import ch.asynk.gdx.boardgame.Drawable;

public interface Animation extends Drawable, Disposable
{
    public boolean completed();
    public boolean animate(float delta);
}
