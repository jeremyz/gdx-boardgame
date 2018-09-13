package ch.asynk.gdx.board.engine.ui;

public class Root extends Assembly
{
    public Root(int c)
    {
        super(c);
        this.alignment = Alignment.ABSOLUTE;
    }

    public void resize(float width, float height)
    {
        setPosition(0, 0, width, height);
        update();
    }
}
