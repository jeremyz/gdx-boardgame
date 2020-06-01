package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import ch.asynk.gdx.boardgame.ui.Alignment;
import ch.asynk.gdx.boardgame.ui.Menu;

public class MenuScreen extends AbstractScreen
{
    private final Sprite corner;
    private final Menu menu;

    public MenuScreen(final GdxBoardTest app)
    {
        super(app, "MenuScreen");

        final Assets assets = app.assets;
        this.corner = new Sprite(assets.getTexture(assets.CORNER));

        this.menu = new Menu(
                assets.getFont(assets.FONT_25),
                assets.getNinePatch(assets.PATCH, 23, 23, 23 ,23),
                "Menu", new String[]{"UI", "Animations", "Board", "Hex","Exit"});
        this.menu.setAlignment(Alignment.MIDDLE_CENTER);
        this.menu.setPaddings(5, 5);
        this.menu.setSpacings(10, 5);
        this.menu.setPadding(20);
        this.menu.setLabelsOffset(10);
        this.root.add(this.menu);

        this.camera = new OrthographicCamera();
        this.camera.position.set(bg.getWidth() / 2f, bg.getHeight() / 2f, 0);
    }

    @Override protected boolean animate(float delta) { return true; }

    @Override protected void draw(SpriteBatch batch)
    {
        batch.draw(bg, 0, 0);
        drawCorners(batch);
        root.draw(batch);
    }

    @Override protected void drawDebug(ShapeRenderer shapeRenderer)
    {
        root.drawDebug(shapeRenderer);
    }

    private void drawCorners(SpriteBatch batch)
    {
        float right = root.getInnerRight() - corner.getWidth();
        float top = root.getInnerTop() - corner.getHeight();
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
    @Override protected void onTouch(int x, int y, boolean down)
    {
        hudTouch.set(x, y, 0);
        camera.unproject(hudTouch);
        if (root.touch(hudTouch.x, hudTouch.y) != root) {
            switch(menu.touched()) {
                case -1:
                    break;
                case 0:
                    app.switchToUi();
                    break;
                case 1:
                    app.switchToAnimations();
                    break;
                case 2:
                    app.switchToBoard();
                    break;
                case 3:
                    app.switchToHex();
                    break;
                case 4:
                    app.switchToExit();
                    break;
            }
        }
    }
}
