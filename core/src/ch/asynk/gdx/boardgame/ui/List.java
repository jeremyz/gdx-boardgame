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
        this.selected.visible = false;
    }

    public void unselect() { idx = null; }
    public Integer getIdx() { return idx; }
    public Item getSelected() { return ((idx == null) ? null : items.get(idx)); }

    @Override public boolean touch(float x, float y)
    {
        if (super.touch(x, y)) {
            idx = (int) Math.floor((getInnerTop() - y) / itemHeight);
            if ((idx >= 0) && (idx < items.size())) {
                selected.setPosition(getX(), getInnerTop() - ((idx + 1) * itemHeight) + spacing / 2f, getWidth(), itemHeight);
                selected.visible = true;
                return true;
            }
        }
        idx = null;
        selected.visible = false;
        return false;
    }

    public void setItems(Collection<Item> items)
    {
        unselect();
        this.items = items;
        float w = 0f;
        for (Item e: items) {
            layout.setText(font, e.s());
            if (layout.width > w) w = layout.width;
        }
        itemHeight = (layout.height + spacing);

        rect.width = w + (2 * padding);
        rect.height = (itemHeight * items.size()) + (2 * padding) - spacing;
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;

        float x = getInnerX();
        float y = getInnerTop();
        for (Item e : items) {
            font.draw(batch, e.s(), x, y);
            y -= itemHeight;
        }
        selected.draw(batch);
    }
}
