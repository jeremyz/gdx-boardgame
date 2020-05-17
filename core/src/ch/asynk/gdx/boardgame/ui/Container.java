package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

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
        taint();
    }

    public void remove(Element e)
    {
        if (children != null) {
            if (children.remove(e)) {
                e.setParent(null);
            }
            taint();
        }
    }

    @Override public void computeGeometry(Rectangle area, boolean resized)
    {
        if ((dirty || damaged || resized) && children != null) {
            children.forEach( c -> c.computeGeometry(area, resized) );
            damaged = false;
        }
        if (dirty || resized) {
            super.computeGeometry(area, resized);
        }
    }

    @Override public void drawReal(Batch batch)
    {
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

