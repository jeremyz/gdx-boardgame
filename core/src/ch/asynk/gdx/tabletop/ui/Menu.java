package ch.asynk.gdx.tabletop.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Menu extends Patch
{
    private Label title;
    private Label[] entries;
    private Integer touchedItem;

    private int entriesOffset;      // horizontal offset
    private int titleSpacing;       // between title and entries
    private int entriesSpacing;     // between entries

    public Menu(BitmapFont font, NinePatch patch, String title, String[] entries)
    {
        super(patch);
        this.touchedItem = null;
        setTitle(font, title);
        setEntries(font, entries);
    }

    public void setTitle(BitmapFont font, String title)
    {
        this.title = new Label(font);
        this.title.write(title);
        this.tainted = true;
    }

    public void setEntries(BitmapFont font, String[] entries)
    {
        this.entries = new Label[entries.length];
        for (int i = 0; i < entries.length; i++) {
            Label l = new Label(font);
            l.write(entries[i]);
            this.entries[i] = l;
        }
        this.tainted = true;
    }

    public void setLabelsOffset(int entriesOffset)
    {
        this.entriesOffset = entriesOffset;
        this.tainted = true;
    }

    public void setPaddings(int titlePadding, int labelPadding)
    {
        this.title.setPadding(titlePadding);
        for (Label label : entries) {
            label.setPadding(labelPadding);
        }
        this.tainted = true;
    }

    public void setSpacings(int titleSpacing, int entriesSpacing)
    {
        this.titleSpacing = titleSpacing;
        this.entriesSpacing = entriesSpacing;
        this.tainted = true;
    }

    @Override public void computeGeometry()
    {
        title.computeGeometry();
        float h = title.getHeight();
        float w = title.getWidth();
        for (Label label : entries) {
            label.computeGeometry();
            h += label.getHeight();
            float t = label.getWidth() + entriesOffset;
            if (t > w)
                w = t;
        }
        h += (titleSpacing + (entriesSpacing * (entries.length - 1)) + (2 * padding));
        w += (2 * padding);

        rect.width = w;
        rect.height = h;
        super.computeGeometry();

        float x = getInnerX();
        float y = getInnerY();

        // setPosition() will trigger computeGeometry() from within draw()
        for (int i = entries.length - 1; i >= 0; i--) {
            entries[i].setPosition(x + entriesOffset, y);
            y += entries[i].getHeight() + entriesSpacing;
        }
        y -= entriesSpacing;
        y += titleSpacing;
        title.setPosition(x, y);
    }

    public Integer touched()
    {
        return touchedItem;
    }

    @Override public boolean touch(float x, float y)
    {
        touchedItem = null;
        if (super.touch(x, y)) {
            for (int i = 0; i < entries.length; i++) {
                if (entries[i].touch(x, y)) {
                    touchedItem = i;
                    return true;
                }
            }
        }
        return false;
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
}
