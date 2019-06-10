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
StoryScreen displays the message that explains the backstory to the game.
 */

public class StoryScreen implements Screen {

    private Viewport viewport;
    private Stage stage;
    private Game game;

    public StoryScreen(Game game) {
        this.game = game;
        viewport = new FitViewport(BrahmsRetribution.V_WIDTH, BrahmsRetribution.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, ((BrahmsRetribution) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Table table = new Table();
        table.center();
        table.setFillParent(true);
        Label label1 = new Label("Johann Strauss has stolen the score of Brahms'", font);
        Label label2 = new Label("newest Symphony. Help Brahms retrieve the score", font);
        Label label3 = new Label("so he can perform his music again.", font);
        table.add(label1).expandX();
        table.row();
        table.add(label2).expandX().padTop(10f);
        table.row();
        table.add(label3).expandX().padTop(10f);
        stage.addActor(table);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            game.setScreen((new PlayScreen((BrahmsRetribution) game)));
            dispose();
        }
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() { }

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
    }
}