package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class FramedSprite
{
    public Texture texture;
    public TextureRegion[] frames;
    public final int width;
    public final int height;
    public final int cols;
    public final int rows;

    public FramedSprite(Texture texture, int cols, int rows)
    {
        this.cols = cols;
        this.rows = rows;
        this.width = (texture.getWidth() / cols);
        this.height = (texture.getHeight() / rows);
        this.texture = texture;
        TextureRegion[][] tmp = TextureRegion.split(texture, width, height);
        frames = new TextureRegion[cols * rows];
        int idx = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                frames[idx++] = tmp[i][j];
            }
        }
    }
}
