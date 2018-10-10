package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

public class TriangleBoard implements Board
{
    private final float side;   // length of the side of the equilateral triangle
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset
    private final BoardFactory.BoardOrientation orientation;

    private final float d;      // side / 2
    private final float h;      // height of the triangle
    private final float m;      // h / d
    private final float h13;    // 1/3 height of the triangle
    private final float h23;    // 2/3 height of the triangle
    private final float h43;    // 4/3 height of the triangle

    private static final int [] vAngles = {330, -1, 30, 90, 150,  -1, 210, 270, 330};
    private static final int [] hAngles = {  0,  0, 60, -1, 120, 180, 240,  -1, 300};

    public TriangleBoard(float side, float x0, float y0, BoardFactory.BoardOrientation boardOrientation)
    {
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
        this.orientation = boardOrientation;

        this.d = side / 2f;
        this.h = side * 0.866f;
        this.m = this.h / this.d;
        this.h13 = this.h * 0.33333f;
        this.h23 = this.h * 0.66666f;
        this.h43 = this.h * 1.33333f;
    }

    @Override public int[] getAngles()
    {
        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            return vAngles;
        } else {
            return hAngles;
        }
    }

    @Override public void centerOf(int x, int y, Vector2 v)
    {
        float cx = this.x0;
        float cy = this.y0;

        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            cy += (y * this.d);
            cx += ((x * this.h) + (((x + y) % 2 == 0) ? this.h23 : this.h13));
        } else {
            cx += (this.d + (x * this.d));
            cy += ((y * this.h) + (((x + y) % 2 == 0) ? this.h13 : this.h23));
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
