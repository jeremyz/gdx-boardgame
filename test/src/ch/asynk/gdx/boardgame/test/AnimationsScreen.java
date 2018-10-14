package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Camera;
import ch.asynk.gdx.boardgame.Orientation;
import ch.asynk.gdx.boardgame.pieces.Piece;
import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.boards.BoardFactory;
import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.animations.AnimationSequence;
import ch.asynk.gdx.boardgame.animations.BounceAnimation;
import ch.asynk.gdx.boardgame.animations.DelayAnimation;

public class AnimationsScreen extends AbstractScreen
{
    private final Texture map;
    private final Piece panzer;
    private final Camera cam;
    private final Board board;
    private final AnimationSequence animations;

    public AnimationsScreen(final GdxBoardTest app)
    {
        super(app, "");

        this.map = app.assets.getTexture(app.assets.MAP_00);
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
        this.camera = this.cam = new Camera(10, map.getWidth(), map.getHeight(), 1.0f, 0.3f, false);

        this.panzer = new Piece(app.assets.getTexture(app.assets.PANZER));
        Vector2 v = new Vector2();
        this.board.centerOf(7, 4, v);
        this.panzer.setPosition(v.x - (panzer.getWidth() / 2), v.y - (panzer.getHeight() / 2));
        this.panzer.setRotation(Orientation.NW.r());

        cam.zoom(-0.3f);
        cam.centerOnWorld();

        animations = AnimationSequence.get(10);
        animations.add(BounceAnimation.get(panzer, 2f, 3f, -1));
        animations.add(DelayAnimation.get(panzer, 1f));
    }

    @Override public void dispose()
    {
        animations.dispose();
        super.dispose();
    }

    @Override public void draw(SpriteBatch batch) { }
    @Override public void render(float delta)
    {
        if (paused) return;

        if (inputBlocked) {
            inputDelay -= delta;
            if (inputDelay <= 0f)
                inputBlocked = false;
        }

        if (animations.animate(delta)) {
            app.switchToMenu();
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.applyBoardViewport();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(map, 0, 0);
        animations.draw(batch);
        batch.end();
    }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug("BoardScrean", String.format("resize (%d,%d)",width, height));
        cam.updateViewport(width, height);
    }

    @Override protected void onZoom(float dz) { }
    @Override protected void onDragged(int dx, int dy) { }
    @Override protected void onTouch(int x, int y) { }
}
