package ch.asynk.gdx.boardgame.ui;

public enum Sizing
{
    NONE(0),
    // 0x02
    FILL_WIDTH(2),
    FILL_HEIGHT(3),
    FILL_BOTH(6),
    // 0x08
    EXPAND_WIDTH(8),
    EXPAND_HEIGHT(9),
    EXPAND_BOTH(24);

    private int s;
    Sizing(int s) { this.s = s; }

    public boolean fill()
    {
        return (this.s & 0x2) == 0x2;
    }

    public boolean fillWidth()
    {
        return (this == FILL_WIDTH || this == FILL_BOTH);
    }

    public boolean fillHeight()
    {
        return (this == FILL_HEIGHT || this == FILL_BOTH);
    }

    public boolean expand()
    {
        return (this.s & 0x8) == 0x8;
    }

    public boolean expandWidth()
    {
        return (this == EXPAND_WIDTH || this == EXPAND_BOTH);
    }

    public boolean expandHeight()
    {
        return (this == EXPAND_HEIGHT || this == EXPAND_BOTH);
    }
}
