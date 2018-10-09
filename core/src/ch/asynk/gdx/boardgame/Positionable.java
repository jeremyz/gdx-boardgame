package ch.asynk.gdx.boardgame;

public interface Positionable
{
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public void centerOn(float cx, float cy);
    public void translate(float dx, float dy);
    public void setPosition(float x, float y);
}
