package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.utils.IterableSet;

public abstract class Assembly extends Element
{
    private Element touched;

    public Assembly(int c)
    {
        this.children = new IterableSet<Element>(c);
    }

    public void taintChildren()
    {
        children.forEach( c -> c.taint() );
    }

    public Element touched()
    {
        return touched;
    }

    @Override public boolean touch(float x, float y)
    {
        for (Element e : children) {
            if (e.touch(x, y)) {
                touched = e;
                return true;
            }

        }
        touched = null;
        return false;
    }

    @Override public boolean drag(float x, float y, int dx, int dy)
    {
        if (touched != null) {
            if (touched.drag(x, y, dx, dy))
                return true;
            touched = null;
        }
        return false;
    }
}
