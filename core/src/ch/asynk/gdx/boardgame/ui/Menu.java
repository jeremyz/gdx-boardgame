package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Menu extends Patch
{
    private Label title;
    private Label[] entries;
    private int touchedItem;

    private int entriesOffset;      // horizontal offset
    private int titleSpacing;       // between title and entries
    private int entriesSpacing;     // between entries

    public Menu(BitmapFont font, NinePatch patch, String title, String[] entries)
    {
        super(patch);
        this.touchedItem = -1;
        setTitle(font, title);
        setEntries(font, entries);
    }

    public void setTitle(BitmapFont font, String title)
    {
        this.title = new Label(font);
        this.title.write(title);
        this.title.setParent(this);
        taint();
    }

    public void setEntries(BitmapFont font, String[] entries)
    {
        this.entries = new Label[entries.length];
        for (int i = 0; i < entries.length; i++) {
            Label l = new Label(font);
            l.write(entries[i]);
            l.setParent(this);
            this.entries[i] = l;
        }
        taint();
    }

    public void setLabelsOffset(int entriesOffset)
    {
        this.entriesOffset = entriesOffset;
        taint();
    }

    public void setPaddings(int titlePadding, int labelPadding)
    {
        this.title.setPadding(titlePadding);
        for (Label label : entries) {
            label.setPadding(labelPadding);
        }
        taint();
    }

    public void setSpacings(int titleSpacing, int entriesSpacing)
    {
        this.titleSpacing = titleSpacing;
        this.entriesSpacing = entriesSpacing;
        taint();
    }

    @Override public void computeDimensions()
    {
        title.computeDimensions();
        float h = title.getHeight();
        float w = title.getWidth();
        for (Label label : entries) {
            label.computeDimensions();
            h += label.getHeight();
            float t = label.getWidth() + entriesOffset;
            if (t > w)
                w = t;
        }
        rect.width = w + (2 * padding);
        rect.height = h + (titleSpacing + (entriesSpacing * (entries.length - 1)) + (2 * padding));
        if (DEBUG_GEOMETRY) System.err.println("  dim " + print(-1));
    }

    @Override public void computePosition()
    {
        super.computePosition();

        float y = getInnerHeight() - title.getHeight();
        title.setPosition(0, y);
        y -= titleSpacing;
        for (Label l : entries) {
            l.setPosition(x + entriesOffset, y - l.getHeight());
            y -= (l.getHeight() + entriesSpacing);
        }
    }

    public int touched()
    {
        return touchedItem;
    }

    @Override public Element touch(float x, float y)
    {
        touchedItem = -1;
        if (super.touch(x, y) != null) {
            for (int i = 0; i < entries.length; i++) {
                final Element touched = entries[i].touch(x, y);
                if (touched != null) {
                    touchedItem = i;
                    return touched;
                }
            }
        }
        return null;
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        if (tainted) computeGeometry();
        super.draw(batch);
        title.draw(batch);
        for (Label label : entries) {
            label.draw(batch);
        }
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        super.drawDebug(shapeRenderer);
        title.drawDebug(shapeRenderer);
        for (Label label : entries) {
            label.drawDebug(shapeRenderer);
        }
    }
}
