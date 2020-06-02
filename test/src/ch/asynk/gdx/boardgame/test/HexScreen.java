package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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

public class HexScreen extends AbstractScreen
{
    private class Unit extends Piece
    {
        public boolean dragging;

        public Unit(Texture texture)
        {
            super(texture);
        }
    }

    private enum Terrain
    {
        WOODS, CITY, HILL, PLAIN;

        static private int[] cv = {23, 74};
        static private int[] hv = {68, 78, 79, 15, 45, 46};
        static private int[] wv = {20, 30, 51, 52, 62, 63, 26, 17};
        static private int[] ch = {17, 61};
        static private int[] hh = {2, 3, 11, 45, 46, 72};
        static private int[] wh = {24, 25, 32, 33, 49, 58, 64, 74};

        static public boolean v = true;

        static public Terrain get(int k)
        {
            if (v) {
                for (int i : cv) {
                    if (i == k)
                        return CITY;
                }
                for (int i : hv) {
                    if (i == k)
                        return HILL;
                }
                for (int i : wv) {
                    if (i == k)
                        return WOODS;
                }
            } else {
                for (int i : ch) {
                    if (i == k)
                        return CITY;
                }
                for (int i : hh) {
                    if (i == k)
                        return HILL;
                }
                for (int i : wh) {
                    if (i == k)
                        return WOODS;
                }
            }
            return PLAIN;
        }
    }

    private class Hex extends Tile
    {
        public final Terrain terrain;

        public Hex(int x, int y, float cx, float cy, Terrain terrain)
        {
            super(x, y, cx, cy);
            this.terrain = terrain;
        }

        @Override public boolean blockLos(final Tile from, final Tile to)
        {
            if (terrain != Terrain.PLAIN) return true;
            return false;
        }

        public String toString()
        {
            return terrain.toString() + super.toString();
        }
    }

    private class MyBoard
    {
        private final IterableSet<Tile> tilesToDraw;
        private final Vector2 v;
        private final Vector3 v3;
        private final Assets assets;
        public Texture map;
        public Board board;
        public TileStorage tileStorage;
        private Hex h0;
        private Hex h1;
        private final Unit panzer;
        private final Unit engineer;
        private final Sprite line;
        public int dx;
        public int dy;
        public int w;
        public int h;
        public float r;

        public MyBoard(final Assets assets)
        {
            this.assets = assets;
            this.v = new Vector2();
            this.v3 = new Vector3();
            Piece.angleCorrection = 90;
            this.panzer = new Unit(assets.getTexture(assets.PANZER));
            this.engineer = new Unit(assets.getTexture(assets.ENGINEER));
            this.line = new Sprite(assets.getTexture(assets.LINE));
            this.tilesToDraw = new IterableSet<Tile>(15);
            Tile.defaultOverlay = assets.getAtlas(app.assets.HEX_OVERLAYS);
        }

        public int getWidth() { return map.getWidth(); }
        public int getHeight() { return map.getHeight(); }

        public void draw(SpriteBatch batch)
        {
            batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
            for (Tile tile: tilesToDraw) {
                tile.draw(batch);
            }
            panzer.draw(batch);
            engineer.draw(batch);
            line.draw(batch);
        }

        public void reset()
        {
            tilesToDraw.clear();
            v.set(0, 0);
            h0 = getHex(0, 0);
            setUnitOn(panzer, 0, 0, Orientation.DEFAULT);
            h1 = getHex(8, 5);
            setUnitOn(engineer, 8, 5, Orientation.SW);
            board.centerOf(0, 0, v);
            updateLine();
        }

        private void setUnitOn(Unit unit, int x, int y, Orientation o)
        {
            board.centerOf(x, y, v);
            unit.centerOn(v.x, v.y);
            unit.setRotation(o.r());
        }

