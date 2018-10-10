package ch.asynk.gdx.boardgame;

public enum Orientation
{
    ALL(255,    0),
    KEEP( 0,    0),
    DEFAULT(0, -1),
    N(   1,    -1),
    NW(  2,    -1),
    W(   4,    -1),
    SW(  8,    -1),
    S(  16,    -1),
    SE( 32,    -1),
    E(  64,    -1),
    NE(128,    -1);
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
        N.r  = angles[1];
        NW.r = angles[2];
        W.r  = angles[3];
        SW.r = angles[4];
        S.r  = angles[5];
        SE.r = angles[6];
        E.r  = angles[7];
        NE.r = angles[8];
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
        Orientation o = (this == NE) ? N : fromS(this.s << 1);
        return (o.r == -1) ? o.left() : o;
    }

    public Orientation right()
    {
        Orientation o = (this == N) ? NE : fromS(this.s >> 1);
        return (o.r == -1) ? o.right() : o;
    }

    public static Orientation fromS(int s)
    {
        if (s == N.s) return N;
        else if (s == NW.s) return NW;
        else if (s == W.s) return W;
        else if (s == SW.s) return SW;
        else if (s == S.s) return S;
        else if (s == SE.s) return SE;
        else if (s == E.s) return E;
        else if (s == NE.s) return NE;
        else return KEEP;
    }

    public static Orientation fromR(float r)
    {
        if (r < 0) r += 360f;
        if ((r > (N.r - delta)) && (r < (N.r + delta))) return N;
        else if ((r > (NW.r - delta)) && (r < (NW.r + delta))) return NW;
        else if ((r > (W.r - delta)) && (r < (W.r + delta))) return W;
        else if ((r > (SW.r - delta)) && (r < (SW.r + delta))) return SW;
        else if ((r > (S.r - delta)) && (r < (S.r + delta))) return S;
        else if ((r > (SE.r - delta)) && (r < (SE.r + delta))) return SE;
        else if ((r > (E.r - delta)) && (r < (E.r + delta))) return E;
        else if ((r > (NE.r - delta)) && (r < (NE.r + delta))) return NE;
        else return KEEP;

    }
}
