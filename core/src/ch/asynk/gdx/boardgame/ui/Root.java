package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.math.Rectangle;

public class Root extends Assembly
{
    public Root(int c)
    {
        super(c);
        this.alignment = Alignment.ABSOLUTE;
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
}
