package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Camera;
import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.Path;
import ch.asynk.gdx.boardgame.Tile;
import ch.asynk.gdx.boardgame.Piece;
import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.boards.BoardFactory;
import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.animations.AnimationBatch;
import ch.asynk.gdx.boardgame.animations.AnimationSequence;
import ch.asynk.gdx.boardgame.animations.BounceAnimation;
import ch.asynk.gdx.boardgame.animations.DelayAnimation;
import ch.asynk.gdx.boardgame.animations.FadeAnimation;
import ch.asynk.gdx.boardgame.animations.MoveAnimation;
import ch.asynk.gdx.boardgame.animations.FireAnimation;

public class AnimationsScreen extends AbstractScreen
{
    private final Texture map;
    private final Piece panzer;
    private final Piece other0;
    private final Piece other1;
    private final Piece other2;
    private final Dice dice;
    private final Camera cam;
    private final Board board;
    private final Path path;
    private final AnimationSequence animations;

    public AnimationsScreen(final GdxBoardTest app)
    {
        super(app, "");

        this.map = app.assets.getTexture(app.assets.MAP_00);
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
        this.camera = this.cam = new Camera(10, map.getWidth(), map.getHeight(), 1.0f, 0.3f, false);

        this.panzer = getPiece(app, 7, 4, Orientation.NW, app.assets.PANZER, 1f);
        this.other0 = getPiece(app, 9, 5, Orientation.SW, app.assets.PANZER, 0f);
        this.other1 = getPiece(app, 3, 1, Orientation.NE, app.assets.PANZER, 0f);
        this.other2 = getPiece(app, 7, 5, Orientation.SE, app.assets.ENGINEER, 0f);

        this.dice = getDice(app, 6, 1, 1);
        this.dice.rollTo(6);

        cam.zoom(-0.3f);
        cam.centerOnWorld();

        path = buildPath(app);

        AnimationBatch batch;
        FireAnimation.register("mgun0", 11, 0.1f, 0.7f, 30f, 150f, 1.3f, 0f,
                app.assets.getTexture(app.assets.GUN_FIRE), 8, 1,
                null, 0, 0,
                app.assets.getSound(app.assets.GUN_FIRE_SND),
                null);
        FireAnimation.register("shell0", 1, 0, 1f, 66f, 400f, 1.3f, 1f,
                app.assets.getTexture(app.assets.SHELL_FIRE), 8, 1,
                app.assets.getTexture(app.assets.EXPLOSIONS), 8, 16,
                app.assets.getSound(app.assets.SHELL_FIRE_SND),
                app.assets.getSound(app.assets.EXPLOSION_SND));
        FireAnimation.register("shell1", 1, 0, .5f, 11f, 500f, .8f, 1f,
                app.assets.getTexture(app.assets.SHELL_FIRE), 8, 1,
                app.assets.getTexture(app.assets.EXPLOSIONS), 8, 16,
                app.assets.getSound(app.assets.SHELL_FIRE_SND),
                app.assets.getSound(app.assets.EXPLOSION_SND));

        animations = AnimationSequence.obtain(10);
        animations.add(BounceAnimation.obtain(panzer, 1f, 3f, -1));
        animations.add(DelayAnimation.obtain(.5f));
        animations.add(MoveAnimation.obtain(panzer, path, 2.5f, app.assets.getSound(app.assets.TANK_MOVE_SND), (p,path) -> {
            Tile from = path.from();
            Tile to = path.to();
            System.err.println(String.format("%s -> %s", from, to));
            from.enableOverlay(2, false);
            if (to != null) to.enableOverlay(2, true);
        }));
        batch = AnimationBatch.obtain(3);
        batch.add(FadeAnimation.obtain(other0, 0f, 1f, .5f));
        batch.add(FadeAnimation.obtain(other1, 0f, 1f, .5f));
        batch.add(FadeAnimation.obtain(other2, 0f, 1f, .5f));
        animations.add(batch);
        animations.add(getFireAnimationBatch());
        batch = AnimationBatch.obtain(3);
        batch.add(FadeAnimation.obtain(other0, 1f, 0f, .5f));
        batch.add(FadeAnimation.obtain(other1, 1f, 0f, .5f));
        batch.add(FadeAnimation.obtain(other2, 1f, 0f, .5f));
        animations.add(batch);
    }

    private Dice getDice(final GdxBoardTest app, int col, int row, int side)
    {
        Dice d = new Dice(app.assets.getTexture(app.assets.DICE), 9, 16, 0.02f,
                app.assets.getSound(app.assets.DICE_SND));
        Vector2 v = new Vector2();
        this.board.centerOf(col, row, v);
        d.centerOn(v.x, v.y);
        d.setSide(side);
        return d;
    }

    private Piece getPiece(final GdxBoardTest app, int col, int row, Orientation o, String rsc, float a)
    {
        Piece p = new Piece(app.assets.getTexture(rsc));
        Vector2 v = new Vector2();
        this.board.centerOf(col, row, v);
        p.centerOn(v.x, v.y);
        p.setRotation(o.r());
        p.setAlpha(a);
        return p;
    }

    private Path buildPath(final GdxBoardTest app)
    {
        Tile.defaultOverlay = app.assets.getAtlas(app.assets.HEX_OVERLAYS);
        Vector2 v = new Vector2();
        Path path = Path.obtain();
        path.ensureCapacity(10);
        board.centerOf(7, 4, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(6, 4, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(5, 3, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(5, 2, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(6, 2, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(7, 2, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(8, 3, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(9, 4, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(8, 4, v);
        path.add(new Tile(v.x, v.y));
        board.centerOf(7, 4, v);
        path.add(new Tile(v.x, v.y));
        path.setFinalOrientation(Orientation.NE);
        return path;
    }

    private AnimationBatch getFireAnimationBatch()
    {
        AnimationBatch batch = AnimationBatch.obtain(3);
        batch.add(FireAnimation.obtain("shell0", other0, panzer));
        batch.add(FireAnimation.obtain("shell1", other1, panzer));
        batch.add(FireAnimation.obtain("mgun0", other2, panzer));
        return batch;
    }

    @Override public void dispose()
    {
        FireAnimation.free();
        animations.dispose();
        super.dispose();
    }

    @Override protected boolean animate(float delta)
    {
        if (inputBlocked) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                inputBlocked = false;
        }

        dice.animate(delta);
        if (animations.animate(delta)) {
            app.switchToMenu();
            return false;
        }
        return true;
    }

    @Override public void draw(SpriteBatch batch)
    {
        batch.draw(map, 0, 0);
        for (int i = 0; i < path.size(); i++) {
            path.get(i).draw(batch);
        }
        panzer.draw(batch);
        other0.draw(batch);
        other1.draw(batch);
        other2.draw(batch);
        dice.draw(batch);
        animations.draw(batch);
    }

    @Override public void drawDebug(ShapeRenderer shapeRenderer)
    {
        for (int i = 0; i < path.size(); i++) {
            path.get(i).drawDebug(shapeRenderer);
        }
        panzer.drawDebug(shapeRenderer);
        other0.drawDebug(shapeRenderer);
        other1.drawDebug(shapeRenderer);
        other2.drawDebug(shapeRenderer);
        dice.drawDebug(shapeRenderer);
        animations.drawDebug(shapeRenderer);
    }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug("AnimationScrean", String.format("resize (%d,%d)",width, height));
        cam.updateViewport(width, height);
        root.resize(cam.getHud());
    }

    @Override protected void onZoom(float dz) { }
    @Override protected void onDragged(int dx, int dy) { }
    @Override protected void onTouch(int x, int y) { }
}
