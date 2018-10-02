package ch.asynk.gdx.tabletop.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.gdx.tabletop.ui.Alignment;
import ch.asynk.gdx.tabletop.ui.Button;
import ch.asynk.gdx.tabletop.ui.Root;

public class UiScreen implements Screen
{
    private static final String DOM = "UiScreen";

    private final GdxBoardTest app;
    private final OrthographicCamera cam;
    private final SpriteBatch batch;
    private final Texture bg;
    private final Root root;
    private final Button hello;
    private boolean paused;

    private final float WORLD_RATIO = 0.5f;
    private final float PADDING = 15f;

    private final Vector3 touch = new Vector3();

    public enum State
    {
        POSITIONS, DONE;
        public State next()
        {
            switch(this) {
                case POSITIONS:
                    return DONE;
                default:
                    return POSITIONS;
            }
        }
    }
    private State state;

    public UiScreen(final GdxBoardTest app)
    {
        this.app = app;
        this.batch = new SpriteBatch();
        this.bg = app.assets.getTexture(app.assets.MAP_00);

        final NinePatch patch = app.assets.getNinePatch(app.assets.PATCH, 23, 23, 23 ,23);
        final BitmapFont font = app.assets.getFont(app.assets.FONT_25);

        this.root = new Root(1);
        this.hello = new Button(font, patch, 10, 15);
        this.hello.write("Hello");
        this.root.add(this.hello);

        this.cam = new OrthographicCamera(bg.getWidth() * WORLD_RATIO, bg.getHeight() * WORLD_RATIO);
        this.cam.position.set(bg.getWidth() / 2f, bg.getHeight() / 2f, 0);
        this.cam.update();

        Gdx.input.setInputProcessor(getMultiplexer());
        this.paused = false;
        setState(State.POSITIONS);
    }

    @Override public void render(float delta)
    {
        if (paused) return;

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        draw(batch);
        batch.end();
    }

    private void draw(Batch batch)
    {
        batch.draw(bg, 0, 0);
        switch (state) {
            case POSITIONS:
                drawButtons(batch);
                break;
        }
    }

    private void drawButtons(Batch batch)
    {
        hello.write("hello");
        hello.setAlignment(Alignment.TOP_LEFT);
        hello.setLabelAlignment(Alignment.BOTTOM_RIGHT);
        root.draw(batch);
        drawHello(batch, Alignment.TOP_CENTER, Alignment.BOTTOM_CENTER);
        drawHello(batch, Alignment.TOP_RIGHT, Alignment.BOTTOM_LEFT);
        drawHello(batch, Alignment.MIDDLE_LEFT, Alignment.MIDDLE_RIGHT);
        drawHello(batch, Alignment.MIDDLE_RIGHT, Alignment.MIDDLE_LEFT);
        drawHello(batch, Alignment.BOTTOM_LEFT, Alignment.TOP_RIGHT);
        drawHello(batch, Alignment.BOTTOM_CENTER, Alignment.TOP_CENTER);
        drawHello(batch, Alignment.BOTTOM_RIGHT, Alignment.TOP_LEFT);
        hello.write("next");
        drawHello(batch, Alignment.MIDDLE_CENTER, Alignment.MIDDLE_CENTER);
    }

    private void drawHello(Batch batch, Alignment alignment1, Alignment alignment2)
    {
        hello.setAlignment(alignment1);
        hello.setLabelAlignment(alignment2);
        hello.draw(batch);
    }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug(DOM, String.format("resize (%d,%d)",width, height));
        if (width >= height) {
            cam.viewportWidth = bg.getWidth();
            cam.viewportHeight = bg.getHeight() / (float)width * (float)height;
        } else {
            cam.viewportHeight = bg.getHeight();
            cam.viewportWidth = bg.getWidth() / (float)height * (float)width;
        }
        cam.viewportWidth *= WORLD_RATIO;
        cam.viewportHeight *= WORLD_RATIO;
        cam.update();
        root.resize(
                cam.position.x - (cam.viewportWidth / 2f) + PADDING,
                cam.position.y - (cam.viewportHeight / 2f) + PADDING,
                cam.viewportWidth - 2 * PADDING,
                cam.viewportHeight - 2 * PADDING
                );
    }

    @Override public void dispose()
    {
        GdxBoardTest.debug(DOM, "dispose()");
        batch.dispose();
    }

    @Override public void show()
    {
        GdxBoardTest.debug(DOM, "show()");
        paused = false;
    }

    @Override public void hide()
    {
        GdxBoardTest.debug(DOM, "hide()");
        paused = true;
    }

    @Override public void pause()
    {
        GdxBoardTest.debug(DOM, "pause() ");
        paused = true;
    }

    @Override public void resume()
    {
        GdxBoardTest.debug(DOM, "resume() ");
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        paused = false;
    }

    private void touch()
    {
        if (root.touch(touch.x, touch.y)) {
            setState(state.next());
        }
    }

    private InputMultiplexer getMultiplexer()
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    touch.set(x, y, 0);
                    cam.unproject(touch);
                    touch();
                }
                return true;
            }
        });

        return multiplexer;
    }

    private void setState(State state)
    {
        switch (state) {
            case DONE:
                app.switchToMenu();
        }
        this.state = state;
    }
}
