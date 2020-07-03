package ch.asynk.gdx.boardgame.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
    public static void main (String[] arg) {

        Tests tests = new Tests();
        tests.run();

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new GdxBoardTest(), config);
        config.title = "gdx-boardgame Demo";
        config.width=800;
        config.height=600;
    }
}
