package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

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
    }

    public void setSpacing(float spacing)
    {
        this.spacing = spacing;
        taint();
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
                    selected.setPosition(0, getHeight() - padding + spacing / 2 - ((idx + 1) * itemHeight), getWidth(), itemHeight);
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

    @Override public void translate(float x, float y)
    {
        super.translate(x, y);
        selected.taint();
    }

    public void computeDimensions()
    {
        float w = 0f;
        for (Item e: items) {
            layout.setText(font, e.s());
            if (layout.width > w) w = layout.width;
        }
        itemHeight = (layout.height + spacing);
        rect.width = w + (2 * padding);
        rect.height = (itemHeight * items.size()) + (2 * padding) - spacing;
    }

    @Override public void computeGeometry(Rectangle area, boolean resized)
    {
        computeDimensions();
        super.computeGeometry(area, resized);
        selected.computeGeometry(rect, resized);
    }

    @Override public void drawReal(Batch batch)
    {
        float x = getInnerX();
        float y = getInnerTop();
        for (Item e : items) {
            font.draw(batch, e.s(), x, y);
            y -= itemHeight;
        }
        selected.draw(batch);
    }
}
