package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Piece extends Sprite implements Drawable, Positionable, Rotable, Scalable
{
    public Piece(Texture texture)
    {
        super(texture);
    }

    @Override public float getScale()
    {
        return getScaleX();
    }

    public void getPosOn(Tile tile, Orientation orientation, Vector3 v)
    {
        v.set((tile.x - (getWidth() / 2f)), (tile.y - (getHeight() / 2f)), orientation.r());
    }

    public void setPosition(float x, float y, float r)
    {
        setPosition(x, y);
        setRotation(r);
    }

    public boolean isOn(Tile tile)
    {
        return (
                (Math.abs(getX() - (tile.x - (getWidth() / 2f))) < 3) &&
                (Math.abs(getY() - (tile.y - (getHeight() / 2f))) < 3)
                );
    }

    public boolean isFacing(Orientation orientation)
    {
        return (Orientation.fromR(getRotation()) == orientation);
    }

    public void getFireingPoint(Vector2 v, Piece target)
    {
        float x0 = getX();
        float y0 = getY();
        float x1 = target.getX();
        float y1 = target.getY();

        float r = MathUtils.atan2(y1 - y0, x1 - x0);
        x0 += (Math.cos(r) + 1f) * (getWidth() / 2f);
        y0 += (Math.sin(r) + 1f) * (getHeight() / 2f);

        v.set(x0, y0);
    }

    public void getImpactPoint(Vector2 v)
    {
        v.set(getX()+ (getWidth() / 2f), getY() + (getHeight() / 2f));
    }
}
