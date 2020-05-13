package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.utils.IterableSet;

public class Container extends Element
{
    public static int DEFAULT_CHILD_COUNT = 2;

    protected IterableSet<Element> children;

    protected Container()
    {
        super();
        this.children = null;
    }

    public void add(Element e)
    {
        if (children == null)
            children = new IterableSet<Element>(DEFAULT_CHILD_COUNT);
        if (children.add(e)) {
            e.setParent(this);
        }
    }

    public void remove(Element e)
    {
        if (children != null) {
            if (children.remove(e)) {
                e.setParent(null);
            }
        }
    }

    public void taintChildren()
    {
        if (children != null)
            children.forEach( c -> c.taint() );
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        if (tainted) computeGeometry();
        if (children != null)
            children.forEach( c -> c.draw(batch) );
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        if (children != null)
            children.forEach( c -> c.drawDebug(shapeRenderer) );
    }

    @Override public Element touch(float x, float y)
    {
        if (super.touch(x, y) != null) {
            if (children != null) {
                for (Element e : children) {
                    final Element t = e.touch(x, y);
                    if (t != null)
                        return t;
                }
            }
            return this;
        }
        return null;
    }
}

