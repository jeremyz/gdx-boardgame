package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Button extends Patch
{
    protected Label label;
    protected float spacing;    // label is aligned within spacing frame;

    public Button(BitmapFont font, NinePatch patch)
    {
        this(font, patch, 0);
    }

    public Button(BitmapFont font, NinePatch patch, float padding)
    {
        this(font, patch, padding, 0);
    }

    public Button(BitmapFont font, NinePatch patch, float padding, float spacing)
    {
        super(patch);
        this.padding = padding;
        this.spacing = spacing;
        this.label = new Label(font);
        this.label.setParent(this, Alignment.MIDDLE_CENTER);
        this.sizing = Sizing.EXPAND_BOTH;
    }

    public void write(String text)
    {
        label.write(text);
        taint();
    }

    public void setLabelAlignment(Alignment alignment)
    {
        label.setAlignment(alignment);
    }

    @Override public void computeDimensions()
    {
        if(sizing.fill()) {
            super.computeDimensions();
        } else {
            float dd = 2 * (padding + spacing);
            label.computeDimensions();
            if (sizing.expandWidth())
                rect.width = label.getWidth() + dd;
            if (sizing.expandHeight())
                rect.height = label.getHeight() + dd;
            if (DEBUG_GEOMETRY) System.err.println("  dim " + print(-1));
        }
    }

    @Override public void computePosition()
    {
        super.computePosition();
        label.computePosition();
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        if (tainted) computeGeometry();
        super.draw(batch);
        label.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        super.drawDebug(shapeRenderer);
        label.drawDebug(shapeRenderer);
    }
}
