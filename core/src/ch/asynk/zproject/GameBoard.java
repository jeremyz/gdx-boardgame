package ch.asynk.zproject;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.zproject.engine.Touchable;
import ch.asynk.zproject.engine.Board;
import ch.asynk.zproject.engine.board.BoardFactory;

public class GameBoard implements Disposable, Touchable
{
    private final Texture map;
    private Board board;

    private int dx;
    private int dy;
    private int w;
    private int h;
    private float r;

    private Vector2 v;

    public GameBoard(final Assets assets)
    {
        this.map = assets.getTexture(assets.MAP_00);
        this.v = new Vector2();
        setState(GameScreen.State.UI);
    }

    @Override public void dispose()
    {
        map.dispose();
    }

    @Override public boolean touch(float x, float y)
    {
        board.toBoard(x, y, v);
        ZProject.debug("BoardGame", String.format("touchDown [%d;%d] => [%d;%d]", (int)x, (int)y, (int)v.x, (int)v.y));
        board.centerOf((int)v.x, (int)v.y, v);
        ZProject.debug("BoardGame", String.format("                  => [%d;%d]", (int)v.x, (int)v.y));
        return true;
    }

    public void setState(GameScreen.State state)
    {
        switch (state) {
            case UI:
            case HEX_V:
                setHEX_V();
                break;
            case HEX_H:
                setHEX_H();
                break;
        }
    }

    private void setHEX_V()
    {
        r = 0;
        dx = 0;
        dy = 0;
        w = map.getWidth();
        h = map.getHeight();
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 10, 9, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
    }

    private void setHEX_H()
    {
        r = 90;
        dx = - ( map.getWidth() - map.getHeight() ) / 2;
        dy = - dx;
        w = map.getHeight();
        h = map.getWidth();
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 9, 10, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL);
    }

    public int getWidth()
    {
        return w;
    }

    public int getHeight()
    {
        return h;
    }

    public void draw(Batch batch)
    {
        batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
    }
}
