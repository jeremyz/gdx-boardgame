package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class GdxBoardTest extends Game
{
    private static final String DOM = "GdxBoardTest";

    private enum State
    {
        NONE,
        LOADING,
        MENU,
        UI,
        BOARD,
        EXIT
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
        debug("dispose()");
        assets.clear();
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
        if (state == State.LOADING) {
            assets.unloadLoading();
        }
        if (state != State.NONE) {
            getScreen().dispose();
        }
        setScreen(nextScreen);
        this.state = nextState;
    }

    public void switchToLoading()
    {
        assets.loadLoading();
        assets.finishLoading();
        switchTo(new LoadingScreen(this, () -> assets.loadApp(), () -> switchToMenu()), State.LOADING);
    }

    public void switchToMenu()
    {
        switchTo(new MenuScreen(this), State.MENU);
    }

    public void switchToUi()
    {
        switchTo(new UiScreen(this), State.UI);
    }

    public void switchToBoard()
    {
        switchTo(new BoardScreen(this), State.BOARD);
    }

    public void switchToExit()
    {
        Gdx.app.exit();
        switchTo(null, State.EXIT);
    }
}
