package ch.asynk.gdx.boardgame.boards;

import ch.asynk.gdx.boardgame.Orientation;

public class BoardFactory
{
    public enum BoardType
    {
        HEX, SQUARE, TRIANGLE
    }

    public enum BoardOrientation
    {
        VERTICAL,
        HORIZONTAL,
    }

    public static Board getBoard(int cols, int rows, BoardType boardType, float side)
    {
        return getBoard(cols, rows, boardType, side, 0f, 0f, BoardOrientation.VERTICAL);
    }

    public static Board getBoard(int cols, int rows, BoardType boardType, float side, float x0, float y0)
    {
        return getBoard(cols, rows, boardType, side, x0, y0, BoardOrientation.VERTICAL);
    }

    public static Board getBoard(int cols, int rows, BoardType boardType, float side, float x0, float y0, BoardOrientation boardOrientation)
    {
        Board board = null;
        switch(boardType)
        {
            case HEX:
                board = new HexBoard(cols, rows, side, x0, y0, boardOrientation);
                break;
            case SQUARE:
                board = new SquareBoard(cols, rows, side, x0, y0);
                break;
            case TRIANGLE:
                board = new TriangleBoard(cols, rows, side, x0, y0, boardOrientation);
                break;
        }
        if (board == null) {
            throw new RuntimeException(boardType + " board type is not implemented yet.");
        }
        Orientation.setValues(board.getAngles());

        return board;
    }
}
