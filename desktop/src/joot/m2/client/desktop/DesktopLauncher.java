package joot.m2.client.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import joot.m2.client.JootM2C;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.resizable = true;
		config.width = 800;
		config.height = 600;
		config.title = "将唐传奇";
		config.addIcon("mir.jpg", FileType.Internal);
		new LwjglApplication(new JootM2C(), config);
	}
}