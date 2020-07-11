package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.math.MathUtils;

public enum Orientation
{
    ALL(255,    0),
    KEEP( 0,    0),
    DEFAULT(0, -1),
    E(   1,    -1),
    NE(  2,    -1),
    N(   4,    -1),
    NW(  8,    -1),
    W(  16,    -1),
    SW( 32,    -1),
    S(  64,    -1),
    SE(128,    -1);
    private int s;
    private int r;
    Orientation(int s, int r)
    {
        this.s = s;
        this.r = r;
    }
    public int s() { return s; }
    public int r() { return r; }

    private static float delta = 5f;

    public static void setValues(int [] angles)
    {
        for (int i = 0; i < angles.length; i++) {
            if (angles[i] < 0 && angles[i] != -1)
                angles[i] += 360;
        }
        E.r  = angles[0];
        NE.r = angles[1];
        N.r  = angles[2];
        NW.r = angles[3];
        W.r  = angles[4];
        SW.r = angles[5];
        S.r  = angles[6];
        SE.r = angles[7];
        DEFAULT.r = angles[8];
    }

    public int allBut()
    {
        return ALL.s & (s ^ 0xFFFF);
    }

    public boolean belongsTo(int sides)
    {
        return ((sides & s) == s);
    }

    public Orientation left()
    {
        Orientation o = (this == SE) ? E : fromS(this.s << 1);
        return (o.r == -1) ? o.left() : o;
    }

    public Orientation right()
    {
        Orientation o = (this == E) ? SE : fromS(this.s >> 1);
        return (o.r == -1) ? o.right() : o;
    }

    public Orientation opposite()
    {
        if (s <= NW.s) return fromS(this.s << 4);
        return fromS(this.s >> 4);
    }

    public static Orientation fromS(int s)
    {
             if (s ==  E.s) return  E;
        else if (s == NE.s) return NE;
        else if (s ==  N.s) return  N;
        else if (s == NW.s) return NW;
        else if (s ==  W.s) return  W;
        else if (s == SW.s) return SW;
        else if (s ==  S.s) return  S;
        else if (s == SE.s) return SE;
        else return KEEP;
    }

    public static Orientation fromR(float r)
    {
        if (r < 0) r += 360f;
        int v = ((int)(r + 2) / 10 * 10);
        if (v == E.r) return E;
        else if (v == NE.r) return NE;
        else if (v == N.r) return N;
        else if (v == NW.r) return NW;
        else if (v == W.r) return W;
        else if (v == SW.r) return SW;
        else if (v == S.r) return S;
        else if (v == SE.r) return SE;
        return KEEP;
    }

    public static Orientation fromTiles(Tile from, Tile to)
    {
        return fromR(MathUtils.atan2((to.cy - from.cy), (to.cx - from.cx)) * MathUtils.radiansToDegrees);
    }
}
