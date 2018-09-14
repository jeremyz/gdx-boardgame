package ch.asynk.gdx.board.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Button extends Patch
{
    private Label label;
    private float spacing;

    public Button(BitmapFont font, NinePatch patch)
    {
        this(font, patch, 0, 0);
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
        write(text, getX(), getY());
    }

    public void write(String text, float x, float y)
    {
        label.write(text, x, y);
    }

    public void setLabelAlignment(Alignment alignment)
    {
        label.setAlignment(alignment);
    }

    @Override public void update()
    {
        label.preUpdate();      // compute width and height
        rect.width = label.getWidth() + 2 * (padding + spacing);
        rect.height = label.getHeight() + 2 * (padding + spacing);
        super.update();
        label.doUpdate();       // compute x and y
        label.postUpdate();     // compute fx and fy
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        super.draw(batch);
        label.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
        super.drawDebug(debugShapes);
        label.drawDebug(debugShapes);
    }
}
