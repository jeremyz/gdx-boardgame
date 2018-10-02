package ch.asynk.gdx.tabletop.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.gdx.tabletop.ui.Alignment;
import ch.asynk.gdx.tabletop.ui.Menu;
import ch.asynk.gdx.tabletop.ui.Root;

public class MenuScreen implements Screen
{
    private static final String DOM = "MenuScreen";

    private final GdxBoardTest app;
    private final OrthographicCamera cam;
    private final SpriteBatch batch;
    private final Texture bg;
    private final Sprite corner;
    private final Root root;
    private final Menu menu;
    private boolean paused;

    private final float WORLD_RATIO = 0.5f;
    private final int PADDING = 15;

    private final Vector3 touch = new Vector3();

    public MenuScreen(final GdxBoardTest app)
    {
        this.app = app;
        this.batch = new SpriteBatch();
        final Assets assets = app.assets;
        this.bg = assets.getTexture(assets.MAP_00);
        this.corner = new Sprite(assets.getTexture(assets.CORNER));

        this.root = new Root(1);
        this.menu = new Menu(
                assets.getFont(assets.FONT_25),
                assets.getNinePatch(assets.PATCH, 23, 23, 23 ,23),
                "Menu", new String[]{"UI","Board","Exit"});
        this.menu.setAlignment(Alignment.MIDDLE_CENTER);
        this.menu.setPaddings(5, 5);
        this.menu.setSpacings(10, 5);
        this.menu.setPadding(20);
        this.menu.setLabelsOffset(10);
        this.root.add(this.menu);

        this.cam = new OrthographicCamera(bg.getWidth() * WORLD_RATIO, bg.getHeight() * WORLD_RATIO);
        this.cam.position.set(bg.getWidth() / 2f, bg.getHeight() / 2f, 0);
        this.cam.update();

        Gdx.input.setInputProcessor(getMultiplexer(this));
        this.paused = false;
    }

    @Override public void render(float delta)
    {
        if (paused) return;

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(bg, 0, 0);
        drawCorners(batch);
        root.draw(batch);
        batch.end();
    }

    private void drawCorners(SpriteBatch batch)
    {
        float right = root.getX() + root.getWidth() - corner.getWidth();
        float top = root.getY() + root.getHeight() - corner.getHeight();
        corner.setRotation(0);
        corner.setPosition(root.getX(), top);
        corner.draw(batch);
        corner.setRotation(90);
        corner.setPosition(root.getX(), root.getY());
        corner.draw(batch);
        corner.setRotation(180);
        corner.setPosition(right, root.getY());
        corner.draw(batch);
        corner.setPosition(right, top);
        corner.setRotation(270);
        corner.draw(batch);
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

    private InputMultiplexer getMultiplexer(final MenuScreen screen)
    {
        final InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(new InputAdapter() {
            @Override public boolean touchDown(int x, int y, int pointer, int button)
            {
                if (button == Input.Buttons.LEFT) {
                    touch.set(x, y, 0);
                    cam.unproject(touch);
                    if (root.touch(touch.x, touch.y)) {
                        switch(menu.touched()) {
                            case 0:
                                app.switchToUi();
                                break;
                            case 1:
                                app.switchToBoard();
                                break;
                            case 2:
                                app.switchToExit();
                                break;
                        }
                    }
                }
                return true;
            }
        });

        return multiplexer;
    }
}
