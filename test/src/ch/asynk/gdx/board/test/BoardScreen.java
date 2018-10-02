package ch.asynk.gdx.tabletop.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.gdx.tabletop.Camera;
import ch.asynk.gdx.tabletop.Board;
import ch.asynk.gdx.tabletop.board.BoardFactory;
import ch.asynk.gdx.tabletop.ui.Button;

public class BoardScreen implements Screen
{
    private static final String DOM = "BoardScreen";
    private static final float INPUT_DELAY = 0.1f;              // filter out touches after gesture
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;

    private static final boolean DEBUG = true;

    private class MyBoard
    {
        private final Assets assets;
        private final Texture sherman;
        private final Vector2 v;
        public Texture map;
        public Board board;
        public int dx;
        public int dy;
        public int w;
        public int h;
        public float r;

        public MyBoard(final Assets assets)
        {
            this.assets = assets;
            this.sherman = assets.getTexture(assets.SHERMAN);
            this.v = new Vector2();
        }

        public void draw(SpriteBatch batch)
        {
            batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
            batch.draw(sherman, v.x - (sherman.getWidth() / 2), v.y - (sherman.getHeight() / 2));
        }

        public void reset()
        {
            board.centerOf(0, 0, v);
        }

        public boolean touch(float x, float y)
        {
            board.toBoard(x, y, v);
            GdxBoardTest.debug("BoardGame", String.format("touchDown [%d;%d] => [%d;%d]", (int)x, (int)y, (int)v.x, (int)v.y));
            board.centerOf((int)v.x, (int)v.y, v);
            GdxBoardTest.debug("BoardGame", String.format("                  => [%d;%d]", (int)v.x, (int)v.y));
            return true;
        }

        public void setHEX_V()
        {
            map = assets.getTexture(assets.MAP_00);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
        }

        public void setHEX_H()
        {
            map = assets.getTexture(assets.MAP_00);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL);
        }

        public void setSQUARE()
        {
            map = assets.getTexture(assets.CHESS);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(BoardFactory.BoardType.SQUARE, 83, 5, 5);
        }

        public void setTRI_H()
        {
            map = assets.getTexture(assets.TRI);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(BoardFactory.BoardType.TRIANGLE, 150, 109, 53, BoardFactory.BoardOrientation.HORIZONTAL);
        }

        public void setTRI_V()
        {
            map = assets.getTexture(assets.TRI);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(BoardFactory.BoardType.TRIANGLE, 150, 16, 110, BoardFactory.BoardOrientation.VERTICAL);
        }
    }

    private final GdxBoardTest app;
    private final MyBoard board;
    private final Camera camera;
    private final SpriteBatch batch;
    private final Button btn;

    private final Vector2 dragPos = new Vector2();
    private final Vector3 boardTouch = new Vector3();
    private final Vector3 hudTouch = new Vector3();

    private boolean paused;
    private float inputDelay;
    private boolean inputBlocked;

    public enum State
    {
        HEX_V, HEX_H, SQUARE, TRI_H, TRI_V, DONE;
        public State next()
        {
            switch(this) {
                case HEX_V:
                    return HEX_H;
                case HEX_H:
                    return SQUARE;
                case SQUARE:
                    return TRI_H;
                case TRI_H:
                    return TRI_V;
                case TRI_V:
                    return DONE;
                default:
                    return HEX_V;
            }
        }
    }
    private State state;

    public BoardScreen(final GdxBoardTest app)
    {
        this.app = app;
        this.board = new MyBoard(app.assets);
        this.batch = new SpriteBatch();
        this.camera = new Camera(10, board.w, board.h, 1.0f, 0.3f, false);
        this.btn = new Button(
                app.assets.getFont(app.assets.FONT_25),
                app.assets.getNinePatch(app.assets.PATCH, 23, 23, 23 ,23),
                15);
        this.btn.write("next");
        Gdx.input.setInputProcessor(getMultiplexer(this));
        setState(State.HEX_V);
        this.inputBlocked = false;
        this.inputDelay = 0f;
        this.paused = false;
    }

    @Override public void render(float delta)
    {
        if (paused) return;

        if (inputBlocked) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                inputBlocked = false;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.applyMapViewport();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        board.draw(batch);
        batch.end();

        camera.applyHudViewport();
        batch.setProjectionMatrix(camera.getHudMatrix());
        batch.begin();
        btn.draw(batch);
        batch.end();
    }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug("BoardScrean", String.format("resize (%d,%d)",width, height));
        camera.updateViewport(width, height);
    }

    @Override public void dispose()
    {
        GdxBoardTest.debug("BoardScrean", "dispose()");
        batch.dispose();
    }

    @Override public void show()
    {
        GdxBoardTest.debug("BoardScrean", "show()");
        paused = false;
    }

    @Override public void hide()
    {
        GdxBoardTest.debug("BoardScrean", "hide()");
        paused = true;
    }

    @Override public void pause()
    {
        GdxBoardTest.debug("pause() ");
        paused = true;
    }

    @Override public void resume()
    {
        GdxBoardTest.debug("resume() ");
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        paused = false;
    }

    private void zoom(float dz)
    {
        camera.zoom(dz);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void nextState()
    {
        setState(this.state.next());
    }

    private void setState(State state)
    {
        switch (state) {
            case HEX_V:
                board.setHEX_V();
                break;
            case HEX_H:
                board.setHEX_H();
                break;
            case SQUARE:
                board.setSQUARE();
                break;
            case TRI_H:
                board.setTRI_H();
                break;
            case TRI_V:
                board.setTRI_V();
                break;
            case DONE:
                this.app.switchToMenu();
                return;
        }
        board.reset();
        this.camera.setDimension(board.w, board.h);
        zoom(1);
        this.state = state;
    }

    private InputMultiplexer getMultiplexer(final BoardScreen screen)
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override public boolean scrolled(int amount)
            {
                zoom(amount * ZOOM_SCROLL_FACTOR);
                return true;
            }
            @Override public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (inputBlocked) return true;
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    camera.unproject(x, y, boardTouch);
                    camera.unprojectHud(x, y, hudTouch);
                    if (btn.touch(hudTouch.x, hudTouch.y)) {
                        nextState();
                    } else {
                        board.touch(boardTouch.x, boardTouch.y);
                    }
                }
                return true;
            }
            @Override public boolean touchDragged(int x, int y, int pointer)
            {
                int dx = (int) (dragPos.x - x);
                int dy = (int) (dragPos.y - y);
                dragPos.set(x, y);
                camera.translate(dx, dy);
                return true;
            }
        });
        multiplexer.addProcessor(new GestureDetector(new GestureAdapter() {
            @Override public boolean zoom(float initialDistance, float distance)
            {
                if (initialDistance > distance)
                    screen.zoom(ZOOM_GESTURE_FACTOR);
                else
                    screen.zoom(-ZOOM_GESTURE_FACTOR);
                inputBlocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        }));

        return multiplexer;
    }
}
