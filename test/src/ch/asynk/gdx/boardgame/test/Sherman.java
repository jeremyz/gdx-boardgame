package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.g2d.Sprite;

import ch.asynk.gdx.boardgame.Scalable;

public class Sherman extends Sprite implements Scalable
{
    public Sherman(final GdxBoardTest app)
    {
        super(app.assets.getTexture(app.assets.SHERMAN));
    }
}
