package ch.asynk.gdx.board;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public class Assets extends ch.asynk.gdx.board.engine.Assets
{
    public static final String LOADING = "loading.atlas";

    public static final String CORNER = "corner.png";
    public static final String MAP_00 = "map_00.png";
    public static final String CHESS = "chess.jpg";
    public static final String FONT = "veteran-typewriter.ttf";
    public static final String FONT_20 = "size20.ttf";
    public static final String FONT_25 = "size25.ttf";
    public static final String PATCH = "ui-patch.png";

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
        GdxBoardTest.debug("diagnostics:\n" + getDiagnostics() );
        clear();
        super.dispose();
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
        load(CHESS, Texture.class);
        load(CORNER, Texture.class);
        load(PATCH, Texture.class);
        load(FONT_20, BitmapFont.class, params20);
        load(FONT_25, BitmapFont.class, params25);
    }

    public void unloadGame()
    {
        unload(MAP_00);
        unload(CHESS);
        unload(CORNER);
        unload(PATCH);
        unload(FONT_20);
        unload(FONT_25);
    }
}
