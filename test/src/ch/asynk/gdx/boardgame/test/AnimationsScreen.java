package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import ch.asynk.gdx.boardgame.Camera;
import ch.asynk.gdx.boardgame.boards.Board;
import ch.asynk.gdx.boardgame.boards.BoardFactory;
import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.animations.Animation;
import ch.asynk.gdx.boardgame.animations.BounceAnimation;

public class AnimationsScreen extends AbstractScreen
{
    public enum State
    {
        BOUNCE, DONE;
        public State next()
        {
            switch(this) {
                case BOUNCE:
                    return DONE;
                default:
                    return BOUNCE;
            }
        }
    }
    private State state;

    private final Texture map;
    private final Panzer panzer;
    private final Camera cam;
    private final Board board;
    private Animation animation;

    public AnimationsScreen(final GdxBoardTest app)
    {
        super(app, "");

        this.map = app.assets.getTexture(app.assets.MAP_00);
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
        this.camera = this.cam = new Camera(10, map.getWidth(), map.getHeight(), 1.0f, 0.3f, false);

        this.panzer = new Panzer(app);
        Vector2 v = new Vector2();
        this.board.centerOf(7, 4, v);
        this.panzer.setPosition(v.x - (panzer.getWidth() / 2), v.y - (panzer.getHeight() / 2));

        cam.zoom(-0.3f);
        cam.centerOnWorld();
        setState(State.BOUNCE);
    }

    private void setState(State state)
    {
        switch (state) {
            case BOUNCE:
                animation = BounceAnimation.get(panzer, 2f, 3f);
            case DONE:
                app.switchToMenu();
        }
        this.state = state;
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

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.applyBoardViewport();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        batch.draw(map, 0, 0);
        animation.animate(delta);
        animation.draw(batch);
        batch.end();

        if (animation.completed()) {
            setState(state.next());
        }
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
