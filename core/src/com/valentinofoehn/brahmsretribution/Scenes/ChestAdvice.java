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

/*
ChestAdvice is used to display a message of advice after a chest has been opened.
 */

public class ChestAdvice {

    public Stage stage;
    private Viewport viewport;
    private Label advice;

    public ChestAdvice(SpriteBatch sb) {
        viewport = new FitViewport(BrahmsRetribution.V_WIDTH, BrahmsRetribution.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, sb);
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        advice = new Label("Press ENTER to Open the Chest.",
                new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        table.add(advice).expand().bottom();
        stage.addActor(table);
    }
}