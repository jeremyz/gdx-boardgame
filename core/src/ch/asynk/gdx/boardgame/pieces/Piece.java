package ch.asynk.gdx.boardgame.pieces;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import ch.asynk.gdx.boardgame.Drawable;
import ch.asynk.gdx.boardgame.Positionable;
import ch.asynk.gdx.boardgame.Rotable;
import ch.asynk.gdx.boardgame.Scalable;

public class Piece extends Sprite implements Drawable, Positionable, Rotable, Scalable
{
    public Piece(Texture texture)
    {
        super(texture);
    }

    @Override public void centerOn(float cx, float cy)
    {
        setPosition((cx - (getWidth() / 2f)), (cy - (getHeight() / 2f)));
    }
}
