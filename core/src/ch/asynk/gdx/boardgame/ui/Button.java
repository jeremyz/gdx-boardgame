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
        float dd = 2 * (padding + spacing);
        label.computeDimensions();
        rect.width = label.getWidth() + dd;
        rect.height = label.getHeight() + dd;
        if (DEBUG_GEOMETRY) System.err.println("  dim " + print(-1));
    }

    @Override public void computePosition()
    {
        super.computePosition();
        label.computePosition();
    }

    @Override public void drawReal(Batch batch)
    {
        super.drawReal(batch);
        label.drawReal(batch);
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        super.drawDebug(shapeRenderer);
        label.drawDebug(shapeRenderer);
    }
}
