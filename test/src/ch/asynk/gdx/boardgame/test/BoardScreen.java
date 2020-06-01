package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Camera;
import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.Piece;
import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.tilestorages.TileStorage;
import ch.asynk.gdx.boardgame.tilestorages.ArrayTileStorage;
import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.boards.BoardFactory;
import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.ui.Button;
import ch.asynk.gdx.boardgame.ui.Root;
import ch.asynk.gdx.boardgame.utils.IterableSet;

public class BoardScreen extends AbstractScreen
{
    private class MyBoard
    {
        private final IterableSet<Tile> tilesToDraw;
        private final Assets assets;
        private final Piece panzer;
        private final Vector2 pos;
        private final Vector2 v;
        private boolean dragging;
        public Texture map;
        public Board board;
        public TileStorage tileStorage;
        public int dx;
        public int dy;
        public int w;
        public int h;
        public float r;

        public MyBoard(final Assets assets)
        {
            this.dragging = false;
            this.assets = assets;
            this.pos = new Vector2();
            this.v = new Vector2();
            Piece.angleCorrection = 90;
            this.panzer = new Piece(assets.getTexture(assets.PANZER));
            this.tilesToDraw = new IterableSet<Tile>(10);
            Tile.defaultOverlay = assets.getAtlas(app.assets.HEX_OVERLAYS);
        }

        public void draw(SpriteBatch batch)
        {
            batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
            for (Tile tile : tilesToDraw) {
                tile.draw(batch);
            }
            panzer.draw(batch);
        }

        public void reset()
        {
            tilesToDraw.clear();
            v.set(0, 0);
            pos.set(0, 0);
            handleAdjacents();
            board.centerOf(0, 0, v);
            panzer.centerOn(v.x, v.y);
            panzer.setRotation(Orientation.DEFAULT.r());
        }

        public boolean touch(float x, float y, boolean down)
        {
            board.toBoard(x, y, v);
            if (!board.isOnMap((int)v.x, (int)v.y))
                return false;
            if (down) {
                Tile tile = getTile((int)v.x, (int)v.y);
                if (!dragging && panzer.isOn(tile)) {
                    dragging = true;
                    clearAdjacents();
                } else {
                    touchInfo(x, y);
                    pos.set(v);
                    handleAdjacents();
                    board.centerOf((int)v.x, (int)v.y, v);
                    panzer.centerOn(v.x, v.y);
                    panzer.setRotation(Orientation.fromR(panzer.getRotation()).left().r());
                    GdxBoardTest.debug("BoardScreen", String.format("                  => [%d;%d]", (int)v.x, (int)v.y));
                }
            } else {
                if (dragging) {
                    touchInfo(x, y);
                    handleAdjacents();
                    panzer.dropOnBoard(board, v);
                    GdxBoardTest.debug("BoardScreen", String.format("                  => [%d;%d]", (int)v.x, (int)v.y));
                    dragging = false;
                }
            }
            return true;
        }

        private void touchInfo(float x, float y)
        {
            GdxBoardTest.debug("BoardScreen", String.format("touchDown [%d;%d] => [%d;%d] => %d", (int)x, (int)y, (int)v.x, (int)v.y, board.genKey((int)v.x, (int)v.y)));
            float d0 = board.distance((int)pos.x, (int)pos.y, (int)v.x, (int)v.y, Board.Geometry.TCHEBYCHEV);
            float d1 = board.distance((int)pos.x, (int)pos.y, (int)v.x, (int)v.y, Board.Geometry.TAXICAB);
            float d2 = board.distance((int)pos.x, (int)pos.y, (int)v.x, (int)v.y, Board.Geometry.EUCLIDEAN);
            GdxBoardTest.debug("BoardScreen", String.format("     from [%d;%d] => %d :: %d :: %f", (int)pos.x, (int)pos.y, (int)d0, (int)d1, d2));
        }

        public boolean drag(float dx, float dy)
        {
            if (dragging) {
                panzer.translate(dx, dy);
                return true;
            }
            return false;
        }

        private void clearAdjacents()
        {
            for (Tile tile : board.getAdjacents()) {
                if (tile != null)
                    tile.enableOverlay(12, false);
            }
        }

        private void handleAdjacents()
        {
            clearAdjacents();
            board.buildAdjacents((int)v.x, (int)v.y);
            for (Tile tile : board.getAdjacents()) {
                if (tile != null) {
                    tilesToDraw.add(tile);
                    tile.enableOverlay(12, true);
                }
            }
            for (Tile tile : tilesToDraw) {
                if (!tile.overlaysEnabled()) {
                    tilesToDraw.remove(tile);
                }
            }
        }

        private Tile getTile(int x, int y)
        {
            return tileStorage.getTile(x, y, board::genKey, this::buildTile);
        }

        private Tile buildTile(int x, int y)
        {
            final Vector2 v = new Vector2();
            board.centerOf(x, y, v);
            return new Tile(v.x, v.y);
        }

        public void setHEX_V()
        {
            map = assets.getTexture(assets.MAP_00);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(10, 9, BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL, this::getTile);
            tileStorage = new ArrayTileStorage(board.size());
        }

        public void setHEX_H()
        {
            map = assets.getTexture(assets.MAP_00);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(9, 10, BoardFactory.BoardType.HEX, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL, this::getTile);
            tileStorage = new ArrayTileStorage(board.size());
        }

        public void setSQUARE()
        {
            map = assets.getTexture(assets.CHESS);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(8, 8, BoardFactory.BoardType.SQUARE, 83, 5, 5, this::getTile);
            tileStorage = new ArrayTileStorage(board.size());
        }

        public void setTRI_H()
        {
            map = assets.getTexture(assets.TRI);
            r = 0;
            dx = 0;
            dy = 0;
            w = map.getWidth();
            h = map.getHeight();
            board = BoardFactory.getBoard(21, 8, BoardFactory.BoardType.TRIANGLE, 150, 109, 53, BoardFactory.BoardOrientation.HORIZONTAL, this::getTile);
            tileStorage = new ArrayTileStorage(board.size());
        }

        public void setTRI_V()
        {
            map = assets.getTexture(assets.TRI);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(8, 21, BoardFactory.BoardType.TRIANGLE, 150, 16, 110, BoardFactory.BoardOrientation.VERTICAL, this::getTile);
            tileStorage = new ArrayTileStorage(board.size());
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
    private final Vector2 relative;

    public BoardScreen(final GdxBoardTest app)
    {
        super(app, "");
        this.relative = new Vector2();
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
        cam.unprojectTranslation(dx, dy, relative);
        if (!board.drag(relative.x, relative.y)) {
            cam.translate(dx, dy);
        }
    }

    @Override protected void onTouch(int x, int y, boolean down)
    {
        if (down) {
            cam.unproject(x, y, boardTouch);
            cam.unprojectHud(x, y, hudTouch);
            if (btn.touch(hudTouch.x, hudTouch.y) != null) {
                setState(state.next());
            } else {
                board.touch(boardTouch.x, boardTouch.y, true);
            }
        } else {
            cam.unproject(x, y, boardTouch);
            board.touch(boardTouch.x, boardTouch.y, false);
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
