package ch.asynk.gdx.boardgame;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Assets extends AssetManager
{
    public Texture getTexture(String assetName)
    {
        return get(assetName, Texture.class);
    }

    public TextureRegion getTextureRegion(String assetName)
    {
        return new TextureRegion(get(assetName, Texture.class));
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

    public Sound getSound(String assetName)
    {
        return get(assetName, Sound.class);
    }
}
