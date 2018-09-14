package ch.asynk.gdx.board.board;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.board.Board;

public class TriangleBoard implements Board
{
    private final float side;   // length of the side of the equilateral triangle
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset
    private final BoardFactory.BoardOrientation orientation;

    private final float d;      // side / 2
    private final float h;      // height of the triangle
    private final float m;      // h / d

    public TriangleBoard(float side, float x0, float y0, BoardFactory.BoardOrientation boardOrientation)
    {
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
        this.orientation = boardOrientation;

        this.d = side / 2f;
        this.h = side * 0.866f;
        this.m = this.h / this.d;
    }

    @Override public void centerOf(int x, int y, Vector2 v)
    {
        float cx = this.x0;
        float cy = this.y0;

        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            cy += (y * this.d);
            cx += ((this.h * ( (y % 2 == 0) ? 0.33333f : 0.66666f )) + (x * this.h));
        } else {
            cx += (this.d + (x * this.d));
            cy += ((this.h * ( (x % 2 == 0) ? 0.33333f : 0.66666f )) + (y * this.h));
        }

        v.set(cx, cy);
    }

    @Override public void toBoard(float x, float y, Vector2 v)
    {
        boolean vert = (this.orientation == BoardFactory.BoardOrientation.VERTICAL);

        float dx = x - this.x0;
        float dy = y - this.y0;
        float cx = (vert ? this.h : this.d);
        float cy = (vert ? this.d : this.h);

        int col = (int) (dx / cx);
        int row = (int) (dy / cy);
        if (dx < 0) col -=1;
        if (dy < 0) row -=1;
        dx -= (col * cx);
        dy -= (row * cy);

        if (vert) {
            if (col % 2 == 0) {
                if (row % 2 == 0) {
                    if (dy > (dx / this.m))
                        row += 1;
                } else {
                    if (dy + (dx / this.m) > d )
                        row += 1;
                }
            } else {
                if (row % 2 == 0) {
                    if (dy + (dx / this.m) > d )
                        row += 1;
                } else {
                    if (dy > (dx / this.m))
                        row += 1;
                }
            }
        } else {
            if (row % 2 == 0) {
                if (col % 2 == 0) {
                    if (dy > (dx * this.m))
                        col -= 1;
                } else {
                    if (dy + (dx * this.m) < h )
                        col -= 1;
                }
            } else {
                if (col % 2 == 0) {
                    if (dy + (dx * this.m) < h )
                        col -= 1;
                } else {
                    if (dy > (dx * this.m))
                        col -= 1;
                }
            }
        }

        v.set(col, row);
    }
}
