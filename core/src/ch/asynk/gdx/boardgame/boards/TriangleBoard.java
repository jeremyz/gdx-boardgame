package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Piece;
import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage.TileProvider;
import ch.asynk.gdx.boardgame.utils.Collection;

public class TriangleBoard implements Board
{
    private final int cols;     // # colmuns
    private final int rows;     // # rows
    private final float side;   // length of the side of the equilateral triangle
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset
    private final BoardFactory.BoardOrientation orientation;

    private final float d;      // side / 2
    private final float h;      // height of the triangle
    private final float m;      // h / d
    private final float h13;    // 1/3 height of the triangle
    private final float h23;    // 2/3 height of the triangle

    // [0] is 0° facing East
    // [8] is default
    private static final int [] vAngles = {  0, 60, -1, 120, 180, 240,  -1, 300,  0};
    private static final int [] hAngles = { -1, 30, 90, 150,  -1, 210, 270, 330, 30};

    private final Tile[] adjacents;
    private final TileProvider tileProvider;

    public TriangleBoard(int cols, int rows, float side, float x0, float y0, BoardFactory.BoardOrientation boardOrientation, TileProvider tileProvider)
    {
        this.cols = cols;
        this.rows = rows;
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;
        this.orientation = boardOrientation;
        this.tileProvider = tileProvider;

        this.d = side / 2f;
        this.h = side * 0.866f;
        this.m = this.h / this.d;
        this.h13 = this.h * 0.33333f;
        this.h23 = this.h * 0.66666f;

        this.adjacents = new Tile[3];
        for (int i = 0; i < 3; i++)
            this.adjacents[i] = Tile.OffMap;
    }

    @Override public int size() { return cols * rows; }

    @Override public Tile getTile(int x, int y)
    {
        return tileProvider.getTile(x, y, isOnMap(x, y));
    }

    @Override public int[] getAngles()
    {
        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            return vAngles;
        } else {
            return hAngles;
        }
    }

    @Override public Tile[] getAdjacents() { return adjacents; }

    @Override public void buildAdjacents(int x, int y)
    {
        // FIXME
    }

    @Override public int genKey(int x, int y)
    {
        return (y * cols + x);
    }

    @Override public boolean isOnMap(int x, int y)
    {
        if (x < 0 || x >= cols || y < 0 || y >= rows) return false;
        return true;
    }

    @Override public void centerOf(int x, int y, Vector2 v)
    {
        float cx = this.x0;
        float cy = this.y0;

        if (this.orientation == BoardFactory.BoardOrientation.VERTICAL) {
            cy += (y * this.d);
            cx += ((x * this.h) + (((x + y) % 2 == 0) ? this.h23 : this.h13));
        } else {
            cx += (this.d + ((x -1) * this.d));
            cy += ((y * this.h) + (((x + y) % 2 == 0) ? this.h23 : this.h13));
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
            col += 1;
        }

        v.set(col, row);
    }

    @Override public float distance(int x0, int y0, int x1, int y1, Geometry geometry)
    {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        switch (geometry) {
            case EUCLIDEAN:
                return 0; // FIXME
            case TAXICAB:
                return dx + dy;
            case TCHEBYCHEV:
                return (dx > dy ? dx : dy);
        }
        return -1;
    }

    @Override public boolean lineOfSight(int x0, int y0, int x1, int y1, Collection<Tile> tiles, Vector2 v)
    {
        System.err.println("NOT implemented yet.");
        return false;
    }

    @Override public int possibleMoves(Piece piece, Tile from, Collection<Tile> tiles)
    {
        System.err.println("NOT implemented yet.");
        tiles.clear();
        return tiles.size();
    }

    @Override public int shortestPath(Piece piece, Tile from, Tile to, Collection<Tile> tiles)
    {
        System.err.println("NOT implemented yet.");
        tiles.clear();
        return tiles.size();
    }
}
