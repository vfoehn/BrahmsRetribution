package com.valentinofoehn.brahmsretribution.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valentinofoehn.brahmsretribution.BrahmsRetribution;

/*
LevelCompleteScreen displays the message that the level is complete. It appears after Brahms has
opened the final chest.
 */

public class LevelCompletedScreen implements Screen{

    private Viewport viewport;
    private Stage stage;
    private Game game;

    public LevelCompletedScreen(Game game) {
        this.game = game;
        viewport = new FitViewport(BrahmsRetribution.V_WIDTH, BrahmsRetribution.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, ((BrahmsRetribution) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        Label levelCompleteLabel = new Label("LEVEL COMPLETED", font);
        table.add(levelCompleteLabel).expandX().center();
        table.row();
        stage.addActor(table);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}