package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;

import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.ui.Menu;

public class MenuScreen extends AbstractScreen
{
    private Sprite corner;
    private Menu menu;

    public MenuScreen(final GdxBoardTest app)
    {
        super(app, "MenuScreen");
    }

    @Override protected void feed()
    {
        final Assets assets = app.assets;
        this.corner = new Sprite(assets.getTexture(assets.CORNER));

        this.menu = new Menu(
                assets.getFont(assets.FONT_25),
                assets.getNinePatch(assets.PATCH, 23, 23, 23 ,23),
                "Menu", new String[]{"UI","Board","Exit"});
        this.menu.setAlignment(Alignment.MIDDLE_CENTER);
        this.menu.setPaddings(5, 5);
        this.menu.setSpacings(10, 5);
        this.menu.setPadding(20);
        this.menu.setLabelsOffset(10);
        this.root.add(this.menu);

        this.camera = new OrthographicCamera(bg.getWidth() * WORLD_RATIO, bg.getHeight() * WORLD_RATIO);
        this.camera.position.set(bg.getWidth() / 2f, bg.getHeight() / 2f, 0);
        this.camera.update();
    }

    @Override protected void draw(SpriteBatch batch)
    {
        batch.draw(bg, 0, 0);
        drawCorners(batch);
        root.draw(batch);
    }

    private void drawCorners(SpriteBatch batch)
    {
        float right = root.getInnerX() + root.getInnerWidth() - corner.getWidth();
        float top = root.getInnerY() + root.getInnerHeight() - corner.getHeight();
        corner.setRotation(0);
        corner.setPosition(root.getInnerX(), top);
        corner.draw(batch);
        corner.setRotation(90);
        corner.setPosition(root.getInnerX(), root.getInnerY());
        corner.draw(batch);
        corner.setRotation(180);
        corner.setPosition(right, root.getInnerY());
        corner.draw(batch);
        corner.setPosition(right, top);
        corner.setRotation(270);
        corner.draw(batch);
    }

    @Override protected void onZoom(float dz) { }
    @Override protected void onDragged(int dx, int dy) { }
    @Override protected void onTouch(int x, int y)
    {
        hudTouch.set(x, y, 0);
        camera.unproject(hudTouch);
        if (root.touch(hudTouch.x, hudTouch.y)) {
            switch(menu.touched()) {
                case 0:
                    app.switchToUi();
                    break;
                case 1:
                    app.switchToBoard();
                    break;
                case 2:
                    app.switchToExit();
                    break;
            }
        }
    }
}
