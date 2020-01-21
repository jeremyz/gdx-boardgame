package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

public interface Board
{
    public int[] getAngles();
    public int size();
    public int getIdx(int x, int y);
    public boolean isOnMap(int x, int y);
    public void centerOf(int x, int y, Vector2 v);
    public void toBoard(float x, float y, Vector2 v);
    public float distance(int x0, int y0, int x1, int y1, Geometry geometry);

    enum Geometry
    {
        EUCLIDEAN,
        TAXICAB,
        TCHEBYCHEV
    }
}
