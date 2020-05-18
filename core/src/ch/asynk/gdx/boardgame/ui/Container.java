package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.gdx.boardgame.utils.IterableSet;

public class Container extends Element
{
    public static int DEFAULT_CHILD_COUNT = 2;

    public enum Direction { FREE, HORIZONTAL, VERTICAL };

    protected float spacing;
    protected Direction direction;
    protected IterableSet<Element> children;
    private Rectangle subArea;

    public Container()
    {
        super();
        this.spacing = 0f;
        this.direction = Direction.FREE;
        this.children = null;
        this.subArea = new Rectangle();
    }

    public void setSpacing(float spacing)
    {
        this.spacing = spacing;
        taint();
    }

    public void setDirection(Direction direction)
    {
        this.direction = direction;
        taint();
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
            if (direction == Direction.HORIZONTAL) {
                // packs at the end
                int n = children.size();
                int m = 0;
                int c = 0;
                for (Element e : children) {
                    e.computeDimensions();
                    c += e.rect.width;
                    if (Sizing.contains(e.sizing, Sizing.EXPAND_X)) m += 1;
                };
                float f = (area.width - c) / m;
                subArea.set(area);
                subArea.x += subArea.width;
                subArea.width = 0;
                for (Element e : children) {
                    float available = e.rect.width;
                    if (Sizing.contains(e.sizing, Sizing.EXPAND_X))
                        available += f;
                    subArea.x -= available;
                    subArea.width = available;
                    e.computeGeometry(subArea, true);
                }
            } else if (direction == Direction.VERTICAL) {
                // packs at the begining
                int n = children.size();
                int m = 0;
                int c = 0;
                for (Element e : children) {
                    e.computeDimensions();
                    c += e.rect.height;
                    if (Sizing.contains(e.sizing, Sizing.EXPAND_Y)) m += 1;
                };
                float f = (area.height - c) / m;
                subArea.set(area);
                subArea.y += subArea.height;
                subArea.height = 0;
                for (Element e : children) {
                    float available = e.rect.height;
                    if (Sizing.contains(e.sizing, Sizing.EXPAND_Y))
                        available += f;
                    subArea.y -= available;
                    subArea.height = available;
                    e.computeGeometry(subArea, true);
                }
            } else {
                children.forEach( c -> c.computeGeometry(area, resized) );
            }
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

