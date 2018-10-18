package ch.asynk.gdx.boardgame;

import java.lang.Math;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector3;

import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.Positionable;
import ch.asynk.gdx.boardgame.Rotable;
import ch.asynk.gdx.boardgame.Scalable;
import ch.asynk.gdx.boardgame.Tile;

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

    @Override public void centerOn(float cx, float cy)
    {
        setPosition((cx - (getWidth() / 2f)), (cy - (getHeight() / 2f)));
    }

    public void getPosOn(Tile tile, Orientation orientation, Vector3 v)
    {
        v.set((tile.x - (getWidth() / 2f)), (tile.y- (getHeight() / 2f)), orientation.r());
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
                (Math.abs(getY() - (tile.y- (getHeight() / 2f))) < 3)
                );
    }

    public boolean isFacing(Orientation orientation)
    {
        return (Orientation.fromR(getRotation()) == orientation);
    }
}
