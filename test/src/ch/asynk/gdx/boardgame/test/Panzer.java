package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.g2d.Sprite;

import ch.asynk.gdx.boardgame.Scalable;

public class Panzer extends Sprite implements Scalable
{
    public Panzer(final GdxBoardTest app)
    {
        super(app.assets.getTexture(app.assets.PANZER));
    }
}
