package ch.asynk.zproject;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Hud implements Disposable
{
    private final Sprite hud;

    public Hud()
    {
        this.hud = new Sprite(new Texture("data/corner.png"));
    }

    @Override public void dispose()
    {
        hud.getTexture().dispose();
    }

    public void draw(SpriteBatch batch, final Rectangle rect)
    {
        float right = rect.x + rect.width - hud.getWidth();
        float top = rect.y + rect.height - hud.getHeight();
        hud.setRotation(0);
        hud.setPosition(rect.x, top);
        hud.draw(batch);
        hud.setRotation(90);
        hud.setPosition(rect.x, rect.y);
        hud.draw(batch);
        hud.setRotation(180);
        hud.setPosition(right, rect.y);
        hud.draw(batch);
        hud.setPosition(right, top);
        hud.setRotation(270);
        hud.draw(batch);
    }
}
