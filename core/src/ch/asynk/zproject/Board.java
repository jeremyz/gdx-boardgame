package ch.asynk.zproject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Board implements Disposable
{
    private final Texture map;

    public Board()
    {
        this.map = new Texture("data/map_00.png");
    }

    @Override public void dispose()
    {
        map.dispose();
    }

    public int getWidth()
    {
        return map.getWidth();
    }

    public int getHeight()
    {
        return map.getHeight();
    }

    public void draw(SpriteBatch batch, Rectangle viewPort)
    {
        batch.draw(map, 0, 0);
    }
}
