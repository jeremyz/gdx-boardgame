package ch.asynk.zproject;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.zproject.engine.Touchable;

public class GameBoard implements Disposable, Touchable
{
    private final Texture map;

    public GameBoard(final Assets assets)
    {
        this.map = assets.getTexture(assets.MAP_00);
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

    public int getWidth()
    {
        return map.getWidth();
    }

    public int getHeight()
    {
        return map.getHeight();
    }

    public void draw(Batch batch)
    {
        batch.draw(map, 0, 0);
    }
}
