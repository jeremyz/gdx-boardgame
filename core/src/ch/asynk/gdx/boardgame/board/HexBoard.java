package ch.asynk.gdx.boardgame.board;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Board;

public class HexBoard implements Board
{
    private final float side;   // length of the side of the hex
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset
    private final BoardFactory.BoardOrientation orientation;

    private final float w;      // side to side orthogonal distance
    private final float dw;     // half hex : w/2
    private final float dh;     // hex top : s/2
    private final float h;      // square height : s + dh
    private final float slope;  // dh / dw

    //  BoardOrientation.VERTICAL : 2 vertical sides : 2 vertices pointing up and down
    //  coordinates
    //  \
    //   \___
    //   cols are horizontal
    //   rows are at -120°
    //   bottom left is the bottom vertice of the most bottom-left vertical hex side of the map
    //
    //  BoardOrientation.HORIZONTAL : 2 horizontal sides : 2 vertices pointing left and right
    //  coordinates
    //  |
    //  |
    //   \
    //    \
    //   cols are at +120°
    //   rows are vertical°
    //   bottom left is the left vertice of the most bottom-left horizontal hex side of the map

    public HexBoard(float side, float x0, float y0, BoardFactory.BoardOrientation boardOrientation)
    {
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
        this.orientation = boardOrientation;

        this.w  = side * 1.73205f;
        this.dw = w / 2.0f;
        this.dh = side / 2.0f;
        this.h  = side + dh;
        this.slope = dh / dw;
    }

    @Override public void centerOf(int x, int y, Vector2 v)
    {
        float cx = this.x0;
        float cy = this.y0;

        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            cx += (this.dw + (x * this.w) - (y * this.dw));
            cy += (this.dh + (y * this.h));
        } else {
            cx += (this.dh + (x * this.h));
            cy += (this.dw + (y * this.w) - (x * this.dw));
        }

        v.set(cx, cy);
    }

    @Override public void toBoard(float x, float y, Vector2 v)
    {
        int col = -1;
        int row = -1;

        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            // compute row
            float dy = y - this.y0;
            row = (int) (dy / this.h);
            if (dy < 0f) row -= 1;

            // compute col
            float dx = x - this.x0 + (row * this.dw);
            col = (int) (dx / this.w);
            if (dx < 0f) col -= 1;

            // upper rectangle or hex body
            if (dy > ((row * this.h) + this.side)) {
                dy -= ((row * this.h) + this.side);
                dx -= (col * this.w);
                // upper left or right rectangle
                if (dx < this.dw) {
                    if (dy > (dx * this.slope)) {
                        // upper left hex
                        row += 1;
                    }
                } else {
                    // if (dy > ((2 * this.dh) - (dx * this.slope))) {
                    if (dy > ((this.w - dx) * this.slope)) {
                        // upper right hex
                        row += 1;
                        col += 1;
                    }
                }
            }
        } else {
            // compute col
            float dx = x - this.x0;
            col = (int) (dx / this.h);
            if (dx < 0f) col -= 1;

            // compute row
            float dy = y - this.y0 + (col * this.dw);
            row = (int) (dy / this.w);
            if (dy < 0f) row -= 1;

            // right rectangle or hex body
            if (dx > ((col * this.h) + this.side)) {
                dx -= ((col * this.h) + this.side);
                dy -= (row * this.w);
                // upper or lower rectangle
                if (dy > ((this.dw - dx) / this.slope)) {
                    if (dy > ((2 * this.dw) - (dx / this.slope))) {
                        // upper right hex
                        col += 1;
                        row += 1;
                    }
                } else {
                    if (dy < (dx / this.slope)) {
                        // lower right hex
                        col += 1;
                    }
                }
            }
        }

        v.set(col, row);
    }
}
