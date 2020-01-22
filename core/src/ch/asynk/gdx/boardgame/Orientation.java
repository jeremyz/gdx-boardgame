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
        DEFAULT.r = angles[0];
        E.r  = angles[1];
        NE.r = angles[2];
        N.r  = angles[3];
        NW.r = angles[4];
        W.r  = angles[5];
        SW.r = angles[6];
        S.r  = angles[7];
        SE.r = angles[8];
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
             if ( E.r != -1 && (r > ( E.r - delta)) && (r < ( E.r + delta))) return  E;
        else if (NE.r != -1 && (r > (NE.r - delta)) && (r < (NE.r + delta))) return NE;
        else if ( N.r != -1 && (r > ( N.r - delta)) && (r < ( N.r + delta))) return  N;
        else if (NW.r != -1 && (r > (NW.r - delta)) && (r < (NW.r + delta))) return NW;
        else if ( W.r != -1 && (r > ( W.r - delta)) && (r < ( W.r + delta))) return  W;
        else if (SW.r != -1 && (r > (SW.r - delta)) && (r < (SW.r + delta))) return SW;
        else if ( S.r != -1 && (r > ( S.r - delta)) && (r < ( S.r + delta))) return  S;
        else if (SE.r != -1 && (r > (SE.r - delta)) && (r < (SE.r + delta))) return SE;
        else return KEEP;

    }

    public static Orientation fromTiles(Tile from, Tile to)
    {
        return fromR(MathUtils.atan2((to.y - from.y), (to.x - from.x)) * MathUtils.radiansToDegrees);
    }
}
