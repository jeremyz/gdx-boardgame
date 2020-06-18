package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.math.MathUtils;

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

class Unit extends Piece
{
    public boolean dragging;
    private final int mp;
    private final boolean hardTarget;

    public Unit(Texture texture, boolean hardTarget)
    {
        super(texture);
        this.hardTarget = hardTarget;
        if (hardTarget) {
            this.mp = 2;
        } else {
            this.mp = 1;
        }
    }

    @Override public int roadMarchBonus()
    {
        if (hardTarget) return 2;
        return 0;
    }

    @Override public int getAvailableMP()
    {
        return mp;
    }

    @Override public int moveCost(Tile from, Tile to, Orientation orientation)
    {
        Terrain dst = ((Hex)to).terrain;
        if (dst == Terrain.HILL) return 3;
        if (!hardTarget) return 1;
        if (((Hex)from).hasRoad(orientation)) return 1;
        if (dst.difficult()) return 2;
        return 1;
    }
}

enum Terrain
{
    WOODS(0,1), CITY(0,2), HILL(2,0), PLAIN(0,0);

    public int elevation;
    public int height;
    private Terrain(int elevation, int height) {
        this.elevation = elevation;
        this.height = height;
    }

    public boolean difficult()
    {
        return (this != PLAIN);
    }

    static public boolean v = true;

    static private int[] cv = {23, 74};
    static private int[] hv = {68, 78, 79, 15, 45, 46};
    static private int[] wv = {20, 30, 51, 52, 62, 63, 26, 17};
    static private int[] ch = {17, 61};
    static private int[] hh = {2, 3, 11, 45, 46, 72};
    static private int[] wh = {24, 25, 32, 33, 49, 58, 64, 74};

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

class Hex extends Tile
{
    public final int roads;
    public final Terrain terrain;

    public Hex(int x, int y, float cx, float cy, int k, Terrain terrain)
    {
        super(x, y, cx, cy);
        this.terrain = terrain;
        this.roads = getRoad(k);
    }

    @Override public boolean hasRoad(Orientation orientation)
    {
        return orientation.belongsTo(roads);
    }

    @Override public boolean blockLos(final Tile from, final Tile to, float d, float dt)
    {
        int h = terrain.elevation + terrain.height;
        if (h == 0) return false;

        int e = ((Hex)from).terrain.elevation;
        if (e > h) {
            if (((Hex)to).terrain.elevation > h) return false;
            return (h * dt / (e - h)) >= (d - dt);
        } else {
            h -= e;
            return (h * d / dt) >= (((Hex)to).terrain.elevation - e);
        }
    }

    public String toString()
    {
        return String.format("%s r:%s %s", terrain.toString(), roads, super.toString());
    }

    static public boolean v = true;

    static private int[] vre  = {35, 36, 37, 41, 42, 43, 48, 49};
    static private int[] vrne = {13, 32, 44, 54, 64};
    static private int[] vrnw = {4, 23, 35, 41, 74, 83};
    static private int[] vrw  = {36, 37, 42, 43, 44, 48, 49, 50};
    static private int[] vrsw = {23, 42, 54, 64, 74};
    static private int[] vrse = {4, 13, 32, 44, 50, 83};
    static private int[] hrne = {7, 31, 44, 51, 70, 80};
    static private int[] hrn  = {29, 30, 41, 42, 43, 54, 55, 56};
    static private int[] hrnw = {26, 35, 44, 51, 70};
    static private int[] hrsw = {17, 41, 54, 61, 80};
    static private int[] hrs  = {29, 30, 31, 42, 43, 44, 55, 56};
    static private int[] hrse = {17, 26, 35, 42, 63};

    private static int getRoad(int k)
    {
        int r = 0;
        if (v) {
            for (int i : vre) {
                if (i == k)
                    r |= Orientation.E.s();
            }
            for (int i : vrne) {
                if (i == k)
                    r |= Orientation.NE.s();
            }
            for (int i : vrnw) {
                if (i == k)
                    r |= Orientation.NW.s();
            }
            for (int i : vrw) {
                if (i == k)
                    r |= Orientation.W.s();
            }
            for (int i : vrsw) {
                if (i == k)
                    r |= Orientation.SW.s();
            }
            for (int i : vrse) {
                if (i == k)
                    r |= Orientation.SE.s();
            }
        } else {
            for (int i : hrne) {
                if (i == k)
                    r |= Orientation.NE.s();
            }
            for (int i : hrn) {
                if (i == k)
                    r |= Orientation.N.s();
            }
            for (int i : hrnw) {
                if (i == k)
                    r |= Orientation.NW.s();
            }
            for (int i : hrsw) {
                if (i == k)
                    r |= Orientation.SW.s();
            }
            for (int i : hrs) {
                if (i == k)
                    r |= Orientation.S.s();
            }
            for (int i : hrse) {
                if (i == k)
                    r |= Orientation.SE.s();
            }
        }
        return r;
    }

}

public class HexScreen extends AbstractScreen
{
    private class MyBoard
    {
        private final IterableSet<Tile> losTiles;
        private final IterableSet<Tile> moveTiles;
        private final Vector2 v;
        private final Vector3 v3;
        private final Assets assets;
        public Texture map;
        public Board board;
        public TileStorage tileStorage;
        private Tile t0;
        private Tile t1;
        private final Unit panzer;
        private final Unit engineer;
        private final Sprite line;
        private final Sprite line_r;
        private boolean blocked;
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
            this.panzer = new Unit(assets.getTexture(assets.PANZER), true);
            this.engineer = new Unit(assets.getTexture(assets.ENGINEER), false);
            this.line = new Sprite(assets.getTexture(assets.LINE));
            this.line_r = new Sprite(assets.getTexture(assets.LINE_R));
            this.losTiles = new IterableSet<Tile>(15);
            this.moveTiles = new IterableSet<Tile>(20);
            Tile.defaultOverlay = assets.getAtlas(app.assets.HEX_OVERLAYS);
        }

