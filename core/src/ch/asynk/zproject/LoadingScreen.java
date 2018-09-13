package ch.asynk.zproject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

import ch.asynk.zproject.ZProject;

public class LoadingScreen implements Screen
{
    private final ZProject zproject;
    private final SpriteBatch batch;
    private final Color c;
    private final OrthographicCamera camera;

    private final float BLINK_AFTER = 2f;
    private final float ALPHA_FACTOR = 1.5f;

    private boolean paused;
    private  TextureAtlas atlas;
    private  AtlasRegion bar;
    private  AtlasRegion border;
    private  AtlasRegion loading;
    private  AtlasRegion loaded;

    private int x, y;
    private int count;
    private float percent;
    private float alpha;
    private boolean incr;
    private boolean isLoaded;

    public interface LoadAction {
        void call();
    }
    private LoadAction onLoaded;

    public LoadingScreen(final ZProject zproject, LoadAction startLoading, LoadAction onLoaded)
    {
        this.zproject = zproject;
        this.onLoaded = onLoaded;
        this.batch = new SpriteBatch();
        this.c = batch.getColor();
        this.camera = new OrthographicCamera();
        int w = Gdx.graphics.getWidth();
        int h = Gdx.graphics.getHeight();
        this.camera.setToOrtho(false, w, h);
        this.camera.update();
        this.paused = false;
        atlas = zproject.assets.getAtlas(zproject.assets.LOADING);
        bar = atlas.findRegion("bar");
        border = atlas.findRegion("border");
        loading = atlas.findRegion("loading");
        loaded = atlas.findRegion("loaded");
        computeCoords(w, h);
        percent = 0f;
        alpha = 1f;
        incr = false;
        isLoaded = false;
        startLoading.call();
    }

    private void computeCoords(int width, int height)
    {
        x = (width - border.getRegionWidth()) / 2;
        y = (height - border.getRegionHeight()) / 2;
    }

    @Override public void render(float delta)
    {
        if (paused) return;

        if (!isLoaded) {
            if (zproject.assets.update()) {
                ZProject.debug("LoadingScreen", "assets loaded");
                isLoaded = true;
                percent = 1f;
            } else {
                percent = zproject.assets.getProgress();
            }
        }

        if (!isLoaded && percent >= 1f) {
            count = 0;
            alpha = 1f;
            incr = false;
        }

        delta *= ALPHA_FACTOR;
        if (incr) {
            alpha += delta;
            if (alpha >= 1f ) {
                alpha = 1f;
                incr = false;
                if (isLoaded) count += 1;
                if (count >= BLINK_AFTER) {
                    onLoaded.call();
                    return;
                }
            }
        } else {
            alpha -= delta;
            if (alpha <= 0f ) {
                alpha = 0f;
                incr = true;
            }
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.setColor(c.r, c.g, c.b, 1f);
        batch.draw(border, x, y);
        batch.draw(bar, x + 4, y + 4, percent * bar.getRegionWidth(), bar.getRegionHeight());
        batch.setColor(c.r, c.g, c.b, alpha);
        if (!isLoaded) {
            batch.draw(loading, x, y + border.getRegionHeight() + 3);
        } else {
            batch.draw(loaded, x + border.getRegionWidth() - loaded.getRegionWidth(), y - loaded.getRegionHeight() - 3);
        }
        batch.end();
    }

    @Override public void resize(int width, int height)
    {
        if (paused) return;
        ZProject.debug("LoadingScreen", String.format("resize (%d,%d)",width, height));
        this.camera.setToOrtho(false, width, height);
        this.camera.update();
        computeCoords(width, height);
    }

    @Override public void dispose()
    {
        ZProject.debug("LoadingScreen", "dispose()");
        batch.dispose();
    }

    @Override public void show()
    {
        ZProject.debug("LoadingScreen", "show()");
    }

    @Override public void hide()
    {
        ZProject.debug("LoadingScreen", "hide()");
    }

    @Override public void pause()
    {
        paused = true;
        ZProject.debug("pause() ");
    }

    @Override public void resume()
    {
        ZProject.debug("resume() ");
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        paused = false;
    }
}
