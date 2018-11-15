package ch.asynk.gdx.boardgame.animations;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.gdx.boardgame.Drawable;

public interface Animation extends Drawable, Disposable
{
    public boolean completed();
    public boolean animate(float delta);
    public default void draw(Batch batch) { };
}
