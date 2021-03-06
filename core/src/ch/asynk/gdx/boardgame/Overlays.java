package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.Positionable;
import ch.asynk.gdx.boardgame.Rotable;
import ch.asynk.gdx.boardgame.Scalable;

public class Overlays implements Drawable, Positionable, Rotable, Scalable
{
    private boolean isEnabled;
    private boolean enabled[];
    private Array<Sprite> sprites;

    public Overlays(TextureAtlas atlas)
    {
        this.sprites = atlas.createSprites();
        this.enabled = new boolean[sprites.size];
        this.isEnabled = false;
    }

    public void disableAll()
    {
        for (int i = 0; i < sprites.size; i++)
            enabled[i] = false;
        isEnabled = false;
    }

    public void enable(int i, boolean enable)
    {
        enabled[i] = enable;
        for (int j = 0; j < sprites.size; j++) {
            if (enabled[j]) {
                isEnabled = true;
                return;
            }
        }
        isEnabled = false;
    }

    public boolean isEnabled(int i)
    {
        return enabled[i];
    }

    public boolean isEnabled()
    {
        return isEnabled;
    }

    @Override public float getX() { return sprites.get(0).getX(); }
    @Override public float getY() { return sprites.get(0).getY(); }
    @Override public float getWidth() { return sprites.get(0).getWidth(); }
    @Override public float getHeight() { return sprites.get(0).getHeight(); }

    @Override public void translate(float dx, float dy)
    {
        for (Sprite sprite : sprites) {
            sprite.translate(dx, dy);
        }
    }

    @Override public void centerOn(float cx, float cy)
    {
        for (Sprite sprite : sprites) {
            sprite.setPosition(
                    (cx - (sprite.getWidth() / 2f)),
                    (cy - (sprite.getHeight() / 2f)));
        }
    }

    @Override public void setPosition(float x, float y)
    {
        for (Sprite sprite : sprites) {
            sprite.setPosition(x, y);
        }
    }

    @Override public float getRotation()
    {
        return sprites.get(0).getRotation();
    }

    @Override public void setRotation(float rotation)
    {
        for (Sprite sprite : sprites) {
            sprite.setRotation(rotation);
        }
    }

    public void setRotation(int idx, float rotation)
    {
        sprites.get(idx).setRotation(rotation);
    }

    @Override public float getScale()
    {
        return sprites.get(0).getScaleX();
    }

    @Override public void setScale(float scale)
    {
        for (Sprite sprite : sprites) {
            sprite.setScale(scale);
        }
    }

    @Override public void setAlpha(float alpha)
    {
        for (Sprite sprite : sprites) {
            sprite.setAlpha(alpha);
        }
    }

    @Override public void draw(Batch batch)
    {
        if (!isEnabled) return;
        for (int i = 0, n = sprites.size; i < n; i++) {
            if (enabled[i]) {
                sprites.get(i).draw(batch);
            }
        }
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        if (!isEnabled) return;
        for (int i = 0, n = sprites.size; i < n; i++) {
            if (enabled[i]) {
                final Sprite sprite = sprites.get(i);
                float w = sprite.getWidth();
                float h = sprite.getHeight();
                shapeRenderer.rect(sprite.getX(), sprite.getY(), (w / 2f), (h / 2f), w, h, sprite.getScaleX(), sprite.getScaleY(), sprite.getRotation());
                return;
            }
        }
    }
}
