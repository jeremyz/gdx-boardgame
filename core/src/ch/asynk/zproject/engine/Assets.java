package ch.asynk.zproject.engine;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets extends AssetManager
{
    public Texture getTexture(String assetName)
    {
        return get(assetName, Texture.class);
    }

    public NinePatch getNinePatch(String assetName, int left, int right, int top, int bottom)
    {
        return new NinePatch(get(assetName, Texture.class), left, right, top, bottom);
    }

    public TextureAtlas getAtlas(String assetName)
    {
        return get(assetName, TextureAtlas.class);
    }

    public BitmapFont getFont(String assetName)
    {
        return get(assetName, BitmapFont.class);
    }
}