        public boolean touch(float x, float y, boolean down)
        {
            board.toBoard(x, y, v);
            Hex hex = getHex((int)v.x, (int)v.y);
            if (!hex.isOnMap())
                return false;
            if (down) {
                if (!panzer.dragging && panzer.isOn(hex)) {
                    panzer.dragging = true;
                } else if (!engineer.dragging && engineer.isOn(hex)) {
                    engineer.dragging = true;
                } else {
                    touchInfo(hex);
                }
            } else {
                if (panzer.dragging) {
                    touchInfo(hex);
                    board.dropInPlace(panzer, v);
                    panzer.dragging = false;
                    h0 = hex;
                    updateLine();
                } else if (engineer.dragging) {
                    touchInfo(hex);
                    engineer.dropOnBoard(board, v);
                    engineer.dragging = false;
                    h1 = hex;
                    updateLine();
                }
            }
            return true;
        }

        private void touchInfo(Hex hex)
        {
            GdxBoardTest.debug("BoardScreen", String.format("touchDown [%d;%d] => %s[%d]", (int)v.x, (int)v.y, hex, board.genKey((int)v.x, (int)v.y)));
        }

        private void updateLine()
        {
            float x0 = panzer.getCX();
            float y0 = panzer.getCY();
            float dx = engineer.getCX() - x0;
            float dy = engineer.getCY() - y0;
            float d = (float) Math.sqrt((dx * dx) + (dy * dy));
            line.setOrigin(0, 0);
            line.setPosition(x0, y0);
            line.setSize(d, line.getHeight());
            line.setRotation((float) Math.toDegrees(Math.atan2(dy, dx)));
            for (Tile tile: tilesToDraw) {
                tile.enableOverlay(0, false);
                tile.enableOverlay(2, false);
            }
            board.lineOfSight(h0.x, h0.y, h1.x, h1.y, tilesToDraw);
            for (Tile tile: tilesToDraw) {
                if (tile.blocked) tile.enableOverlay(0, Orientation.N);
                else tile.enableOverlay(2, Orientation.N);
            }
        }

        public boolean drag(float dx, float dy)
        {
            if (panzer.dragging) {
                panzer.translate(dx, dy);
                return true;
            } else if (engineer.dragging) {
                engineer.translate(dx, dy);
                return true;
            }
            return false;
        }

        private Hex getHex(int x, int y)
        {
            return (Hex) board.getTile(x, y);
        }

        private Tile getTile(int x, int y, boolean isOnMap)
        {
            if (isOnMap)
                return tileStorage.getTile(x, y, board::genKey, this::buildTile);
            return Tile.OffMap;
        }

        private Tile buildTile(int x, int y)
        {
            final Vector2 v = new Vector2();
            board.centerOf(x, y, v);
            return new Hex(x, y, v.x, v.y, Terrain.get(board.genKey(x, y)));
        }

        public void setHEX_V()
        {
            Terrain.v = true;
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
            Terrain.v = false;
            map = assets.getTexture(assets.MAP_00);
            r = 90;
            dx = - ( map.getWidth() - map.getHeight() ) / 2;
            dy = - dx;
            w = map.getHeight();
            h = map.getWidth();
            board = BoardFactory.getBoard(9, 10, BoardFactory.BoardType.HEX, 110, 103, 50, BoardFactory.BoardOrientation.HORIZONTAL, this::getTile);
            tileStorage = new ArrayTileStorage(board.size());
        }
    }

    public enum State
    {
        HEX_V, HEX_H, DONE;
        public State next()
        {
            switch(this) {
                case HEX_V:
                    return HEX_H;
                case HEX_H:
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
    private final Vector2 relative = new Vector2(0, 0);

    public HexScreen(final GdxBoardTest app)
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
        this.board.reset();
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
        cam.unproject(x, y, boardTouch);
        cam.unprojectHud(x, y, hudTouch);
        if (btn.touch(hudTouch.x, hudTouch.y) != null) {
            if (down)
                setState(state.next());
        } else {
            board.touch(boardTouch.x, boardTouch.y, down);
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
