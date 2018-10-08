package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Drawable
{
    public void draw(Batch batch);
    default public void drawDebug(ShapeRenderer debugShapes) { }
}
