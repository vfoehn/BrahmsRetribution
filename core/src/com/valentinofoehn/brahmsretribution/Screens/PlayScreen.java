package com.valentinofoehn.brahmsretribution.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.valentinofoehn.brahmsretribution.BrahmsRetribution;
import com.valentinofoehn.brahmsretribution.Scenes.ChestAdvice;
import com.valentinofoehn.brahmsretribution.Scenes.Hud;
import com.valentinofoehn.brahmsretribution.Scenes.Message;
import com.valentinofoehn.brahmsretribution.Sprites.Boomerang;
import com.valentinofoehn.brahmsretribution.Sprites.Boss;
import com.valentinofoehn.brahmsretribution.Sprites.Brahms;
import com.valentinofoehn.brahmsretribution.Sprites.Bullet;
import com.valentinofoehn.brahmsretribution.Sprites.Chest;
import com.valentinofoehn.brahmsretribution.Sprites.Gate;
import com.valentinofoehn.brahmsretribution.Sprites.Portal;
import com.valentinofoehn.brahmsretribution.Sprites.Soldier;
import com.valentinofoehn.brahmsretribution.Tools.B2WorldCreator;
import com.valentinofoehn.brahmsretribution.Tools.WorldContactListener;

import java.util.LinkedList;

/*
PlayScreen is responsible for the gameplay. It deals with the updating and rendering of the scene.
It also creates all the individual actors. These actors include the protagonist Brahms, his enemies
and many more interactive objects. Additionally, PlayScreen handles the user input.
 */

public class PlayScreen implements Screen {

    private BrahmsRetribution game;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;
    private Music music;
    private World world;
    private Box2DDebugRenderer b2dr;

    public Brahms player;
    public static Message message;
    public static Chest[] chests = new Chest[3];
    private ChestAdvice chestAdvice;
    private boolean firstTimeNearBoss;
    public static Portal portalA;
    public static Portal portalB;
    public boolean[] teleporting;   //teleporting[0] means portal with id 0 is currently being used
    public boolean ignoreInput;
    public LinkedList<Soldier> soldiers;
    public Boss boss;
    public Gate gate;
    public boolean gameOver;
    public static boolean levelCompleted;
    private boolean levelCompletedTimerStarted;
    private double levelCompletedTimerStart;

    // Mask so that bullets don't collide with barrels, but everything else does.
    public static final short CATEGORY_BULLET = 1;
    public static final short CATEGORY_BARREL = 2;
    public static final short CATEGORY_ORDINARY = 4;

