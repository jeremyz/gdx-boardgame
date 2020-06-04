package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;

public class Scrollable extends Element
{
    private Element child;
    private int scissorIdx;
    public boolean vScroll;
    public boolean hScroll;

    public Scrollable(Element child)
    {
        super();
        this.vScroll = true;
        this.hScroll = false;
        this.scissorIdx = -1;
        this.child = child;
        child.setParent(this);
    }

    public Element getChild() { return child; }

    public float getBestWidth()
    {
        return (child.getWidth() + (2 * padding));
    }

    public float getBestHeight()
    {
        return (child.getHeight() + (2 * padding));
    }

    public void computeDimensions()
    {
        child.computeDimensions();
    }

    @Override public void computeGeometry(Rectangle area, boolean resized)
    {
        if (resized || scissorIdx == -1)
            setPosition(area.x, area.y, area.width, area.height);
        child.computeGeometry(innerRect, resized);
        clear();
    }


    @Override public Element touch(float x, float y)
    {
        if (innerRect.contains(x, y)) {
            final Element touched = child.touch(x, y);
            if (touched == child)
                return this;
            return touched;
        }
        return null;
    }

    @Override public boolean drag(float x, float y, int dx, int dy)
    {
        if (touch(x, y) == null) return false;
        float tx = 0;
        float ty = 0;
        if (vScroll) {
            if (dy < 0 && child.rect.y < innerRect.y) {
                ty = Math.min(-dy, innerRect.y - child.rect.y);
            } else if (dy > 0) {
                float cly = innerRect.y + innerRect.height;
                float chy = child.rect.y + child.rect.height;
                if (chy > cly)
                    ty = -Math.min(dy, chy - cly);
            }
        }
        if (hScroll) {
            if (dx > 0 && child.rect.x < innerRect.x) {
                tx = Math.min(dx, innerRect.x - child.rect.x);
            }
            else if (dx < 0) {
                float clx = innerRect.x + innerRect.width;
                float chx = child.rect.x + child.rect.width;
                if (chx > clx)
                    tx = Math.max(dx, clx - chx);
            }
        }
        child.translate(tx, ty);
        return true;
    }

    @Override public void setPosition(float x, float y, float w, float h)
    {
        rect.set(x, y, w, h);
        innerRect.set((x + padding), (y + padding), (w - 2 * padding), (h - 2 * padding));
        scissorIdx = Scissors.compute(scissorIdx, innerRect);
        child.setPositionClear(0, - (child.rect.height - innerRect.height), innerRect);
    }

    @Override public void drawReal(Batch batch)
    {
        batch.flush();
        Rectangle scissor = Scissors.get(scissorIdx, innerRect);
        HdpiUtils.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        child.draw(batch);
        batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }
}
