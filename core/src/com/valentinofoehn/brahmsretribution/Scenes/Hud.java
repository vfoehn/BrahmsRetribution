package com.valentinofoehn.brahmsretribution.Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valentinofoehn.brahmsretribution.BrahmsRetribution;
import com.valentinofoehn.brahmsretribution.Sprites.Brahms;

/*
Hud (Heads Up Display) is always visible at the same place when the game is running. It displays how
much health and how many lives Brahms has.
 */

public class Hud {

    public Stage stage;
    private Viewport viewport;
    private Label livesLabel;
    private Label healthLabel;

    public Hud(SpriteBatch sb) {
        viewport = new FitViewport(BrahmsRetribution.V_WIDTH, BrahmsRetribution.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, sb);
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        livesLabel = new Label("LIVES: " + String.format("%02d", Brahms.lives),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        healthLabel = new Label("HEALTH: " + String.format("%03d",(int)Brahms.health),
                new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        table.add(livesLabel).expandX().padTop(10);
        table.add(healthLabel).expandX().padTop(10);
        stage.addActor(table);
    }

    public void update(float dt) {
        livesLabel.setText("LIVES: " + String.format("%02d", Brahms.lives));
        healthLabel.setText("HEALTH: " + String.format("%03d",(int)Brahms.health));
    }
}