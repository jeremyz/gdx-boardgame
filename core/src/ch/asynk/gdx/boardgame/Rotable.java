package ch.asynk.gdx.boardgame;

public interface Rotable
{
    public float getRotation();
    public void setRotation(float rotation);
    default public void rotate(float r) { setRotation(getRotation() + r); }
}
