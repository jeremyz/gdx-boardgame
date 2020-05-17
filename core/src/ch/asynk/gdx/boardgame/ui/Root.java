package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.gdx.boardgame.utils.IterableSet;

public class Root extends Container
{
    private Element touched;
    private boolean resized;

    public Root(int c)
    {
        this.alignment = Alignment.ABSOLUTE;
        this.children = new IterableSet<Element>(c);
    }

    public void resize(Rectangle r)
    {
        resize(r.x, r.y, r.width, r.height);
    }

    public void resize(float width, float height)
    {
        resize(getX(), getY(), width, height);
    }

    public void resize(float x, float y, float width, float height)
    {
        setPosition(x, y, width, height);
        resized = true;
    }

    @Override public void computeGeometry(Rectangle area, boolean resized)
    {
        rect.x = x;
        rect.y = y;
        innerRect.set(getInnerX(), getInnerY(), getInnerWidth(), getInnerHeight());
        super.computeGeometry(innerRect, resized);
    }

    public Element touched()
    {
        return touched;
    }

    @Override public void draw(Batch batch)
    {
        if (dirty || resized) {
            computeGeometry(null, resized);
            resized = false;
        }
        super.draw(batch);
    }

    @Override public Element touch(float x, float y)
    {
        touched = super.touch(x, y);
        return touched;
    }

    @Override public boolean drag(float x, float y, int dx, int dy)
    {
        if (touched != null && touched != this) {
            if (touched.drag(x, y, dx, dy))
                return true;
            touched = null;
        }
        return false;
    }
}
