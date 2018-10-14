package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

public class SquareBoard implements Board
{
    private final float side;   // length of the side of a square
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset

    // [0] is 0° facing East
    private static final int [] angles = {90, 0, -1, 90, -1, 180, -1, 270, -1, 0};

    public SquareBoard(float side, float x0, float y0)
    {
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
    }

    @Override public int[] getAngles()
    {
        return angles;
    }

    @Override public void centerOf(int x, int y, Vector2 v)
    {
        float cx = this.x0 + (this.side / 2) + (this.side * x);
        float cy = this.y0 + (this.side / 2) + (this.side * y);

        v.set(cx, cy);
    }

    @Override public void toBoard(float x, float y, Vector2 v)
    {
        float dx = x - this.x0;
        float dy = y - this.y0;
        int col = (int) (dx / this.side);
        int row = (int) (dy / this.side);
        if (dx < 0) col -=1;
        if (dy < 0) row -=1;

        v.set(col, row);
    }
}
