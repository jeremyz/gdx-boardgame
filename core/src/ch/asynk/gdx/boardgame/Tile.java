package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.Overlays;

public class Tile implements Drawable
{
    public static TextureAtlas defaultOverlay = null;

    public int x;
    public int y;
    public float cx;
    public float cy;
    public boolean blocked;
    private Overlays overlays;

    public Tile(int x, int y, float cx, float cy)
    {
        this.x = x;
        this.y = y;
        this.cx = cx;
        this.cy = cy;
        this.blocked = false;
        if (defaultOverlay != null) {
            setOverlay(defaultOverlay);
        }
    }

    public boolean blockLos(final Tile from, final Tile to)
    {
        return false;
    }

    public boolean overlaysEnabled()
    {
        if (overlays != null) {
            return overlays.isEnabled();
        }
        return false;
    }

    public void setOverlay(TextureAtlas textureAtlas)
    {
            this.overlays = new Overlays(textureAtlas);
            this.overlays.centerOn(cx, cy);
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
        return "[" + x + ", " + y + "] => [" + cx + "," + cy + "]";
    }

    @Override public void draw(Batch batch)
    {
        if (overlays != null) {
            overlays.draw(batch);
        }
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        if (overlays != null) {
            overlays.drawDebug(shapeRenderer);
        }
    }
}
