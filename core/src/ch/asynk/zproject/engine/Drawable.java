package ch.asynk.gdx.board.engine;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public interface Drawable
{
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public float getInnerX();
    public float getInnerY();
    public float getInnerWidth();
    public float getInnerHeight();
    public void draw(Batch batch);
    default public void drawDebug(ShapeRenderer debugShapes) { }
    public void setPosition(float x, float y, float w, float h);
}
