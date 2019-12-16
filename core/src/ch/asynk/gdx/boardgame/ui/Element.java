package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.Paddable;
import ch.asynk.gdx.boardgame.Positionable;
import ch.asynk.gdx.boardgame.Touchable;

public abstract class Element implements Drawable, Paddable, Positionable, Touchable
{
    public boolean blocked;
    public boolean visible;
    protected float padding;
    protected Element parent;
    protected Alignment alignment;
    protected Rectangle rect;           // outer drawing coordinates
    protected float x, y;               // given position
    protected boolean tainted;          // geometry must be computed
    protected boolean taintParent;      // propagate tainted state up the tree

    protected Element()
    {
        this.blocked = false;
        this.visible = true;
        this.padding = 0;
        this.parent = null;
        this.alignment = alignment.RELATIVE;
        this.rect = new Rectangle(0, 0, 0, 0);
        this.x = this.y = 0;
        this.tainted = true;
        this.taintParent= false;
    }

    @Override public final float getX()        { return rect.x; }
    @Override public final float getY()        { return rect.y; }
    @Override public final float getWidth()    { return rect.width; }
    @Override public final float getHeight()   { return rect.height; }

    @Override public final float getInnerX()              { return rect.x + padding; }
    @Override public final float getInnerY()              { return rect.y + padding; }
    @Override public final float getInnerWidth()          { return rect.width - 2 * padding; }
    @Override public final float getInnerHeight()         { return rect.height - 2 * padding; }

    @Override public void drawDebug(ShapeRenderer debugShapes)
    {
        debugShapes.rect(getX(), getY(), getWidth(), getHeight());
    }

    @Override public boolean touch(float x, float y)
    {
        if (blocked || !visible) return false;
        return rect.contains(x, y);
    }

    public void taint()
    {
        this.tainted = true;
        if (parent != null && taintParent) parent.taint();
    }

    @Override public void setPosition(float x, float y, float w, float h)
    {
        this.x = x;
        this.y = y;
        this.rect.width = w;
        this.rect.height = h;
        taint();
        // rect.(x,y) will be set in computeGeometry
    }

    public void setParent(Element parent)
    {
        this.parent = parent;
        taint();
    }

    @Override public void setPadding(float padding)
    {
        this.padding = padding;
        taint();
    }

    public void setAlignment(Alignment alignment)
    {
        this.alignment = alignment;
        taint();
    }

    @Override public final void centerOn(float cx, float cy)
    {
        setPosition((cx - (rect.width / 2f)), (cy - (rect.height / 2f)));
    }

    @Override public final void translate(float dx, float dy)
    {
        setPosition(x + dx, y + dy);
    }

    public final void setPosition(Rectangle r)
    {
        setPosition(r.x, r.y, r.width, r.height);
    }

    @Override public final void setPosition(float x, float y)
    {
       setPosition(x, y, rect.width, rect.height);
    }

    protected void computeGeometry()
    {
        if (alignment == Alignment.ABSOLUTE || parent == null) {
            rect.x = x;
            rect.y = y;
        } else if (alignment == Alignment.RELATIVE) {
            rect.x = x + parent.getInnerX();
            rect.y = y + parent.getInnerX();
        } else {
            rect.x = x + alignment.getX(parent, rect.width);
            rect.y = y + alignment.getY(parent, rect.height);
        }
        this.tainted = false;
        // System.err.println(String.format("%s : %s", this, rect));
    }
}
