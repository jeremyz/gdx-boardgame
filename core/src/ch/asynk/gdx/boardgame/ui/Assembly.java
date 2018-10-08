package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.utils.IterableSet;

public abstract class Assembly extends Element
{
    private IterableSet<Element> children;
    private Element touched;

    public Assembly(int c)
    {
        this.children = new IterableSet<Element>(c);
    }

    public void add(Element e)
    {
        if (children.add(e)) {
            e.setParent(this);
        }
    }

    public void remove(Element e)
    {
        if (children.remove(e)) {
            e.setParent(null);
        }
    }

    public Element touched()
    {
        return touched;
    }

    @Override public boolean touch(float x, float y)
    {
        for (Element e : children)
            if (e.touch(x, y)) {
                touched = e;
                return true;
            }
        touched = null;
        return false;
    }

    @Override public void taint()
    {
        children.forEach( c -> c.taint() );
    }

    @Override public void draw(Batch batch)
    {
        if (tainted) computeGeometry();
        children.forEach( c -> c.draw(batch) );
    }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
        children.forEach( c -> c.drawDebug(debugShapes) );
    }
}
