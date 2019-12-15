package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.Arrays;

public class Button extends Patch
{
    private Label label;
    private float spacing;      // for label alignment;

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
        label = new Label(font);
        label.setParent(this);
        label.setAlignment(Alignment.MIDDLE_CENTER);
    }

    public void write(String text)
    {
        label.write(text);
        this.tainted = true;    // might impact Button's geometry
    }

    public void setLabelAlignment(Alignment alignment)
    {
        label.setAlignment(alignment);
    }

    @Override public void computeGeometry()
    {
        float dd = 2 * (padding + spacing);
        label.computeGeometry();    // update dimensions
        rect.width = label.getWidth() + dd;
        rect.height = label.getHeight() + dd;
        super.computeGeometry();
        label.computeGeometry();    // update position
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        if (tainted) computeGeometry();
        super.draw(batch);
        label.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
        super.drawDebug(debugShapes);
        label.drawDebug(debugShapes);
    }

    @Override public int hashCode()
    {
        return Arrays.hashCode(new Object[]{super.hashCode(), label, spacing});
    }
}
