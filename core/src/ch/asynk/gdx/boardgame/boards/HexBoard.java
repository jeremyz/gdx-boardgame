package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

public class HexBoard implements Board
{
    private final float side;   // length of the side of the hex
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset
    private final BoardFactory.BoardOrientation orientation;

    private final int cols;     // # colmuns
    private final int rows;     // # rows
    private final float w;      // side to side orthogonal distance
    private final float dw;     // half hex : w/2
    private final float dh;     // hex top : s/2
    private final float h;      // square height : s + dh
    private final float slope;  // dh / dw

    private final int tl;       // tiles in 2 consecutive lines

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

    // [0] is 0° facing East
    private static final int [] vAngles = {60,  0, 60, -1, 120, 180, 240,  -1, 300};
    private static final int [] hAngles = {90, -1, 30, 90, 150,  -1, 210, 270, 330};

    public HexBoard(int cols, int rows, float side, float x0, float y0, BoardFactory.BoardOrientation boardOrientation)
    {
        this.cols = cols;
        this.rows = rows;
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
        this.orientation = boardOrientation;

        this.w  = side * 1.73205f;
        this.dw = w / 2.0f;
        this.dh = side / 2.0f;
        this.h  = side + dh;
        this.slope = dh / dw;

        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            this.tl = (2 * cols - 1);
        } else {
            this.tl = (2 * rows - 1);
        }
    }

    @Override public int size()
    {
        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            return (rows / 2) * tl + ((rows % 2) * cols);
        } else {
            return (cols / 2) * tl + ((cols % 2) * rows);
        }
    }

    @Override public int[] getAngles()
    {
        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            return vAngles;
        } else {
            return hAngles;
        }
    }

    @Override public int getIdx(int x, int y)
    {
        if (!isOnMap(x, y)) return -1;
        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            int n = y / 2;
            int i =  x - n + n * tl;
            if ((y % 2) != 0) {
                i += (cols - 1);
            }
            return i;
        } else {
            int n = x / 2;
            int i =  y - n + n * tl;
            if ((x % 2) != 0) {
                i += (rows - 1);
            }
            return i;
        }
    }

    @Override public boolean isOnMap(int x, int y)
    {
        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            if ((y < 0) || (y >= rows)) return false;
            if ((x < ((y + 1) / 2)) || (x >= (cols + (y / 2)))) return false;
        } else {
            if ((x < 0) || (x >= cols)) return false;
            if ((y < ((x + 1) / 2)) || (y >= (rows + (x / 2)))) return false;
        }
        return true;
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

    @Override public float distance(int x0, int y0, int x1, int y1, Geometry geometry)
    {
        if (geometry == Board.Geometry.EUCLIDEAN) {
            int dx = (x1 - x0);
            int dy = (y1 - y0);

            if (dx == 0) {
                return  Math.abs(dy);
            }
            else if (dy == 0 || dx == dy) {
                return  Math.abs(dx);
            }
            float fdx = dx - dy / 2f;
            float fdy = dy * 0.86602f;
            return (float)Math.sqrt((fdx * fdx) + (fdy * fdy));
        } else {
            int dx = Math.abs(x1 - x0);
            int dy = Math.abs(y1 - y0);
            int dz = Math.abs((x0 - y0) - (x1 - y1));

            if (dx > dy) {
                if (dx > dz)
                    return dx;
                else
                    return dz;
            } else {
                if (dy > dz)
                    return dy;
                else
                    return dz;
            }
        }
    }
}
