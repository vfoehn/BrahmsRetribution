package com.valentinofoehn.brahmsretribution.Sprites;

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

/*
Boss is the main enemy that protects the final gate against Brahms. Once the boss is killed, the
gate opens. The boss moves in a circle and kills Brahms if they collide.
 */

public class Boss extends Sprite {

    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    private Brahms player;
    private PlayScreen playScreen;

    private Animation animation;
    public enum State {UP, RIGHT, DOWN, LEFT}
    private State state = State.DOWN;
    private float velocity = 75;  // varies according to the spiral
    private int health = 1;
    private boolean toDestroy;
    private boolean destroyed;
    private boolean inactive = true;
    private float currentPeriodLength = 3f;
    private float stateTimer = 0;

    public Boss(World world, TiledMap map, Rectangle bounds, Brahms player, PlayScreen playScreen) {
        super(new TextureRegion(new Texture("bat.png"), 60, 3, 24, 32));

        this.world = world;
        this.map = map;
        this.bounds = bounds;
        this.player = player;
        this.playScreen = playScreen;
        initializeAnimations();
        defineBoss();
    }

    public void initializeAnimations() {
        Texture bossText = new Texture("bat.png");
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(bossText, 32*i, 0, 32, 32));
        }
        animation = new Animation(0.1f, frames);
        frames.clear();
    }

    public void defineBoss() {
        setPosition(bounds.getX(), bounds.getY());
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight()/1.25f);
        body = world.createBody(bdef);
        fdef.filter.categoryBits = PlayScreen.CATEGORY_ORDINARY;
        fdef.filter.maskBits = PlayScreen.CATEGORY_BARREL | PlayScreen.CATEGORY_BULLET | PlayScreen.CATEGORY_ORDINARY;
        fdef.shape = shape;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
        body.setLinearVelocity(0, -velocity);
        body.setActive(false);
    }

    public void update(float dt) {
        stateTimer += dt;

        //Boss has been defeated. Need to open up the wall
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
            playScreen.gate.open();
        }else {
            if (Math.pow(player.getX() - getX(), 2) + Math.pow(player.getY() - getY(), 2) < Math.pow(200, 2) && inactive) {
                inactive = false;
                body.setActive(true);
                state = State.DOWN;
                stateTimer = 0;
                body.setLinearVelocity(0, -velocity);
            }
            move();
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) animation.getKeyFrame(stateTimer, true));
        }
    }

    public void move(){
        if(stateTimer > currentPeriodLength){
            switch(state){
                case RIGHT:
                    body.setLinearVelocity(0, -velocity);
                    state = State.DOWN;
                    break;
                case DOWN:
                    body.setLinearVelocity(-velocity, 0);
                    state = State.LEFT;
                    break;
                case LEFT:
                    body.setLinearVelocity(0, velocity);
                    state = State.UP;
                    break;
                default:
                    body.setLinearVelocity(velocity, 0);
                    state = State.RIGHT;
            }
            stateTimer = 0;
        }
    }

    public void takeDamage() {
        if(health > 0){     //2 lives. Deduct one if hit.
            health--;
        }else {
            toDestroy = true;
            setRegion(new TextureRegion(new Texture("soldier.png"), 197, 0, 32, 32));
        }
    }

    public void collide(Fixture object) {
        if(object.getUserData().equals("feet") || object.getUserData().equals("head")){
            player.setHealth(0);
            playScreen.gameOver = true;
        }else if(object.getUserData() instanceof Barrel || object.getUserData() instanceof Bullet){
            return;
        }
    }

    public boolean isDestroyed(){
        return destroyed;
    }
}