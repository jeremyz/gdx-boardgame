package ch.asynk.zproject;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public class Assets extends AssetManager implements Disposable
{
    public static final String CORNER = "data/corner.png";
    public static final String MAP_00 = "data/map_00.png";
    public static final String LOADING = "data/loading.atlas";
    public static final String FONT = "data/veteran-typewriter.ttf";
    public static final String FONT_20 = "size20.ttf";
    public static final String FONT_25 = "size25.ttf";

    private final FreeTypeFontLoaderParameter params20;
    private final FreeTypeFontLoaderParameter params25;

    public Assets()
    {
        super();
        final FileHandleResolver resolver = new InternalFileHandleResolver();
        setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
        params20 = new FreeTypeFontLoaderParameter();
        params20.fontFileName = FONT;
        params20.fontParameters.size = 20;
        params25 = new FreeTypeFontLoaderParameter();
        params25.fontFileName = FONT;
        params25.fontParameters.size = 25;
    }

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

    public BitmapFont getFont(String assetName)
    {
        return get(assetName, BitmapFont.class);
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
        load(FONT_20, BitmapFont.class, params20);
        load(FONT_25, BitmapFont.class, params25);
    }

    public void unloadGame()
    {
        unload(MAP_00);
        unload(CORNER);
        unload(FONT_20);
        unload(FONT_25);
    }
}
