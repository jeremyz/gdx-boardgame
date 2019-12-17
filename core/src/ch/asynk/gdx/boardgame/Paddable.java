package ch.asynk.gdx.boardgame;

public interface Paddable
{
    public float getInnerX();
    public float getInnerY();
    public float getInnerWidth();
    public float getInnerHeight();
    public float getInnerTop();
    public float getInnerRight();
    public void setPadding(float padding);
    public void setPosition(float x, float y, float w, float h);
}

