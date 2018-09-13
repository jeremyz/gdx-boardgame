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
    private final Assets assets;
    private Texture map;
    private Board board;

    private int dx;
    private int dy;
    private int w;
    private int h;
    private float r;

    private Vector2 v;

    public GameBoard(final Assets assets)
    {
        this.assets = assets;
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
            case SQUARE:
                setSQUARE();
                break;
        }
    }

    private void setHEX_V()
    {
        this.map = assets.getTexture(assets.MAP_00);
        r = 0;
        dx = 0;
        dy = 0;
        w = this.map.getWidth();
        h = this.map.getHeight();
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
    }

    private void setHEX_H()
    {
        this.map = assets.getTexture(assets.MAP_00);
        r = 90;
        dx = - ( this.map.getWidth() - this.map.getHeight() ) / 2;
        dy = - dx;
        w = this.map.getHeight();
        h = this.map.getWidth();
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL);
    }

    private void setSQUARE()
    {
        this.map = assets.getTexture(assets.CHESS);
        r = 0;
        dx = 0;
        dy = 0;
        w = map.getHeight();
        h = map.getWidth();
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.SQUARE, 83, 5, 5);
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
