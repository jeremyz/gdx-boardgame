package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.Overlays;

public class Tile implements Drawable
{
    public static TextureAtlas defaultOverlay = null;

    public float x;
    public float y;
    private Overlays overlays;

    public Tile(float x, float y)
    {
        this.x = x;
        this.y = y;
        if (defaultOverlay != null) {
            this.overlays = new Overlays(defaultOverlay);
            this.overlays.centerOn(x, y);
        }
    }

    @Override public void draw(Batch batch)
    {
        if (overlays != null) {
            overlays.draw(batch);
        }
    }

    public void enableOverlay(int i, boolean enable)
    {
        if (overlays != null) {
            overlays.enable(i, enable);
        }
    }

    public void enableOverlay(int i, Orientation o)
    {
        if (overlays != null) {
            overlays.setRotation(i, o.r());
            overlays.enable(i, true);
        }
    }

    @Override public String toString()
    {
        return "[" + x + ", " + y + "]";
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        if (overlays != null) {
            overlays.drawDebug(shapeRenderer);
        }
    }
}
