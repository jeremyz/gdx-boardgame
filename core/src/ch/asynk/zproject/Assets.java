package ch.asynk.zproject;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class Assets extends AssetManager implements Disposable
{
    public static final String CORNER = "data/corner.png";
    public static final String MAP_00 = "data/map_00.png";
    public static final String LOADING = "data/loading.atlas";

    @Override public void dispose()
    {
        ZProject.debug("diagnostics:\n" + getDiagnostics() );
        clear();
        super.dispose();
    }

    public Texture getTexture(String assetName)
    {
        return get(assetName, Texture.class);
    }

    public TextureAtlas getAtlas(String assetName)
    {
        return get(assetName, TextureAtlas.class);
    }

    public void loadLoading()
    {
        load(LOADING, TextureAtlas.class);
    }

    public void unloadLoading()
    {
        unload(LOADING);
    }

    public void loadGame()
    {
        load(MAP_00, Texture.class);
        load(CORNER, Texture.class);
    }

    public void unloadGame()
    {
        unload(MAP_00);
        unload(CORNER);
    }
}
