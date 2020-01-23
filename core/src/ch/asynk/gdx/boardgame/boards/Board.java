package ch.asynk.gdx.boardgame.boards;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage.TileProvider;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage.TileKeyGenerator;

public interface Board extends TileKeyGenerator
{
    public int size();
    public int[] getAngles();

    public boolean isOnMap(int x, int y);
    public void centerOf(int x, int y, Vector2 v);
    public void toBoard(float x, float y, Vector2 v);

    public Tile[] getAdjacents();
    public void buildAdjacents(int x, int y, TileProvider tileProvider);

    enum Geometry
    {
        EUCLIDEAN,
        TAXICAB,
        TCHEBYCHEV
    }
    public float distance(int x0, int y0, int x1, int y1, Geometry geometry);
}
