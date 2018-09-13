package ch.asynk.zproject;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class ZProject extends Game
{
    public static final String DOM = "ZProject";

    private enum State
    {
        NONE,
        LOADING,
        GAME,
    }
    private State state;

    public final Assets assets = new Assets();

    @Override public void create()
    {
        this.state = State.NONE;
        Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);
        debug(String.format("create() [%d;%d] %f", Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Gdx.graphics.getDensity()));
        switchToLoading();
    }

    @Override public void dispose()
    {
        switchToNone();
        assets.dispose();
    }

    public static void error(String msg)
    {
        Gdx.app.error(DOM, msg);
    }

    public static void debug(String msg)
    {
        Gdx.app.debug(DOM, msg);
    }

    public static void debug(String from, String msg)
    {
        Gdx.app.debug(DOM, String.format("%s : %s", from, msg));
    }

    private void switchTo(Screen nextScreen, State nextState)
    {
        if (state == nextState) {
            error("switch from and to " + state);
            return;
        }
        switch(state) {
            case LOADING: assets.unloadLoading(); break;
            case GAME: assets.unloadGame(); break;
        }
        if (state != State.NONE) {
            getScreen().dispose();
        }
        setScreen(nextScreen);
        this.state = nextState;
    }

    public void switchToNone()
    {
        switchTo(null, State.NONE);
    }

    public void switchToLoading()
    {
        assets.loadLoading();
        assets.finishLoading();
        switchTo(new LoadingScreen(this, () -> assets.loadGame(), () -> switchToGame()), State.LOADING);
    }

    public void switchToGame()
    {
        switchTo(new GameScreen(this), State.GAME);
    }
}
