package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

import java.util.LinkedList;

/*
Soldier is an enemy of Brahms. A soldier can shoot Bullets in the direction he is facing. A soldier
can be killed by Brahms using Boomerangs.
 */

public class Soldier extends Sprite {

    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    private Brahms player;
    private PlayScreen playScreen;

    private boolean lookingLeft;
    public enum State {STANDING, STATIC_SHOOTING, DEAD}
    private State state = State.STANDING;
    private Animation staticShooting;
    private TextureRegion standing; // Animation standing consists of 1 frame
    private float stateTimer = 0;
    private double shootingInterval;
    private float periodSinceLastShot = 0;
    private LinkedList<Bullet> bullets = new LinkedList<Bullet>();

    public Soldier(World world, TiledMap map, Rectangle bounds, Brahms player, PlayScreen playScreen,
                   boolean lookingLeft, String shootingInterval) {
        super(new TextureRegion(new Texture("soldier.png"), 60, 3, 24, 32));    //Default texture

        this.world = world;
        this.map = map;
        this.bounds = bounds;
        this.player = player;
        this.playScreen = playScreen;
        this.lookingLeft = lookingLeft;
        this.shootingInterval = Double.valueOf(shootingInterval);
        initializeAnimations();
        standing = new TextureRegion(new Texture("soldier.png"), 5, 3, 20, 32);
        defineSoldier();
    }

    private void initializeAnimations() {
        Texture soldierText = new Texture("soldier.png");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(soldierText,61, 1, 25, 32));
        frames.add(new TextureRegion(soldierText,89, 1, 25, 32));
        staticShooting = new Animation((float)shootingInterval / 2, frames);
        frames.clear();
    }

    private void defineSoldier() {
        setPosition(bounds.getX(), bounds.getY());
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
        body = world.createBody(bdef);
        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);   //Soldier has rectangular body
        fdef.filter.categoryBits = PlayScreen.CATEGORY_ORDINARY;
        fdef.filter.maskBits = PlayScreen.CATEGORY_BARREL | PlayScreen.CATEGORY_BULLET | PlayScreen.CATEGORY_ORDINARY;
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
        if(lookingLeft)
            flip(true,false);
    }

    public void update(float dt) {
        stateTimer += dt;

        for (Bullet bullet : bullets) {
            bullet.update(dt);
        }
        //If the soldier dies, the bullets still have to be tracked, but the rest becomes irrelevant
        if (!(state == State.DEAD)){
            //Start shooting when Brahms is in the vicinity
            if (Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2) < Math.pow(200, 2)) {
                state = State.STATIC_SHOOTING;
            }else {
                state = State.STANDING;
            }
            switch (state) {
                case STATIC_SHOOTING:
                    setRegion((TextureRegion) staticShooting.getKeyFrame(stateTimer, true));
                    periodSinceLastShot += dt;
                    break;
                default:
                    setRegion(standing);
            }
            if (periodSinceLastShot > shootingInterval) {
                periodSinceLastShot -= shootingInterval;
                if(lookingLeft)
                    bullets.add(new Bullet(world, map, player, playScreen, Brahms.State.LEFT,  getX(), getY(), true));
                else
                    bullets.add(new Bullet(world, map, player, playScreen, Brahms.State.RIGHT,  getX(), getY(), true));
            }
            flip(lookingLeft, false);
        }
    }

    public void die() {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run () {
                world.destroyBody(body);
            }
        });
        state = State.DEAD;
        setRegion(new TextureRegion(new Texture("soldier.png"), 209, 0, 20, 32));
    }

    public LinkedList<Bullet> getBullets() {
        return bullets;
    }
}
