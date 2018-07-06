package ch.asynk.zproject;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Disposable;

public class Hud implements Disposable
{
    private final Rectangle rect;
    private final Sprite hud;
    private final GlyphLayout glyphLayout;
    private final BitmapFont font20;
    private final BitmapFont font25;

    public Hud(final Assets assets)
    {
        this.rect = new Rectangle(0, 0, 0, 0);
        this.hud = new Sprite(assets.getTexture(assets.CORNER));
        this.glyphLayout = new GlyphLayout();
        font20 = assets.getFont(assets.FONT_20);
        font25 = assets.getFont(assets.FONT_25);
    }

    @Override public void dispose()
    {
        hud.getTexture().dispose();
    }

    public void resize(float width, float height)
    {
        rect.set(0, 0, width, height);
    }

    public void draw(Batch batch)
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
        glyphLayout.setText(font20, "Hello");
        font20.draw(batch, glyphLayout, 60, 30);
        glyphLayout.setText(font25, "worlD");
        font25.draw(batch, glyphLayout, 120, 32);
    }
}
