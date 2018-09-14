package ch.asynk.gdx.board.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class Label extends Element
{
    private BitmapFont font;
    private GlyphLayout layout;
    private float fx;
    private float fy;
    private String text;

    public Label(BitmapFont font)
    {
        this(font, 0);
    }

    public Label(BitmapFont font, float padding)
    {
        this(font, padding, Alignment.RELATIVE);
    }

    public Label(BitmapFont font, float padding, Alignment alignment)
    {
        super();
        this.font = font;
        this.padding = padding;
        this.alignment = alignment;
        this.layout = new GlyphLayout();
    }

    public void write(String text)
    {
        write(text, getX(), getY());
    }

    public void write(String text, float x, float y)
    {
        this.text = text;
        setPosition(x, y);
    }

    @Override protected void preUpdate()
    {
        this.layout.setText(font, (text == null) ? "" : text);
        super.setPosition(x, y, (layout.width + (2 * padding)), (layout.height + (2 * padding)));
    }

    @Override protected void postUpdate()
    {
        fx = getInnerX();
        fy = getInnerY() + layout.height;
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        font.draw(batch, layout, fx, fy);
    }
}
