package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

import java.util.LinkedList;

/*
Brahms is the main character of the game. He can move around the map and shoot boomerangs. He can
also take damage if he is shot by an enemy or walks through water.
 */

public class Brahms extends Sprite {

    private PlayScreen playScreen;
    private World world;
    private Body b2body;

    public enum State {UP, RIGHT, DOWN, LEFT, UP_RIGHT, RIGHT_DOWN, DOWN_LEFT, LEFT_UP}
    private State currentState;
    private State previousState;
    private boolean standing;
    private float stateTimer;
    private Animation[] brahmsRun; // 0: UP, 1: RIGHT, 2: DOWN, 3: LEFT, 4-7: diagonal movement

    private float speed;
    private float acceleration;
    public static double health;
    public static Integer lives;
    private int inWater; // Number of different water object the feet are in. inWater == 0 means Brahms is not touching any water
    private float timeInWater;
    private double waterDamage = 100.0 / 4;
    private boolean[] nearChest = new boolean[3];
    private LinkedList<Boomerang> boomerangs;
    private boolean hasBoomerangs;
    private final double SHOOTING_INTERVAL = 0.5f;
    private float periodSinceLastShot = 2f; //This means we are ready to shoot initially

    public Brahms(World world, PlayScreen playScreen) {
        super(new TextureRegion(new Texture("brahms.png"), 0, 0, 24, 32));
        this.world = world;
        this.playScreen = playScreen;

        currentState = State.UP;
        previousState = State.UP;
        standing = true;
        stateTimer = 0;
        brahmsRun = new Animation[8];
        initializeAnimations();

        speed = 10000;
        acceleration = 50f;
        health = 100;
        lives = 3;
        inWater = 0;
        timeInWater = 0;
        boomerangs = new LinkedList<Boomerang>();
        hasBoomerangs = false;
        defineBrahms();
    }

    private void initializeAnimations() {
        Texture brahmsText = new Texture("brahms.png");
        Array<TextureRegion> frames;
        for (int j = 0; j < 4; j++) {
            frames = new Array<TextureRegion>();
            for (int i = 0; i < 4; i++) {
                frames.add(new TextureRegion(brahmsText, i * 3 * 24, j * 32, 24, 32));
            }
            for (int i = 0; i < 4; i++) {
                frames.add(new TextureRegion(brahmsText, i * 3 * 24, 4 * 32 + j * 32, 24, 32));
            }
            brahmsRun[j] = new Animation(0.1f, frames);
            frames.clear();
        }
    }

    private void defineBrahms() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(500, 250);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape1 = new CircleShape();
        shape1.setRadius(7);
        shape1.setPosition(new Vector2(getX(), getY() - 8));
        fdef.shape = shape1;
        b2body.createFixture(fdef).setUserData("feet");

        CircleShape shape2 = new CircleShape();
        shape2.setRadius(6);
        shape2.setPosition(new Vector2(getX(), getY() + 2));
        fdef.shape = shape2;
        fdef.isSensor = true;
        fdef.filter.categoryBits = playScreen.CATEGORY_ORDINARY;
        fdef.filter.maskBits = PlayScreen.CATEGORY_BARREL | PlayScreen.CATEGORY_BULLET | PlayScreen.CATEGORY_ORDINARY;
        b2body.createFixture(fdef).setUserData("head");
    }

    public void update(float dt) {
        updateWater(dt);
        updateBoomerangs(dt);

        //When Brahms stops moving, we might need to adjust the state
        if (b2body.getLinearVelocity().x == 0 && b2body.getLinearVelocity().y == 0) {
            switch (currentState) {
                case LEFT_UP:
                case UP_RIGHT:
                    currentState = State.UP;
                    break;
                case DOWN_LEFT:
                case RIGHT_DOWN:
                    currentState = State.DOWN;
                    break;
            }
        }
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        standing = b2body.getLinearVelocity().isZero();
        setRegion(getFrame(dt));
        if (health <= 0)
            playScreen.gameOver = true;
    }

    // Check for how long Brahms has been standing in water and calculate the damage accordingly.
    private void updateWater(float dt) {
        if (inWater > 0) {
            timeInWater += dt;
            if (timeInWater > 0.25) {
                timeInWater -= 0.5;
                health -= waterDamage;
            }
        } else {
            timeInWater = 0;
        }
    }

    private void updateBoomerangs(float dt) {
        for (Boomerang boomerang : boomerangs) {
            boomerang.update(dt);
        }
        periodSinceLastShot += dt;
    }

    private TextureRegion getFrame(float dt) {
        TextureRegion region;
        if (standing)
            stateTimer = 0;

        switch (currentState) {
            case LEFT_UP:
            case UP_RIGHT:
            case UP:
                region = (TextureRegion) brahmsRun[0].getKeyFrame(stateTimer, true);
                break;
            case RIGHT:
                region = (TextureRegion) brahmsRun[1].getKeyFrame(stateTimer, true);
                break;
            case DOWN_LEFT:
            case RIGHT_DOWN:
            case DOWN:
                region = (TextureRegion) brahmsRun[2].getKeyFrame(stateTimer, true);
                break;
            case LEFT:
                region = (TextureRegion) brahmsRun[3].getKeyFrame(stateTimer, true);
                break;
            default:
                // Standard Brahms looking upwards
                region = new TextureRegion(new Texture("brahms.png"), 0, 0, 24, 32);
        }
        stateTimer = (currentState == previousState) ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    public void throwBoomerang() {
        if (periodSinceLastShot > SHOOTING_INTERVAL) {
            boomerangs.add(new Boomerang(world, this, playScreen, currentState, getX(), getY()));
            periodSinceLastShot = 0;
        }
    }

    public void hitByBullet(double damage) {
        health -= damage;
    }

    public void setTransform(float x, float y) {
        this.b2body.setTransform(x, y, 0);
    }

    // The following are only getters and setters for the attributes of this class:

    public Body getB2body() {
        return b2body;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public float getSpeed() {
        return speed;
    }

    public float getAcceleration() {
        return acceleration;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public int getInWater() {
        return inWater;
    }

    public void setInWater(int inWater) {
        this.inWater = inWater;
    }

    public void setWaterDamage(float waterDamage) {
        this.waterDamage = waterDamage;
    }

    public boolean[] getNearChest() {
        return nearChest;
    }

    public void setNearChest(int index, boolean nearChest) {
        this.nearChest[index] = nearChest;
    }

    public boolean getHasBoomerangs() {
        return hasBoomerangs;
    }

    public void setHasBoomerangs(boolean hasBoomerangs) {
        this.hasBoomerangs = hasBoomerangs;
    }

    public LinkedList<Boomerang> getBoomerangs() {
        return boomerangs;
    }
}