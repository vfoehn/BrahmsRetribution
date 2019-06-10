package com.valentinofoehn.brahmsretribution.Scenes;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valentinofoehn.brahmsretribution.BrahmsRetribution;

/*
Message is used as a general class to display a message while the game is running. The text can be
customized.
 */

public class Message {

    public Stage stage;
    private Viewport viewport;
    private Label message;
    private Table table;
    private String textMessage;
    private float duration = 2;
    private boolean showMessage = false;
    private float timeShown = 0;

    public Message(SpriteBatch sb) {
        this.showMessage = true;
        viewport = new FitViewport(BrahmsRetribution.V_WIDTH, BrahmsRetribution.V_HEIGHT,
                new OrthographicCamera());
        stage = new Stage(viewport, sb);
        table = new Table();
        table.bottom();
        table.setFillParent(true);
        message = new Label(textMessage, new Label.LabelStyle(new BitmapFont(), Color.BLACK));
        table.add(message).expandX();
        stage.addActor(table);
    }

    public void setMessage(String textMessage, float duration) {
        this.textMessage = "";
        this.textMessage = textMessage;
        this.duration = duration;
        table.removeActor(message);
        message = new Label(textMessage, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        message.setAlignment(Align.center);
        table.add(message).expandX();
        showMessage = true;
        timeShown = 0;
    }

    public void update(float dt) {
        if(showMessage) {
            timeShown += dt;
            if (timeShown > duration){
                showMessage = false;
            }
        }
    }

    public boolean getShowMessage() {
        return showMessage;
    }

    public void setShowMessage(boolean showMessage) {
        this.showMessage = showMessage;
    }
}