package ch.asynk.zproject;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.zproject.engine.Touchable;

public class GameBoard implements Disposable, Touchable
{
    private final Texture map;

    private int dx;
    private int dy;
    private int w;
    private int h;
    private float r;
    private boolean rotate = false;

    public GameBoard(final Assets assets)
    {
        this.map = assets.getTexture(assets.MAP_00);
        computeValues();
    }

    @Override public void dispose()
    {
        map.dispose();
    }

    @Override public boolean touch(float x, float y)
    {
        ZProject.debug("GameBoard", String.format("touchDown : %f %f", x, y));
        return true;
    }

    public void setRotate(boolean rotate)
    {
        this.rotate = rotate;
        computeValues();
    }

    private void computeValues()
    {
        if (rotate) {
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
        } else {
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
        }
    }

    public int getWidth()
    {
        return w;
    }

    public int getHeight()
    {
        return h;
    }

    public void draw(Batch batch)
    {
        batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
    }
}
