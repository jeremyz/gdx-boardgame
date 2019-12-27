package ch.asynk.gdx.boardgame;

import ch.asynk.gdx.boardgame.ui.Element;

public interface Touchable
{
    public Element touch(float x, float y);
    public boolean drag(float x, float y, int dx, int dy);
}
