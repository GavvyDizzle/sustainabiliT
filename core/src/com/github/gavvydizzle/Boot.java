package com.github.gavvydizzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.github.gavvydizzle.screen.MazeScreen;
import com.github.gavvydizzle.screen.RainwaterScreen;
import com.github.gavvydizzle.screen.TrashScreen;

public class Boot extends Game {
	public static Boot instance;
	private int widthScreen, heightScreen;
	private OrthographicCamera camera;

	public Boot() {
		instance = this;
	}

	@Override
	public void create() {
		this.widthScreen = Gdx.graphics.getWidth();
		this.heightScreen = Gdx.graphics.getHeight();
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, widthScreen, heightScreen);

		setScreen(new TrashScreen(camera));
	}
}
