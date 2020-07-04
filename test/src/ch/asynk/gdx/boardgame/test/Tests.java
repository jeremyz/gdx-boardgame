package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage;
import ch.asynk.gdx.boardgame.tilestorages.ArrayTileStorage;
import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.boards.BoardFactory;

public class Tests
{
    private Board board;
    private TileStorage tileStorage;
    private Vector2 v;

    public Tests()
    {
        v = new Vector2();
    }

    public void run ()
    {
        System.err.println("Run tests ...");
        runHexVertical();
        runHexHorizontal();
        System.err.println("done.");
    }

    private void runHexVertical()
    {
        System.err.println(" * Vertical HexBoard");
        board = BoardFactory.getBoard(10, 9, BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL, this::getTile);
        tileStorage = new ArrayTileStorage(board.size());
        check((board.size() == 86), "Vertical ", "size", board.size());
        testCenter(5, 3, 812, 653);
        testCenter(5, 2, 907, 488);
        testCenter(6, 3, 1002, 653);
        testTouch(899, 602, 5, 3, 812, 653, 32);
        testTouch(906, 593, 5, 2, 907, 488, 23);
        testTouch(916, 601, 6, 3, 1002, 653, 33);
        testTouch(1768, 205, 9, 0, 1859, 158, 9);
        testTouch(1781, 218, 9, 0, 1859, 158, 9);
        testTouch(1851, 258, 9, 0, 1859, 158, 9);
        testTouch(1874, 257, 9, 0, 1859, 158, 9);
        testTouch(1939, 218, 9, 0, 1859, 158, 9);
        testTouch(1950, 201, 9, 0, 1859, 158, 9);
        testTouch(1951, 111, 9, 0, 1859, 158, 9);
        testTouch(1940, 100, 9, 0, 1859, 158, 9);
        testTouch(1868, 59, 9, 0, 1859, 158, 9);
        testTouch(1851, 59, 9, 0, 1859, 158, 9);
        testTouch(1779, 99, 9, 0, 1859, 158, 9);
        testTouch(1768, 115, 9, 0, 1859, 158, 9);
    }

    private void runHexHorizontal()
    {
        System.err.println(" * Horizontal HexBoard");
        board = BoardFactory.getBoard(9, 10, BoardFactory.BoardType.HEX, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL, this::getTile);
        tileStorage = new ArrayTileStorage(board.size());
        check((board.size() == 86), "Horizontal ", "size", board.size());
        testCenter(3, 7, 653, 1193);
        testCenter(3, 6, 653, 1002);
        testCenter(4, 7, 818, 1097);
        testTouch(705, 1103, 3, 7, 653, 1193, 34);
        testTouch(707, 1092, 3, 6, 653, 1002, 33);
        testTouch(715, 1096, 4, 7, 818, 1097, 43);
        testTouch(1423, 224, 8, 4, 1478, 145, 76);
        testTouch(1448, 235, 8, 4, 1478, 145, 76);
        testTouch(1516, 234, 8, 4, 1478, 145, 76);
        testTouch(1538, 218, 8, 4, 1478, 145, 76);
        testTouch(1578, 152, 8, 4, 1478, 145, 76);
        testTouch(1575, 137, 8, 4, 1478, 145, 76);
        testTouch(1537, 63, 8, 4, 1478, 145, 76);
        testTouch(1515, 56, 8, 4, 1478, 145, 76);
        testTouch(1437, 53, 8, 4, 1478, 145, 76);
        testTouch(1421, 64, 8, 4, 1478, 145, 76);
        testTouch(1380, 130, 8, 4, 1478, 145, 76);
        testTouch(1386, 161, 8, 4, 1478, 145, 76);
    }

    private void testCenter(int x, int y, int cx, int cy)
    {
        String title = String.format("[%d;%d] -> (%d;%d) ", x, y, cx, cy);
        board.centerOf(x, y, v);
        check(((int)v.x == cx), title, "cx", (int)v.x);
        check(((int)v.y == cy), title, "cy", (int)v.y);
    }

    private void testTouch(int tx, int ty, int x, int y, int cx, int cy, int g)
    {
        String title = String.format("[%d;%d] -> [%d;%d] (%d;%d) %d : ", tx, ty, x, y, cx, cy, g);
        board.toBoard(tx, ty, v);
        Tile t = board.getTile((int)v.x, (int)v.y);
        int k = board.genKey(t.x, t.y);
        check((t.x == x), title, "x", t.x);
        check((t.y == y), title, "y", t.y);
        check(((int)t.cx == cx), title, "cx", (int)t.cx);
        check(((int)t.cy == cy), title, "cy", (int)t.cy);
        check((g == k), title, "Key", k);
    }

    private Tile getTile(int x, int y, boolean isOnBoard)
    {
        if (isOnBoard)
            return tileStorage.getTile(x, y, board::genKey, this::buildTile);
        return Tile.OffMap;
    }

    private Tile buildTile(int x, int y)
    {
        final Vector2 v = new Vector2();
        board.centerOf(x, y, v);
        int k = board.genKey(x, y);
        return new Hex(x, y, v.x, v.y, k, Terrain.get(k));
    }

    private void check(boolean condition, String title, String what, int got)
    {
        if (!condition) {
            System.err.println( "  :: " + title + "failed on : " + what + " got " + got);
        }
    }
}
