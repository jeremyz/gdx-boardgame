package ch.asynk.gdx.board.board;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.board.Board;

public class SquareBoard implements Board
{
    private float side;     // length of the side of a square
    private float x0;       // bottom left x offset
    private float y0;       // bottom left y offset

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
        int col = (int) ((x - this.x0) / this.side);
        int row = (int) ((y - this.y0) / this.side);

        v.set(col, row);
    }
}
