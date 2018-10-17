package ch.asynk.gdx.boardgame;

public class Tile
{
    public float x;
    public float y;

    public Tile(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override public String toString()
    {
        return String.format("[%4d, %4d]", (int)x, (int)y);
    }
}
