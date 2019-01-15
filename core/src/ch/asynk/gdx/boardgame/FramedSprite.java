package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FramedSprite implements Drawable, Positionable
{
    public static int trim = 2;
    public static int offset = 0;

    private TextureRegion[][] frames;
    private TextureRegion frame;
    public final int rows;
    public final int cols;
    public float x;
    public float y;
    public float a;

    public FramedSprite(Texture texture, int rows, int cols)
    {
        this.frames = TextureRegion.split(texture, (texture.getWidth() / cols), (texture.getHeight() / rows));
        this.frame = frames[0][0];
        this.rows = rows;
        this.cols = cols;
        this.x = 0;
        this.y = 0;
        this.a = 0;

        if (trim > 0 || offset > 0) {
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    TextureRegion f = frames[r][c];
                    if (offset > 0 ){
                        f.setRegionX(f.getRegionX() + offset);
                        f.setRegionY(f.getRegionY() + offset);
                    }
                    if (trim > 0) {
                        f.setRegionWidth(f.getRegionWidth() - trim);
                        f.setRegionHeight(f.getRegionHeight() - trim);
                    }
                }
            }
        }
    }

    public FramedSprite(FramedSprite other)
    {
        Texture t = other.frame.getTexture();
        this.rows = other.rows;
        this.cols = other.cols;
        this.frames = TextureRegion.split(t, (t.getWidth() / cols), (t.getHeight() / rows));
        this.frame = frames[0][0];
        this.x = other.x;
        this.y = other.y;
        this.a = other.a;
    }

    public void setFrame(int row, int col)
    {
        this.frame = frames[row][col];
    }

    public TextureRegion getFrame()
    {
        return frame;
    }

    @Override public float getX()
    {
        return x;
    }

    @Override public float getY()
    {
        return y;
    }

    @Override public float getWidth()
    {
        return frame.getRegionWidth();
    }

    @Override public float getHeight()
    {
        return frame.getRegionHeight();
    }

    @Override public void translate(float dx, float dy)
    {
        setPosition(getX() + dx, getY() + dy);
    }

    @Override public void setPosition(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override public void draw(Batch batch)
    {
        batch.draw(frame, x, y, 0, 0, frame.getRegionWidth(), frame.getRegionHeight(), 1f, 1f, a);
    }
}
