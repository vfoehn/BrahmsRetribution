package com.valentinofoehn.brahmsretribution;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.valentinofoehn.brahmsretribution.Screens.StartGameScreen;

/*
BrahmsRetribution is the main class for the game. As such, an instance of BrahmsRetribution is
directly created when DesktopLauncher.java is first called. This class manages the audio resources
and starts the game.
 */

public class BrahmsRetribution extends Game {

	public static final int V_WIDTH = 400;
	public static final int V_HEIGHT = 208;
	public SpriteBatch batch;
	public static AssetManager manager;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		manager = new AssetManager();
		manager.load("music/Hungarian5.ogg", Music.class);
		manager.load("music/FledermausOverture.ogg", Music.class);
		manager.load("sound/portal.ogg", Sound.class);
		manager.finishLoading();
		setScreen(new StartGameScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}