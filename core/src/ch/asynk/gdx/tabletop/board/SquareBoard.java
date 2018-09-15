package ch.asynk.gdx.tabletop.board;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.tabletop.Board;

public class SquareBoard implements Board
{
    private final float side;   // length of the side of a square
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset

    public SquareBoard(float side, float x0, float y0)
    {
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
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
