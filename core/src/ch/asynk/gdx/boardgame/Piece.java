package ch.asynk.gdx.boardgame;

import java.lang.Math;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.Positionable;
import ch.asynk.gdx.boardgame.Rotable;
import ch.asynk.gdx.boardgame.Scalable;
import ch.asynk.gdx.boardgame.Tile;

public class Piece implements Drawable, Positionable, Rotable, Scalable
{
    public static int angleCorrection = 0;

    protected Sprite sprite;

    public Piece(Texture texture)
    {
        setHead(texture);
    }

    public int getAvailableMP()
    {
        return 0;
    }

    public int moveCost(Tile from, Tile to, Orientation orientation)
    {
        // Integer.MAX_VALUE means impracticable
        return Integer.MAX_VALUE;
    }

    public boolean atLeastOneTileMove()
    {
        return true;
    }

    public int roadMarchBonus()
    {
        return 0;
    }

    public void setHead(Texture texture)
    {
        sprite = new Sprite(texture);
    }

    public void getPosOn(Tile tile, Orientation orientation, Vector3 v)
    {
        v.set((tile.cx - (getWidth() / 2f)), (tile.cy - (getHeight() / 2f)), orientation.r());
    }

    public void setPosition(float x, float y, float r)
    {
        setPosition(x, y);
        setRotation(r);
    }

    public void dropOnBoard(Board board, Vector2 v)
    {
        board.toBoard(getCX(), getCY(), v);
        board.centerOf((int)v.x, (int)v.y, v);
        centerOn(v.x, v.y);
    }

    public boolean isOn(Tile tile)
    {
        return (
                (Math.abs(getX() - (tile.cx - (getWidth() / 2f))) < 3) &&
                (Math.abs(getY() - (tile.cy - (getHeight() / 2f))) < 3)
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

        float r = (float) (MathUtils.atan2(y1 - y0, x1 - x0));
        x0 += (Math.cos(r) + 1f) * (getWidth() / 2f);
        y0 += (Math.sin(r) + 1f) * (getHeight() / 2f);

        v.set(x0, y0);
    }

    public void getImpactPoint(Vector2 v)
    {
        v.set(getX()+ (getWidth() / 2f), getY() + (getHeight() / 2f));
    }

    @Override public float getX() { return sprite.getX(); }
    @Override public float getY() { return sprite.getY(); }
    @Override public float getWidth() { return sprite.getWidth(); }
    @Override public float getHeight() { return sprite.getHeight(); }
    @Override public void translate(float x, float y) { sprite.translate(x, y); }
    @Override public void setPosition(float x, float y) { sprite.setPosition(x, y); }


    @Override public float getScale()
    {
        return sprite.getScaleX();
    }

    @Override public void setScale(float s)
    {
        sprite.setScale(s);
    }

    @Override public float getRotation()
    {
        return sprite.getRotation() + angleCorrection;
    }

    @Override public void setRotation(float r)
    {
        sprite.setRotation(r - angleCorrection);
    }

    @Override public void draw(Batch batch)
    {
        sprite.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        float w = getWidth();
        float h = getHeight();
        shapeRenderer.rect(getX(), getY(), (w / 2f), (h / 2f), w, h, sprite.getScaleX(), sprite.getScaleY(), getRotation());
    }
}
