package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;

import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.ui.Button;

public class UiScreen extends AbstractScreen
{
    private final float WORLD_RATIO = 0.5f;
    private final Button hello;

    public enum State
    {
        POSITIONS, DONE;
        public State next()
        {
            switch(this) {
                case POSITIONS:
                    return DONE;
                default:
                    return POSITIONS;
            }
        }
    }
    private State state;


    public UiScreen(final GdxBoardTest app)
    {
        super(app, "UiScreen");

        final NinePatch patch = app.assets.getNinePatch(app.assets.PATCH, 23, 23, 23 ,23);
        final BitmapFont font = app.assets.getFont(app.assets.FONT_25);

        this.hello = new Button(font, patch, 10, 15);
        this.hello.write("Hello");
        this.root.add(this.hello);

        this.camera = new OrthographicCamera(bg.getWidth() * WORLD_RATIO, bg.getHeight() * WORLD_RATIO);
        this.camera.position.set(bg.getWidth() / 2f, bg.getHeight() / 2f, 0);
        this.camera.update();
        setState(State.POSITIONS);
    }

    private void setState(State state)
    {
        switch (state) {
            case DONE:
                app.switchToMenu();
        }
        this.state = state;
    }

    @Override protected void draw(SpriteBatch batch)
    {
        batch.draw(bg, 0, 0);
        switch (state) {
            case POSITIONS:
                drawButtons(batch);
                break;
        }
    }

    private void drawButtons(SpriteBatch batch)
    {
        hello.write("hello");
        hello.setAlignment(Alignment.TOP_LEFT);
        hello.setLabelAlignment(Alignment.BOTTOM_RIGHT);
        root.draw(batch);
        drawHello(batch, Alignment.TOP_CENTER, Alignment.BOTTOM_CENTER);
        drawHello(batch, Alignment.TOP_RIGHT, Alignment.BOTTOM_LEFT);
        drawHello(batch, Alignment.MIDDLE_LEFT, Alignment.MIDDLE_RIGHT);
        drawHello(batch, Alignment.MIDDLE_RIGHT, Alignment.MIDDLE_LEFT);
        drawHello(batch, Alignment.BOTTOM_LEFT, Alignment.TOP_RIGHT);
        drawHello(batch, Alignment.BOTTOM_CENTER, Alignment.TOP_CENTER);
        drawHello(batch, Alignment.BOTTOM_RIGHT, Alignment.TOP_LEFT);
        hello.write("next");
        drawHello(batch, Alignment.MIDDLE_CENTER, Alignment.MIDDLE_CENTER);
    }

    private void drawHello(SpriteBatch batch, Alignment alignment1, Alignment alignment2)
    {
        hello.setAlignment(alignment1);
        hello.setLabelAlignment(alignment2);
        hello.draw(batch);
    }

    @Override public void resize(int width, int height)
    {
        resize(width, height, WORLD_RATIO);
    }

    @Override protected void onZoom(float dz) { }
    @Override protected void onDragged(int dx, int dy) { }
    @Override protected void onTouch(int x, int y)
    {
        hudTouch.set(x, y, 0);
        camera.unproject(hudTouch);
        if (root.touch(hudTouch.x, hudTouch.y)) {
            setState(state.next());
        }
    }
}
