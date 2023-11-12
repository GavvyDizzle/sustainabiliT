package com.github.gavvydizzle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.github.gavvydizzle.screen.MazeScreen;
import com.github.gavvydizzle.screen.RainwaterScreen;
import com.github.gavvydizzle.screen.TrashScreen;

public class Boot extends Game {
	public static Boot instance;
	private int widthScreen, heightScreen;
	private OrthographicCamera camera;

	private final Array<Integer> screen;

	public Boot() {
		instance = this;
		screen = new Array<>();
		for (int i = 0; i < 3; i++) {
			screen.add(i);
		}
	}

	@Override
	public void create() {
		this.widthScreen = Gdx.graphics.getWidth();
		this.heightScreen = Gdx.graphics.getHeight();
		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(false, widthScreen, heightScreen);

		setRandomScreen();
	}

	public void setRandomScreen() {
		if (screen.isEmpty()) {
			System.out.println("WINNER");
			Gdx.app.exit();
		}

		int rand = screen.random();
		screen.removeValue(rand, false);

		switch (rand) {
			case 0 -> setScreen(new MazeScreen(camera));
			case 1 -> setScreen(new RainwaterScreen(camera));
			case 2 -> setScreen(new TrashScreen(camera));
		}
	}
}