    public PlayScreen(BrahmsRetribution game) {
        this.game = game;
        gamecam = new OrthographicCamera();
        gamePort = new FitViewport(BrahmsRetribution.V_WIDTH, BrahmsRetribution.V_HEIGHT, gamecam);
        hud = new Hud(game.batch);
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("map_1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map);
        gamecam.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight() / 2, 0);
        music = BrahmsRetribution.manager.get("music/Hungarian5.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
        world = new World(new Vector2(0,0), true);
        b2dr = new Box2DDebugRenderer();

        player = new Brahms(world, this);
        player.setHealth(100);
        player.throwBoomerang();
        chestAdvice = new ChestAdvice(game.batch);
        message = new Message(game.batch);
        firstTimeNearBoss = true;
        soldiers = new LinkedList<Soldier>();
        teleporting = new boolean[2];

        world.setContactListener(new WorldContactListener());
        new B2WorldCreator(world, map, player, this);
    }

    @Override
    public void show() {}

    public void update(float dt) {
        handleInput(dt);
        world.step(1/60f, 6, 2);

        player.update(dt);
        hud.update(dt);
        message.update(dt);
        boss.update(dt);
        gate.update(dt);
        portalA.update(dt);
        portalB.update(dt);
        if(teleporting[0])
            teleportBrahms(0);
        if(teleporting[1])
            teleportBrahms(1);
        for(Soldier soldier: soldiers){
            soldier.update(dt);
        }
        displayFinalBossMessage();
        isLevelCompleted();

        gamecam.position.x = player.getB2body().getPosition().x;
        gamecam.position.y = player.getB2body().getPosition().y;
        gamecam.update();
        renderer.setView(gamecam); // Only render view that is currently visible
    }

    public void handleInput(float dt) {
        if(ignoreInput)
            return;

        handleMovementInput();
        // Chests
        if(player.getNearChest()[0] && Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            chests[0].open();
        }
        if(player.getNearChest()[1] && Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            chests[1].open();
        }
        if(player.getNearChest()[2] && Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            chests[2].open();
        }

        // Bomerangs
        if (player.getHasBoomerangs() && Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            player.throwBoomerang();
        }
    }

    private void handleMovementInput() {
        //Check for diagonal movement first
        Brahms.State currentState = player.getCurrentState();
        float speed = player.getSpeed();
        float acceleration = player.getAcceleration();
        Body b2body = player.getB2body();

        if (Gdx.input.isKeyPressed(Input.Keys.UP) && Gdx.input.isKeyPressed(Input.Keys.RIGHT) &&
                b2body.getLinearVelocity().y <= speed) {
            if (currentState != Brahms.State.UP_RIGHT)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2((int) Math.sqrt(acceleration) + 1,
                    (int) Math.sqrt(acceleration) + 1), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.UP_RIGHT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && Gdx.input.isKeyPressed(Input.Keys.DOWN) &&
                b2body.getLinearVelocity().x <= speed) {
            if (currentState != Brahms.State.RIGHT_DOWN)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2((int) Math.sqrt(acceleration) + 1,
                    -(int) Math.sqrt(acceleration) + 1), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.RIGHT_DOWN);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && Gdx.input.isKeyPressed(Input.Keys.LEFT) &&
                b2body.getLinearVelocity().x >= -speed) {
            if (currentState != Brahms.State.DOWN_LEFT)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(-(int) Math.sqrt(acceleration) + 1,
                    -(int) Math.sqrt(acceleration) + 1), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.DOWN_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && Gdx.input.isKeyPressed(Input.Keys.UP) &&
                b2body.getLinearVelocity().x >= -speed) {
            if (currentState != Brahms.State.LEFT_UP)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(-(int) Math.sqrt(acceleration) + 1,
                    (int) Math.sqrt(acceleration) + 1), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.LEFT_UP);
        } else if (Gdx.input.isKeyPressed(Input.Keys.UP) && b2body.getLinearVelocity().y <= speed) {
            if (currentState != Brahms.State.UP)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(0, acceleration), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.UP);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && b2body.getLinearVelocity().x <= speed) {
            if (currentState != Brahms.State.RIGHT)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(acceleration, 0), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.RIGHT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && b2body.getLinearVelocity().y >= -speed) {
            if (currentState != Brahms.State.DOWN)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(0, -acceleration), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.DOWN);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && b2body.getLinearVelocity().x >= -speed) {
            if (currentState != Brahms.State.LEFT)
                b2body.setLinearVelocity(new Vector2(0, 0));
            b2body.applyLinearImpulse(new Vector2(-acceleration, 0), b2body.getWorldCenter(), true);
            player.setCurrentState(Brahms.State.LEFT);
        } else if (!Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)) {
            b2body.setLinearVelocity(new Vector2(0, 0));
        }
    }

    private void teleportBrahms(int id) {
        if(teleporting[0]) {
            player.setTransform(1693, 991);
            teleporting[0] = false;
        }else{
            player.setTransform(1373, 1471);
            teleporting[1] = false;
        }
        player.setCurrentState(Brahms.State.DOWN);
    }

    private void displayFinalBossMessage() {
        if (firstTimeNearBoss && player.getB2body().getPosition().x > 3200 && player.getB2body().getPosition().y > 580) {
            firstTimeNearBoss = false;
            message.setMessage("Defeat the final boss: Strauss' Fledermaus.\n" +
                    "Only then will the hidden gate be opened.", 6);
            message.setShowMessage(true);
            music.stop();
            music = BrahmsRetribution.manager.get("music/FledermausOverture.ogg", Music.class);
            music.play();
        }
    }

    private void isLevelCompleted() {
        if(levelCompleted) {
            if(!levelCompletedTimerStarted) {
                levelCompletedTimerStarted = true;
                levelCompletedTimerStart = System.currentTimeMillis();
            }
            if(System.currentTimeMillis() - levelCompletedTimerStart > 7000)
                game.setScreen((new LevelCompletedScreen(game)));
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        renderer.render();
        //b2dr.render(world, gamecam.combined); // Useful for debugging body collisions.
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();

        portalA.draw(game.batch);
        portalB.draw(game.batch);
        for(Soldier soldier: soldiers){
            soldier.draw(game.batch);
            for(Bullet bullet: soldier.getBullets()) {
                bullet.draw(game.batch);
            }
        }
        for(Boomerang boomerang: player.getBoomerangs()){
            if(!boomerang.isDestroyed())
                 boomerang.draw(game.batch);
        }
        if(!boss.isDestroyed())
            boss.draw(game.batch);
        player.draw(game.batch); // Draw player last, so it will be the top layer (apart from messages)

        game.batch.end();
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(player.getNearChest()[0] || player.getNearChest()[1] || player.getNearChest()[2]) {
            chestAdvice = new ChestAdvice(game.batch);
            game.batch.setProjectionMatrix(chestAdvice.stage.getCamera().combined);
            chestAdvice.stage.draw();
        }else{
            chestAdvice.stage.clear();
        }
        if(message.getShowMessage()){
            game.batch.setProjectionMatrix(message.stage.getCamera().combined);
            message.stage.draw();
        }
        if(gameOver){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
    }
}