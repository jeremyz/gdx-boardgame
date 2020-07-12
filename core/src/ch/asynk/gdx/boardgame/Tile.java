package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.Overlays;

public class Tile implements Drawable
{
    public static TextureAtlas defaultOverlay = null;

    public static final Tile OffMap = new Tile(Integer.MIN_VALUE, Integer.MIN_VALUE, 0f, 0f, false);

    public int x;
    public int y;
    public float cx;
    public float cy;
    public boolean blocked;
    public boolean onMap;

    public Tile parent;
    public int acc;
    public int searchCount;
    public boolean roadMarch;
    public float f;

    private Overlays overlays;

    public Tile(int x, int y, float cx, float cy)
    {
        this(x, y, cx, cy, true);
    }

    public Tile(int x, int y, float cx, float cy, boolean onMap)
    {
        this.x = x;
        this.y = y;
        this.cx = cx;
        this.cy = cy;
        this.onMap = onMap;
        this.blocked = false;
        if (defaultOverlay != null) {
            setOverlay(defaultOverlay);
        }
    }

    public boolean isOnBoard()
    {
        return onMap;
    }

    public boolean hasRoad(Orientation orientation)
    {
        return false;
    }

    public boolean blockLos(final Tile from, final Tile to, float d, float dt)
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

    public void disableOverlays()
    {
        overlays.disableAll();
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
        return "[" + x + ";" + y + "] => [" + (int)cx + ";" + (int)cy + "]";
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
