package ch.asynk.zproject;

import java.util.function.Supplier;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

import ch.asynk.zproject.engine.ui.Button;
import ch.asynk.zproject.engine.ui.Alignment;
import ch.asynk.zproject.engine.ui.Root;
import ch.asynk.zproject.engine.Touchable;

public class GameHud implements Disposable, Touchable
{
    private final Sprite corner;
    private final Root root;
    private final Button hello;
    private final Button next;
    private GameScreen.State state;
    private final Supplier<GameScreen.State> nextState;

    public GameHud(final Assets assets, GameScreen.State state, Supplier<GameScreen.State> nextState)
    {
        this.state = state;
        this.nextState = nextState;
        this.corner = new Sprite(assets.getTexture(assets.CORNER));

        this.root = new Root(2);
        this.root.setPadding(30);

        this.hello = new Button(assets.getFont(assets.FONT_25), assets.getNinePatch(assets.PATCH, 23, 23, 23 ,23), 10, 15);
        this.hello.write("Hello");
        this.root.add(this.hello);

        this.next = new Button(assets.getFont(assets.FONT_25), assets.getNinePatch(assets.PATCH, 23, 23, 23 ,23), 20, 0);
        this.next.write("NEXT");
        this.next.setPosition(50, 50);
        this.next.setAlignment(Alignment.MIDDLE_CENTER);
        this.next.setLabelAlignment(Alignment.MIDDLE_CENTER);
        this.root.add(this.next);
    }

    @Override public void dispose()
    {
        corner.getTexture().dispose();
    }

    @Override public boolean touch(float x, float y)
    {
        if (root.touch(x, y)) {
            ZProject.debug("GameHud", String.format("touchDown : %f %f", x, y));
            if (root.touched() == this.next)
                onNext();
            return true;
        }
        return false;
    }

    public void resize(float width, float height)
    {
        this.root.resize(width, height);
    }

    public void onNext()
    {
        this.state = nextState.get();
        switch (this.state) {
            case UI:
                updateNext(50, Alignment.MIDDLE_CENTER);
                this.root.add(this.hello);
                break;
            case HEX_V:
                updateNext(0, Alignment.BOTTOM_RIGHT);
                this.root.remove(this.hello);
                break;
            case HEX_H:
                break;
        }
    }

    private void updateNext(int p, Alignment a)
    {
        this.next.setPosition(p, p);
        this.next.setAlignment(a);
        this.next.update();
    }

    public void draw(Batch batch)
    {
        switch (this.state) {
            case UI:
                drawButtons(batch);
                drawCorners(batch);
                break;
            case HEX_V:
            case HEX_H:
                drawRoot(batch);
                break;
        }
    }

    private void drawRoot(Batch batch)
    {
        root.draw(batch);
    }

    private void drawCorners(Batch batch)
    {
        float right = root.getX() + root.getWidth() - corner.getWidth();
        float top = root.getY() + root.getHeight() - corner.getHeight();
        corner.setRotation(0);
        corner.setPosition(root.getX(), top);
        corner.draw(batch);
        corner.setRotation(90);
        corner.setPosition(root.getX(), root.getY());
        corner.draw(batch);
        corner.setRotation(180);
        corner.setPosition(right, root.getY());
        corner.draw(batch);
        corner.setPosition(right, top);
        corner.setRotation(270);
        corner.draw(batch);
    }

    private void drawButtons(Batch batch)
    {
        hello.setAlignment(Alignment.TOP_LEFT);
        hello.setLabelAlignment(Alignment.BOTTOM_RIGHT);
        hello.update();
        root.draw(batch);
        drawHello(batch, Alignment.TOP_CENTER, Alignment.BOTTOM_CENTER);
        drawHello(batch, Alignment.TOP_RIGHT, Alignment.BOTTOM_LEFT);
        drawHello(batch, Alignment.MIDDLE_LEFT, Alignment.MIDDLE_RIGHT);
        // drawHello(batch, Alignment.MIDDLE_CENTER, Alignment.MIDDLE_CENTER);
        drawHello(batch, Alignment.MIDDLE_RIGHT, Alignment.MIDDLE_LEFT);
        drawHello(batch, Alignment.BOTTOM_LEFT, Alignment.TOP_RIGHT);
        drawHello(batch, Alignment.BOTTOM_CENTER, Alignment.TOP_CENTER);
        drawHello(batch, Alignment.BOTTOM_RIGHT, Alignment.TOP_LEFT);
    }

    private void drawHello(Batch batch, Alignment alignment1, Alignment alignment2)
    {
        hello.setAlignment(alignment1);
        hello.setLabelAlignment(alignment2);
        hello.update();
        hello.draw(batch);
    }

    public void drawDebug(ShapeRenderer debugShapes)
    {
        root.drawDebug(debugShapes);
    }
}
