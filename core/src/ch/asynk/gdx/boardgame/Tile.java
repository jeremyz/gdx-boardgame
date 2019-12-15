package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

import java.util.Arrays;

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

    @Override public String toString()
    {
        return "[" + x + ", " + y + "]";
    }

    @Override public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return Float.compare(tile.x, x) == 0 &&
                Float.compare(tile.y, y) == 0 &&
                (overlays == tile.overlays) || (overlays != null && overlays.equals(tile.overlays));
    }

    @Override public int hashCode()
    {
        return Arrays.hashCode(new Object[]{x, y, overlays});
    }
}
