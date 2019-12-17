package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.OrthographicCamera;

import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.ui.Button;

class MyButton extends Button
{
    public MyButton(BitmapFont font, NinePatch patch, float padding, float spacing)
    {
        super(font, patch, padding, spacing);
    }

    @Override public void computeGeometry()
    {
        super.computeGeometry();
        label.write(String.format("%04d;%04d", (int)getX(), (int)getY()));
    }
}

public class UiScreen extends AbstractScreen
{
    private final Button next;
    private final Button[] buttons = new Button[8];

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

        this.buttons[0] = buildButton(font, patch, 10, 15, Alignment.TOP_LEFT, Alignment.BOTTOM_RIGHT);
        this.buttons[1] = buildButton(font, patch, 10, 15, Alignment.TOP_CENTER, Alignment.BOTTOM_CENTER);
        this.buttons[2] = buildButton(font, patch, 10, 15, Alignment.TOP_RIGHT, Alignment.BOTTOM_LEFT);
        this.buttons[3] = buildButton(font, patch, 10, 15, Alignment.MIDDLE_LEFT, Alignment.MIDDLE_RIGHT);
        this.buttons[4] = buildButton(font, patch, 10, 15, Alignment.MIDDLE_CENTER, Alignment.MIDDLE_CENTER);
        this.buttons[5] = buildButton(font, patch, 10, 15, Alignment.MIDDLE_RIGHT, Alignment.MIDDLE_LEFT);
        this.buttons[6] = buildButton(font, patch, 10, 15, Alignment.BOTTOM_LEFT, Alignment.TOP_RIGHT);
        this.buttons[7] = buildButton(font, patch, 10, 15, Alignment.BOTTOM_CENTER, Alignment.TOP_CENTER);

        this.next = new Button(font, patch, 10, 15);
        this.next.write("Next");
        this.next.setAlignment(Alignment.BOTTOM_RIGHT);
        this.next.setLabelAlignment(Alignment.TOP_LEFT);
        this.root.add(this.next);

        this.camera = new OrthographicCamera();
        this.camera.position.set(bg.getWidth() / 2f, bg.getHeight() / 2f, 0);
        setState(State.POSITIONS);
    }

    private Button buildButton(BitmapFont font, NinePatch patch, int padding, int spacing, Alignment a, Alignment la)
    {
        final Button button = new MyButton(font, patch, padding, spacing);
        button.setAlignment(a);
        button.setLabelAlignment(la);
        button.write(String.format("%04d;%04d", 0, 0));
        return button;
    }

    private void setState(State state)
    {
        switch (state) {
            case POSITIONS:
                setButtons(true);
                break;
            case DONE:
                setButtons(false);
                app.switchToMenu();
                break;
        }
        this.state = state;
    }

    @Override protected boolean animate(float delta) { return true; }

    @Override protected void draw(SpriteBatch batch)
    {
        batch.draw(bg, 0, 0);
        root.draw(batch);
    }

    @Override protected void drawDebug(ShapeRenderer shapeRenderer)
    {
        root.drawDebug(shapeRenderer);
    }

    private void setButtons(boolean add)
    {
        if (add) {
            for (Button button : buttons) {
                root.add(button);
            }
        } else {
            for (Button button : buttons)
                root.remove(button);
        }
    }

    @Override protected void onZoom(float dz) { }
    @Override protected void onDragged(int dx, int dy) { }
    @Override protected void onTouch(int x, int y)
    {
        hudTouch.set(x, y, 0);
        camera.unproject(hudTouch);
        if (root.touch(hudTouch.x, hudTouch.y)) {
            if (root.touched() == next)
                setState(state.next());
        }
    }
}