        public int getWidth() { return map.getWidth(); }
        public int getHeight() { return map.getHeight(); }

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
            int k = board.genKey(x, y);
            return new Hex(x, y, v.x, v.y, k, Terrain.get(k));
        }

        public void draw(SpriteBatch batch)
        {
            batch.draw(map, dx, dy, map.getWidth()/2, map.getHeight()/2, map.getWidth(), map.getHeight(), 1, 1, r, 0, 0, map.getWidth(), map.getHeight(), false, false);
            for (Tile tile: losTiles) tile.draw(batch);
            for (Tile tile: moveTiles) tile.draw(batch);
            panzer.draw(batch);
            engineer.draw(batch);
            line.draw(batch);
            if (blocked) line_r.draw(batch);
        }

        public void reset()
        {
            v.set(0, 0);
            losTiles.clear();
            moveTiles.clear();
            t0 = board.getTile(0, 0);
            setUnitOn(panzer, t0.x, t0.y, Orientation.DEFAULT);
            t1 = board.getTile(8, 5);
            setUnitOn(engineer, t1.x, t1.y, Orientation.SW);
            update(t0, panzer);
        }

        private void setUnitOn(Unit unit, int x, int y, Orientation o)
        {
            board.centerOf(x, y, v);
            unit.centerOn(v.x, v.y);
            unit.setRotation(o.r());
        }

        private void touchInfo(Tile t)
        {
            GdxBoardTest.debug("BoardScreen", String.format("touchDown [%d;%d] => %s[%d]", t.x, t.y, t, board.genKey(t.x, t.y)));
        }

        public boolean touch(float x, float y, boolean down)
        {
            board.toBoard(x, y, v);
            Tile tile = board.getTile((int)v.x, (int)v.y);
            if (!tile.isOnMap())
                return false;
            if (down) {
                if (!panzer.dragging && panzer.isOn(tile)) {
                    panzer.dragging = true;
                } else if (!engineer.dragging && engineer.isOn(tile)) {
                    engineer.dragging = true;
                } else {
                    touchInfo(tile);
                }
            } else {
                if (panzer.dragging) {
                    update(tile, panzer);
                } else if (engineer.dragging) {
                    update(tile, engineer);
                }
            }
            return true;
        }

        private void update(Tile t, Unit u)
        {
            touchInfo(t);
            u.centerOn(t.cx, t.cy);
            u.dragging = false;
            if (u == panzer)
                t0 = t;
            else
                t1 = t;
            for (Tile tile: losTiles) tile.disableOverlays();
            for (Tile tile: moveTiles) tile.disableOverlays();
            board.possibleMoves(u, t, moveTiles);
            for (Tile tile: moveTiles) tile.enableOverlay(3, Orientation.N);

            blocked = board.lineOfSight(t0, t1, losTiles, v);
            if (blocked) {
                setLine(line, panzer.getCX(), panzer.getCY(), v.x, v.y);
                setLine(line_r, v.x, v.y, engineer.getCX(), engineer.getCY());
            } else {
                setLine(line, panzer.getCX(), panzer.getCY(), engineer.getCX(), engineer.getCY());
            }
            for (Tile tile: losTiles) {
                if (tile.blocked) tile.enableOverlay(0, Orientation.N);
                else tile.enableOverlay(2, Orientation.N);
                moveTiles.remove(tile);
            }
        }

        private void setLine(Sprite l, float x0, float y0, float x1, float y1)
        {
            float dx = x1 - x0;
            float dy = y1 - y0;
            float d = (float) Math.sqrt((dx * dx) + (dy * dy));
            l.setOrigin(0, 0);
            l.setPosition(x0, y0);
            l.setSize(d, l.getHeight());
            l.setRotation((float) Math.toDegrees(Math.atan2(dy, dx)));
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

        public void setHEX_V()
        {
            Terrain.v = true;
            Hex.v = true;
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
            Hex.v = false;
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
