package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.gdx.boardgame.ui.Root;
import ch.asynk.gdx.boardgame.ui.Scissors;

public abstract class AbstractScreen implements Screen
{
    private static final boolean DEBUG = false;

    private static final float INPUT_DELAY = 0.1f;              // filter out touches after gesture
    private static final float ZOOM_SCROLL_FACTOR = .1f;
    private static final float ZOOM_GESTURE_FACTOR = .01f;

    protected final Vector2 dragPos = new Vector2(0, 0);
    protected final Vector3 boardTouch = new Vector3(0, 0, 0);
    protected final Vector3 hudTouch = new Vector3(0, 0, 0);

    private ShapeRenderer shapeRenderer = null;

    protected final String dom;
    protected final GdxBoardTest app;
    protected final SpriteBatch batch;
    protected final Texture bg;
    protected final Root root;
    protected Camera camera;
    protected boolean inputBlocked;
    protected float inputDelay;
    protected boolean paused;

    public AbstractScreen(final GdxBoardTest app, final String dom)
    {
        this.app = app;
        this.dom = dom;
        this.batch = new SpriteBatch();
        this.bg = app.assets.getTexture(app.assets.MAP_00);
        this.root = new Root(1);
        this.root.setPadding(15);
        Gdx.input.setInputProcessor(getMultiplexer());
        this.inputBlocked = false;
        this.inputDelay = 0f;
        this.paused = false;

        if (DEBUG) this.shapeRenderer = new ShapeRenderer();

        HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    protected abstract boolean animate(float delta);
    protected abstract void draw(SpriteBatch batch);
    protected abstract void drawDebug(ShapeRenderer shapeRenderer);
    @Override public void render(float delta)
    {
        if (paused) return;

        if (!animate(delta)) return;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        draw(batch);
        batch.end();

        if (DEBUG) {
            Gdx.gl.glEnable(GL20.GL_BLEND);
            shapeRenderer.setAutoShapeType(true);
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin();
            drawDebug(shapeRenderer);
            shapeRenderer.end();
        }
    }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug(dom, String.format("resize (%d,%d)", width, height));
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        root.resize(
                camera.position.x - (camera.viewportWidth / 2f),
                camera.position.y - (camera.viewportHeight / 2f),
                camera.viewportWidth,
                camera.viewportHeight
                );
    }

    @Override public void dispose()
    {
        GdxBoardTest.debug(dom, "dispose()");
        batch.dispose();
        Scissors.clear();
    }

    @Override public void show()
    {
        GdxBoardTest.debug(dom, "show()");
        paused = false;
    }

    @Override public void hide()
    {
        GdxBoardTest.debug(dom, "hide()");
        paused = true;
    }

    @Override public void pause()
    {
        GdxBoardTest.debug(dom, "pause() ");
        paused = true;
    }

    @Override public void resume()
    {
        GdxBoardTest.debug(dom, "resume() ");
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        paused = false;
    }

    protected abstract void onTouch(int x, int y);
    protected abstract void onZoom(float dz);
    protected abstract void onDragged(int dx, int dy);
    private InputMultiplexer getMultiplexer()
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override public boolean scrolled(int amount)
            {
                onZoom(amount * ZOOM_SCROLL_FACTOR);
                return true;
            }
            @Override public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (inputBlocked) return true;
                if (button == Input.Buttons.LEFT) {
                    dragPos.set(x, y);
                    onTouch(x, y);
                }
                return true;
            }
            @Override public boolean touchDragged(int x, int y, int pointer)
            {
                int dx = (int) (x - dragPos.x);
                int dy = (int) (y - dragPos.y);
                dragPos.set(x, y);
                onDragged(dx, dy);
                return true;
            }
        });
        multiplexer.addProcessor(new GestureDetector(new GestureAdapter() {
            @Override public boolean zoom(float initialDistance, float distance)
            {
                if (initialDistance > distance)
                    onZoom(ZOOM_GESTURE_FACTOR);
                else
                    onZoom(-ZOOM_GESTURE_FACTOR);
                inputBlocked = true;
                inputDelay = INPUT_DELAY;
                return true;
            }
        }));

        return multiplexer;
    }
}
