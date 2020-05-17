package ch.asynk.gdx.boardgame.ui;

public class Sizing
{
    public static final int NONE = 0;
    public static final int FILL_X = 0x1;
    public static final int FILL_Y = 0x2;
    public static final int FILL_BOTH = FILL_X | FILL_Y;
    public static final int EXPAND_X = 0x4;
    public static final int EXPAND_Y = 0x8;
    public static final int EXPAND_BOTH = EXPAND_X | EXPAND_Y;

    public static boolean contains(int v, int w)
    {
        return ((v & w) == w);
    }

    public static boolean fill(int v)
    {
        return contains(v, FILL_X) || contains(v, FILL_Y);
    }

    public static boolean expand(int v)
    {
        return contains(v, EXPAND_X) || contains(v, EXPAND_Y);
    }

    public static String print(int v)
    {
        String ret = "";
        if (v == 0)
            return "NONE";
        if (fill(v)) {
            ret += "FILL_";
            if (contains(v, FILL_BOTH))
                ret += "BOTH ";
            else if (contains(v, FILL_X))
                ret += "X ";
            else
                ret += "Y ";
        }
        if (expand(v)) {
            ret += "EXPAND_";
            if (contains(v, EXPAND_BOTH))
                ret += "BOTH ";
            else if (contains(v, EXPAND_X))
                ret += "X ";
            else
                ret += "Y ";
        }
        return ret.substring(0, ret.length() - 1);
    }
}
