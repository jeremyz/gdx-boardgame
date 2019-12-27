package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import ch.asynk.gdx.boardgame.utils.Collection;

public class List extends Element
{
    public interface Item
    {
        public String s();
    }

    private BitmapFont font;
    private GlyphLayout layout;
    private float spacing;
    private float itemHeight;
    private Integer idx;
    private Bg selected;
    private Collection<Item> items;

    public List(BitmapFont font, TextureRegion selected, float padding, float spacing)
    {
        this.font = font;
        this.padding = padding;
        this.spacing = spacing;
        this.layout = new GlyphLayout();
        this.idx = null;
        this.selected = new Bg(selected);
        this.selected.setParent(this);
        this.selected.visible = false;
        this.sizing = Sizing.EXPAND_BOTH;
    }

    public void unselect() { idx = null; }
    public Integer getIdx() { return idx; }
    public Item getSelected() { return ((idx == null) ? null : items.get(idx)); }

    @Override public Element touch(float x, float y)
    {
        final Element touched = super.touch(x, y);
        if (touched != null) {
            int i = (int) Math.floor((getInnerTop() - y) / itemHeight);
            if (i >= 0 && i < items.size()) {
                if (idx == null || idx != i) {
                    idx = i;
                    selected.setPosition(0, getInnerHeight() - ((idx + 1) * itemHeight) + spacing / 2f, getWidth(), itemHeight);
                    selected.visible = true;
                }
                return touched;
            }
        }
        idx = null;
        selected.visible = false;
        return null;
    }

    public void setItems(Collection<Item> items)
    {
        unselect();
        this.items = items;
        taint();
    }

    @Override public void computeDimensions()
    {
        float w = 0f;
        for (Item e: items) {
            layout.setText(font, e.s());
            if (layout.width > w) w = layout.width;
        }
        itemHeight = (layout.height + spacing);
        if (sizing.fill()) {
            super.computeDimensions();
        } else if (sizing.expand()) {

            if (sizing.expandWidth())
                rect.width = w + (2 * padding);
            if (sizing.expandHeight())
                rect.height = (itemHeight * items.size()) + (2 * padding) - spacing;
            if (DEBUG_GEOMETRY) System.err.println("  dim " + print(-1));
        }
    }

    @Override public void computePosition()
    {
        super.computePosition();
        selected.computePosition();
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;

        if (tainted) computeGeometry();
        float x = getInnerX();
        float y = getInnerTop();
        for (Item e : items) {
            font.draw(batch, e.s(), x, y);
            y -= itemHeight;
        }
        selected.draw(batch);
    }
}
