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
import ch.asynk.gdx.boardgame.ui.Button;
import ch.asynk.gdx.boardgame.ui.Root;

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
    private final Texture sherman;
    private final Vector2 v;
    private final Camera cam;
    private final Board board;
    private final Button btn;
    private final Root root;

    public AnimationsScreen(final GdxBoardTest app)
    {
        super(app, "");

        this.map = app.assets.getTexture(app.assets.MAP_00);
        this.sherman = app.assets.getTexture(app.assets.SHERMAN);
        this.v = new Vector2();
        this.board = BoardFactory.getBoard(BoardFactory.BoardType.HEX, 110, 50, 103, BoardFactory.BoardOrientation.VERTICAL);
        this.camera = this.cam = new Camera(10, map.getWidth(), map.getHeight(), 1.0f, 0.3f, false);
        this.board.centerOf(7, 4, v);

        this.btn = new Button(
                app.assets.getFont(app.assets.FONT_25),
                app.assets.getNinePatch(app.assets.PATCH, 23, 23, 23 ,23),
                15);
        this.btn.setAlignment(Alignment.BOTTOM_RIGHT);
        this.btn.write("next");
        this.root = new Root(1);
        this.root.add(btn);
        this.root.setPadding(5);
        setState(State.BOUNCE);
        cam.zoom(-0.3f);
        cam.centerOnWorld();
    }

    private void setState(State state)
    {
        switch (state) {
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
        batch.draw(sherman, v.x - (sherman.getWidth() / 2), v.y - (sherman.getHeight() / 2));
        batch.end();

        cam.applyHudViewport();
        batch.setProjectionMatrix(cam.getHudMatrix());
        batch.begin();
        root.draw(batch);
        batch.end();
    }

    @Override public void resize(int width, int height)
    {
        GdxBoardTest.debug("BoardScrean", String.format("resize (%d,%d)",width, height));
        cam.updateViewport(width, height);
        root.resize(cam.getHud());
    }

    @Override protected void onZoom(float dz) { }
    @Override protected void onDragged(int dx, int dy) { }
    @Override protected void onTouch(int x, int y)
    {
        cam.unprojectHud(x, y, hudTouch);
        if (btn.touch(hudTouch.x, hudTouch.y)) {
            setState(state.next());
        }
    }
}
