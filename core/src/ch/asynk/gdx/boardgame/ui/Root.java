package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.gdx.boardgame.utils.IterableSet;

public class Root extends Element
{
    private Element touched;

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
        taint();
        taintChildren();
    }

    public Element touched()
    {
        return touched;
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

    @Override public void draw(Batch batch)
    {
        if (tainted) computeGeometry();
        children.forEach( c -> c.draw(batch) );
    }
}
