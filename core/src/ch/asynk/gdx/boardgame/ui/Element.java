package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.Paddable;
import ch.asynk.gdx.boardgame.Positionable;
import ch.asynk.gdx.boardgame.Touchable;

public abstract class Element implements Drawable, Paddable, Positionable, Touchable
{
    public static boolean DEBUG_GEOMETRY = false;

    public boolean blocked;
    public boolean visible;
    protected float padding;
    protected Element parent;
    protected Sizing sizing;            // sizing policy
    protected Alignment alignment;      // where to position itself
    protected Rectangle rect;           // outer drawing coordinates
    protected float x, y;               // given position
    protected boolean dirty;            // geometry must be computed

    protected Element()
    {
        this.blocked = false;
        this.visible = true;
        this.padding = 0;
        this.parent = null;
        this.sizing = Sizing.NONE;
        this.alignment = alignment.RELATIVE;
        this.rect = new Rectangle(0, 0, 0, 0);
        this.x = this.y = 0;
        taint();
    }

    @Override public final float getX()         { return rect.x; }
    @Override public final float getY()         { return rect.y; }
    @Override public final float getWidth()     { return rect.width; }
    @Override public final float getHeight()    { return rect.height; }

    @Override public final float getInnerX()            { return rect.x + padding; }
    @Override public final float getInnerY()            { return rect.y + padding; }
    @Override public final float getInnerWidth()        { return rect.width - 2 * padding; }
    @Override public final float getInnerHeight()       { return rect.height - 2 * padding; }
    @Override public final float getInnerTop()          { return rect.y + rect.height - padding; }
    @Override public final float getInnerRight()        { return rect.x + rect.width - padding; }

    @Override public String toString()
    {
        return print(0);
    }

    protected String print(int level)
    {
        String suffix = "";
        for (int i = 0; i < level; i++)
            suffix += " ";

        String r = suffix;
        r += " : " + (int)x + " " + (int)y +
            " [" + (int)rect.x + " " + (int)rect.y + " " + (int)rect.width + " " + (int)rect.height + "] +" +
            (int)padding + " " + alignment + " " + sizing + " "+ getClass().getName() + " " + Integer.toHexString(hashCode());
        if (level < 0)
            return r;
        if (parent != null)
            r +=  "\n" + parent.print(level + 1);
        else
            r += "\n" + suffix + " *";
        return r;
    }

    protected void print(String msg)
    {
        System.err.println(msg + " : " + getClass().getName() + " " + Integer.toHexString(hashCode()));
    }

    public void clear()
    {
        if (DEBUG_GEOMETRY) print(" clear");
        this.dirty = false;
    }

    public void mark()
    {
        if (DEBUG_GEOMETRY) print("  mark");
        this.dirty = true;
    }

    public void drip()
    {
        if (DEBUG_GEOMETRY) print("  drip");
        this.dirty = true;
    }

    public void taint()
    {
        if (DEBUG_GEOMETRY) print(" taint");
        mark();
        if (parent != null) parent.mark();
    }

    @Override public void setPosition(float x, float y, float w, float h)
    {
        this.x = x;
        this.y = y;
        this.rect.width = w;
        this.rect.height = h;
        taint();
    }

    public final void setPositionClear(float x, float y)
    {
        this.x = x;
        this.y = y;
        computePosition();
        clear();
    }

    public void setParent(Element parent)
    {
        setParent(parent, null);
    }

    public void setParent(Element parent, Alignment alignment)
    {
        this.parent = parent;
        if (alignment != null) this.alignment = alignment;
        taint();
    }

    @Override public void setPadding(float padding)
    {
        this.padding = padding;
        taint();
    }

    public void setSizing(Sizing sizing)
    {
        this.sizing = sizing;
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

    public void computeDimensions()
    {
        if (parent != null && sizing.fill()) {
            if (sizing.fillWidth()) {
                rect.width = parent.getInnerWidth();
            }
            if (sizing.fillHeight()) {
                rect.height = parent.getInnerHeight();
            }
        }
        // it is up to the subclass to implement the expand logic
        if (DEBUG_GEOMETRY) System.err.println("  dim " + print(-1));
    }

    public void computePosition()
    {
        if (alignment == Alignment.ABSOLUTE || parent == null) {
            rect.x = x;
            rect.y = y;
        } else if (alignment == Alignment.RELATIVE) {
            rect.x = x + parent.getInnerX();
            rect.y = y + parent.getInnerY();
        } else {
            rect.x = x + alignment.getX(parent, rect.width);
            rect.y = y + alignment.getY(parent, rect.height);
        }
        if (DEBUG_GEOMETRY) System.err.println("  pos " + print(-1));
    }

    public abstract void drawReal(Batch batch);

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        if (dirty) {
            if (DEBUG_GEOMETRY) { print("[Geometry"); System.err.println("  --> " + print(-1)); }
            computeDimensions();
            computePosition();
            clear();
            if (DEBUG_GEOMETRY) System.err.println("Geometry]");
        }
        drawReal(batch);
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
    }

    @Override public Element touch(float x, float y)
    {
        if (!blocked && visible && rect.contains(x, y)) {
            return this;
        }
        return null;
    }

    @Override public boolean drag(float x, float y, int dx, int dy)
    {
        if (blocked || !visible) return false;
        return rect.contains(x, y);
    }
}
