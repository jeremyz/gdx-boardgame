package ch.asynk.gdx.boardgame;

public interface Positionable
{
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public void setPosition(float x, float y, float w, float h);
}
