package ch.asynk.gdx.boardgame.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Bg extends Element
{
    private TextureRegion region;

    public Bg(TextureRegion region)
    {
        this.region = region;
        rect.set(0, 0, region.getRegionWidth(), region.getRegionHeight());
    }

    @Override public void drawReal(Batch batch)
    {
        batch.draw(region, rect.x, rect.y, rect.width, rect.height);
    }
}
