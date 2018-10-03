package ch.asynk.gdx.tabletop.test;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new GdxBoardTest(), config);
        config.title = "gdx-tabletop Demo";
        config.width=800;
        config.height=600;
	}
}
