package ch.asynk.gdx.boardgame.ui;

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

    public String getText()
    {
        return text;
    }

    public void setFont(BitmapFont font)
    {
        this.font = font;
        write();
    }

    public void write(String text)
    {
        this.text = text;
        write();
    }

    public void write()
    {
        this.layout.setText(font, (text == null) ? "" : text);
        taint();
    }

    @Override public void computeGeometry()
    {
        this.rect.width = (layout.width + (2 * padding));
        this.rect.height = (layout.height + (2 * padding));
        super.computeGeometry();
        fx = getInnerX();
        fy = getInnerY() + layout.height;
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        if (tainted) computeGeometry();
        font.draw(batch, layout, fx, fy);
    }
}
