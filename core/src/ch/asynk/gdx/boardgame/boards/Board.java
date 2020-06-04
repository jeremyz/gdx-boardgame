package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Piece;
import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage.TileKeyGenerator;
import ch.asynk.gdx.boardgame.utils.Collection;

public interface Board extends TileKeyGenerator
{
    public int size();
    public int[] getAngles();
    public Tile getTile(int x, int y);

    public boolean isOnMap(int x, int y);
    public void centerOf(int x, int y, Vector2 v);
    public void toBoard(float x, float y, Vector2 v);

    public Tile[] getAdjacents();
    public void buildAdjacents(int x, int y);

    public boolean lineOfSight(int x0, int y0, int x1, int y1, Collection<Tile> tiles);

    default public boolean lineOfSight(Tile from, Tile  to, Collection<Tile> tiles)
    {
        return lineOfSight(from.x, from.y, to.x, to.y, tiles);
    }

    enum Geometry
    {
        EUCLIDEAN,
        TAXICAB,
        TCHEBYCHEV
    }
    public float distance(int x0, int y0, int x1, int y1, Geometry geometry);

    default public float distance(int x0, int y0, int x1, int y1)
    {
        return distance(x0, y0, x1, y1, Geometry.EUCLIDEAN);
    }

    default public void dropInPlace(Piece piece, Vector2 v)
    {
        toBoard(piece.getCX(), piece.getCY(), v);
        centerOf((int)v.x, (int)v.y, v);
        piece.centerOn(v.x, v.y);
    }
}
