package ch.asynk.gdx.boardgame.tilestorages;

import java.util.Map;
import java.util.HashMap;

import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.TileStorage;
import ch.asynk.gdx.boardgame.TileStorage.TileFactory;

public class HashTileStorage implements TileStorage
{
    private Map<Integer, Tile> tiles;

    public HashTileStorage(int x, int y)
    {
        this.tiles = new HashMap<Integer, Tile>();
    }

    @Override public Tile getTile(int x, int y, int k, TileFactory tileFactory)
    {
        Tile tile = tiles.get(k);
        if (tile == null) {
            tile = tileFactory.buildTile(x, y);
            tiles.put(k, tile);
        }
        return tile;
    }

    Tile get(int x, int y, TileFactory tileFactory)
    {
        return getTile(x, y, hash(x, y), tileFactory);
    }

    public int hash(int x, int y)
    {
        int k = 0;
        if (x > 0 ) {
            k = (x & 0x0fff);
        } else {
            k = ((-x & 0x0fff) | 0x8000);
        }
        k = k << 16;
        if (y > 0 ) {
            k = k | (y & 0x0fff);
        } else {
            k = k | ((-y & 0x0fff) | 0x8000);
        }
        return k;
    }
}
