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
    private Rectangle clip;
    public boolean vScroll;
    public boolean hScroll;

    public Scrollable(Element child)
    {
        super();
        this.vScroll = true;
        this.hScroll = false;
        this.child = child;
        this.scissorIdx = -1;
        this.clip = new Rectangle();
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

    @Override public void computeDimensions()
    {
        child.computeDimensions();
    }

    @Override public void computePosition()
    {
        child.computePosition();
    }

    @Override public Element touch(float x, float y)
    {
        if (clip.contains(x, y)) {
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
            if (dy > 0 && child.rect.y < clip.y) {
                ty = Math.min(dy, clip.y - child.rect.y);
            }
            else if (dy < 0) {
                float cly = clip.y + clip.height;
                float chy = child.rect.y + child.rect.height;
                if (chy > cly)
                    ty = Math.max(dy, cly - chy);
            }
        }
        if (hScroll) {
            if (dx > 0 && child.rect.x < clip.x) {
                tx = Math.min(dx, clip.x - child.rect.x);
            }
            else if (dx < 0) {
                float clx = clip.x + clip.width;
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
        clip.set((x + padding), (y + padding), (w - 2 * padding), (h - 2 * padding));
        child.setPosition(clip.x, clip.y - (child.rect.height - clip.height));
        scissorIdx = Scissors.compute(scissorIdx, clip);
    }

    @Override public void draw(Batch batch)
    {
        if (!visible) return;
        batch.flush();
        Rectangle scissor = Scissors.get(scissorIdx, clip);
        HdpiUtils.glScissor((int)scissor.x, (int)scissor.y, (int)scissor.width, (int)scissor.height);
        Gdx.gl.glEnable(GL20.GL_SCISSOR_TEST);
        child.draw(batch);
        batch.flush();
        Gdx.gl.glDisable(GL20.GL_SCISSOR_TEST);
    }
}
