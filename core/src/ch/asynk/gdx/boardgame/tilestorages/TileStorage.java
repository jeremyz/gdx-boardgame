package ch.asynk.gdx.boardgame.tilestorages;

import ch.asynk.gdx.boardgame.Tile;

public interface TileStorage
{
    @FunctionalInterface
    public interface TileFactory
    {
        public Tile buildTile(int x, int y);
    }

    @FunctionalInterface
    public interface TileKeyGenerator
    {
        public int genKey(int x, int y);
    }

    @FunctionalInterface
    public interface TileProvider
    {
        public Tile getTile(int x, int y, boolean isOffMap);
    }

    Tile getTile(int x, int y, int k, TileFactory tileFactory);

    default Tile getTile(int x, int y, TileKeyGenerator tileKeyGenerator, TileFactory tileFactory)
    {
        return getTile(x, y, tileKeyGenerator.genKey(x, y), tileFactory);
    }
}
