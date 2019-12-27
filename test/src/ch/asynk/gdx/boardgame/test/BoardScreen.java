package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Camera;
import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.Piece;
import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.boards.BoardFactory;
import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.ui.Button;
import ch.asynk.gdx.boardgame.ui.Root;

public class BoardScreen extends AbstractScreen
{
    private class MyBoard
    {
        private final Assets assets;
        private final Piece panzer;
        private final Vector2 pos;
        private final Vector2 v;
        public Texture map;
        public Board board;
        public int dx;
        public int dy;
        public int w;
        public int h;
        public float r;

        public MyBoard(final Assets assets)
        {
            this.assets = assets;
            this.pos = new Vector2();
            this.v = new Vector2();
            this.panzer = new Piece(assets.getTexture(assets.PANZER));
        }

        public void draw(SpriteBatch batch)
        {
            batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
            panzer.draw(batch);
        }

        public void reset()
        {
            pos.set(0, 0);
            board.centerOf(0, 0, v);
            panzer.centerOn(v.x, v.y);
            panzer.setRotation(Orientation.DEFAULT.r());
        }

        public boolean touch(float x, float y)
        {
            board.toBoard(x, y, v);
            GdxBoardTest.debug("BoardScreen", String.format("touchDown [%d;%d] => [%d;%d]", (int)x, (int)y, (int)v.x, (int)v.y));
            float d0 = board.distance((int)pos.x, (int)pos.y, (int)v.x, (int)v.y, Board.Geometry.TCHEBYCHEV);
            float d1 = board.distance((int)pos.x, (int)pos.y, (int)v.x, (int)v.y, Board.Geometry.TAXICAB);
            float d2 = board.distance((int)pos.x, (int)pos.y, (int)v.x, (int)v.y, Board.Geometry.EUCLIDEAN);
            GdxBoardTest.debug("BoardScreen", String.format("     from [%d;%d] => %d :: %d :: %f", (int)pos.x, (int)pos.y, (int)d0, (int)d1, d2));
            pos.set(v);
            board.centerOf((int)v.x, (int)v.y, v);
            panzer.centerOn(v.x, v.y);
            panzer.setRotation(Orientation.fromR(panzer.getRotation()).left().r());
            GdxBoardTest.debug("BoardScreen", String.format("                  => [%d;%d]", (int)v.x, (int)v.y));
            return true;
        }

        public void setHEX_V()
        {
            map = assets.getTexture(assets.MAP_00);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
        }

        public void setHEX_H()
        {
            map = assets.getTexture(assets.MAP_00);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL);
        }

        public void setSQUARE()
        {
            map = assets.getTexture(assets.CHESS);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(BoardFactory.BoardType.SQUARE, 83, 5, 5);
        }

        public void setTRI_H()
        {
            map = assets.getTexture(assets.TRI);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(BoardFactory.BoardType.TRIANGLE, 150, 109, 53, BoardFactory.BoardOrientation.HORIZONTAL);
        }

        public void setTRI_V()
        {
            map = assets.getTexture(assets.TRI);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(BoardFactory.BoardType.TRIANGLE, 150, 16, 110, BoardFactory.BoardOrientation.VERTICAL);
        }
    }

    public enum State
    {
        HEX_V, HEX_H, SQUARE, TRI_H, TRI_V, DONE;
        public State next()
        {
            switch(this) {
                case HEX_V:
                    return HEX_H;
                case HEX_H:
                    return SQUARE;
                case SQUARE:
                    return TRI_H;
                case TRI_H:
                    return TRI_V;
                case TRI_V:
                    return DONE;
                default:
                    return HEX_V;
            }
        }
    }
    private State state;

    private final Camera cam;
    private final MyBoard board;
    private final Button btn;
    private final Root root;

    public BoardScreen(final GdxBoardTest app)
    {
        super(app, "");
        this.board = new MyBoard(app.assets);
        this.camera = this.cam = new Camera(10, board.w, board.h, 1.0f, 0.3f, false);
        this.btn = new Button(
                app.assets.getFont(app.assets.FONT_25),
                app.assets.getNinePatch(app.assets.PATCH, 23, 23, 23 ,23),
                15);
        this.btn.setAlignment(Alignment.BOTTOM_RIGHT);
        this.btn.write("next");
        this.root = new Root(1);
        this.root.add(btn);
        this.root.setPadding(5);
        setState(State.HEX_V);
    }

    @Override protected boolean animate(float delta)
    {
        if (inputBlocked) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                inputBlocked = false;
        }
        return true;
    }

    @Override public void draw(SpriteBatch batch)
    {
        batch.end(); // for AbstractScreen

        cam.applyBoardViewport();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        board.draw(batch);
        batch.end();

        cam.applyHudViewport();
        batch.setProjectionMatrix(cam.getHudMatrix());
        batch.begin();
        root.draw(batch);
        batch.end();

        batch.begin(); // for AbstractScreen
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer) { }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug("BoardScrean", String.format("resize (%d,%d)",width, height));
        cam.updateViewport(width, height);
        root.resize(cam.getHud());
    }

    @Override protected void onDragged(int dx, int dy)
    {
        cam.translate(dx, dy);
    }

    @Override protected void onTouch(int x, int y)
    {
        cam.unproject(x, y, boardTouch);
        cam.unprojectHud(x, y, hudTouch);
        if (btn.touch(hudTouch.x, hudTouch.y) != null) {
            setState(state.next());
        } else {
            board.touch(boardTouch.x, boardTouch.y);
        }
    }

    @Override protected void onZoom(float dz)
    {
        cam.zoom(dz);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    private void setState(State state)
    {
        switch (state) {
            case HEX_V: board.setHEX_V(); break;
            case HEX_H: board.setHEX_H(); break;
            case SQUARE: board.setSQUARE(); break;
            case TRI_H: board.setTRI_H(); break;
            case TRI_V: board.setTRI_V(); break;
            case DONE:
                this.app.switchToMenu();
                return;
        }
        board.reset();
        cam.setDimension(board.w, board.h);
        onZoom(1);
        this.state = state;
    }
}
