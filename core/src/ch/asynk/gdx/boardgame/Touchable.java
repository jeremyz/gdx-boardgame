package ch.asynk.gdx.boardgame;

public interface Touchable
{
    public boolean touch(float x, float y);
    public boolean drag(float x, float y, int dx, int dy);
}
