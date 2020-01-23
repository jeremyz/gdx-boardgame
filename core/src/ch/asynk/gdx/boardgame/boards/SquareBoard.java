package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage.TileProvider;

public class SquareBoard implements Board
{
    private final int cols;     // # colmuns
    private final int rows;     // # rows
    private final float side;   // length of the side of a square
    private final float x0;     // bottom left x offset
    private final float y0;     // bottom left y offset

    // [0] is 0° facing East
    // [8] is default
    private static final int [] angles = {0, -1, 90, -1, 180, -1, 270, -1, 90};

    private final Tile[] adjacents;

    public SquareBoard(int cols, int rows, float side, float x0, float y0)
    {
        this.cols = cols;
        this.rows = rows;
        this.side = side;
        this.x0 = x0;
        this.y0 = y0;

        this.adjacents = new Tile[8];
    }

    @Override public int size() { return cols * rows; }

    @Override public int[] getAngles() { return angles; }

    @Override public Tile[] getAdjacents() { return adjacents; }

    @Override public void buildAdjacents(int x, int y, TileProvider tileProvider)
    {
        adjacents[0] = tileProvider.getTile(x + 1, y);
        adjacents[1] = tileProvider.getTile(x + 1, y + 1);
        adjacents[2] = tileProvider.getTile(x    , y + 1);
        adjacents[3] = tileProvider.getTile(x - 1, y + 1);
        adjacents[4] = tileProvider.getTile(x - 1, y);
        adjacents[5] = tileProvider.getTile(x - 1, y - 1);
        adjacents[6] = tileProvider.getTile(x    , y - 1);
        adjacents[7] = tileProvider.getTile(x + 1, y - 1);
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

    @Override public float distance(int x0, int y0, int x1, int y1, Geometry geometry)
    {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        switch (geometry) {
            case EUCLIDEAN:
                return (float)Math.sqrt((dx * dx) + (dy * dy));
            case TAXICAB:
                return dx + dy;
            case TCHEBYCHEV:
                return (dx > dy ? dx : dy);
        }
        return -1;
    }
}
