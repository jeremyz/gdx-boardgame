package ch.asynk.zproject.engine.board;

import ch.asynk.zproject.engine.Board;

public class BoardFactory
{
    public enum BoardType
    {
        HEX,
    }

    public enum BoardOrientation
    {
        VERTICAL,
        HORIZONTAL,
    }

    public static Board getBoard(BoardType boardType, int cols, int rows, float side)
    {
        return getBoard(boardType, cols, rows, side, 0f, 0f, BoardOrientation.VERTICAL);
    }

    public static Board getBoard(BoardType boardType, int cols, int rows, float side, float x0, float y0)
    {
        return getBoard(boardType, cols, rows, side, x0, y0, BoardOrientation.VERTICAL);
    }

    public static Board getBoard(BoardType boardType, int cols, int rows, float side, float x0, float y0, BoardOrientation boardOrientation)
    {
        switch(boardType)
        {
            case HEX:
                return new HexBoard(cols, rows, side, x0, y0, boardOrientation);
            default:
                throw new RuntimeException( String.format("%s board type is not implemented yet.", boardType) );
        }
    }
}
