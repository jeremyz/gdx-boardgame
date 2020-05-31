package ch.asynk.gdx.boardgame;

public interface Positionable
{
    public float getX();
    public float getY();
    public float getWidth();
    public float getHeight();
    public void translate(float dx, float dy);
    public void setPosition(float x, float y);
    default public float getCX() { return getX() + (getWidth() / 2f); }
    default public float getCY() { return getY() + (getHeight() / 2f); }

    default public void centerOn(float cx, float cy)
    {
        setPosition((cx - (getWidth() / 2f)), (cy - (getHeight() / 2f)));
    }
}
