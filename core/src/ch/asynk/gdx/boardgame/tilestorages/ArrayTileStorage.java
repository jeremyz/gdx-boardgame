package ch.asynk.gdx.boardgame.tilestorages;

import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.TileStorage;
import ch.asynk.gdx.boardgame.TileStorage.TileFactory;

public class ArrayTileStorage implements TileStorage
{
    public Tile[] tiles;

    public ArrayTileStorage(int n)
    {
        this.tiles = new Tile[n];
    }

    @Override public Tile getTile(int x, int y, int k, TileFactory tileFactory)
    {
        Tile tile = tiles[k];
        if (tile == null) {
            tile = tileFactory.buildTile(x, y);
            tiles[k] = tile;
        }
        return tile;
    }
}
