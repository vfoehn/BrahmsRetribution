package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

/*
Bullet is the weapon of the Soldiers. The soldiers can shoot bullets in the direction they are
facing. When a bullet hits Brahms it deals damage.
 */

public class Bullet extends Sprite {

    protected World world;
    protected TiledMap map;
    public Body body;
    protected Fixture fixture;
    private Brahms player;
    private PlayScreen playScreen;

    private Brahms.State state;
    private Animation animation;
    public boolean isSoldier;
    private float x , y;
    private final int VELOCITY = 60;
    private float stateTimer = 0;
    private final int HORIZONTAL_OFFSET = 20;
    private final float LIFE_SPAN = 1.6f;
    private float timeLived = 0;
    public boolean toDestroy = false;
    public boolean destroyed = false;

    public Bullet(World world, TiledMap map, Brahms player, PlayScreen playScreen,
                  Brahms.State state, float x, float y, boolean isSoldier) {
        super(new TextureRegion(new Texture("bullet.png"),0,0,16,19)); // Default texture

        this.world = world;
        this.map = map;
        this.player = player;
        this.playScreen = playScreen;
        this.state = state;
        this.isSoldier = isSoldier;


        positionalOffset(x, y);
        initializeAnimations();
        defineBullet();
    }

    private void positionalOffset(float x, float y) {
        // Need offset depending in which direction the bullet is headed
        switch(state){
            case LEFT:
                this.x = x + 8 - HORIZONTAL_OFFSET;
                break;
            case RIGHT:
                this.x = x + 8 + HORIZONTAL_OFFSET;
                break;
            case UP_RIGHT:
                this.x = x + 8 + HORIZONTAL_OFFSET;
                break;
            case RIGHT_DOWN:
                this.x = x + 8 + HORIZONTAL_OFFSET;
                break;
            case DOWN_LEFT:
                this.x = x + 8 - HORIZONTAL_OFFSET;
                break;
            case LEFT_UP:
                this.x = x + 8 - HORIZONTAL_OFFSET;
                break;
        }
        this.y = y + 15;
    }

    private void initializeAnimations() {
        Texture bulletText = new Texture("bullet.png");
        Array<TextureRegion> frames = new Array<TextureRegion>();

        for(int i = 0; i < 3; i++){
            frames.add(new TextureRegion(bulletText,i*16, 0, 16, 19));
        }
        animation = new Animation(0.1f, frames);
        frames.clear();
    }

    private void defineBullet() {
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);    //half the width/height
        body = world.createBody(bdef);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = playScreen.CATEGORY_BULLET;
        fdef.filter.maskBits = playScreen.CATEGORY_ORDINARY;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
        setInitialVelocity();
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
    }

    public void update(float dt) {
        stateTimer += dt;
        timeLived += dt;

       if(timeLived >= LIFE_SPAN)
           toDestroy = true;
       if(toDestroy && !destroyed){
           world.destroyBody(body);
           destroyed = true;
       }else{
           setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
           setRegion((TextureRegion)animation.getKeyFrame(stateTimer, true));
       }
    }

    private void setInitialVelocity() {
        switch(state){
            case LEFT:
                body.applyLinearImpulse(new Vector2(-VELOCITY, 0), body.getWorldCenter(), true);
                break;
            case RIGHT:
                body.applyLinearImpulse(new Vector2(VELOCITY, 0), body.getWorldCenter(), true);
                break;
            case UP_RIGHT:
                body.applyLinearImpulse(new Vector2(VELOCITY, VELOCITY), body.getWorldCenter(), true);
                break;
            case RIGHT_DOWN:
                body.applyLinearImpulse(new Vector2(VELOCITY, -VELOCITY), body.getWorldCenter(), true);
                break;
            case DOWN_LEFT:
                body.applyLinearImpulse(new Vector2(-VELOCITY, -VELOCITY), body.getWorldCenter(), true);
                break;
            case LEFT_UP:
                body.applyLinearImpulse(new Vector2(-VELOCITY, VELOCITY), body.getWorldCenter(), true);
                break;
        }
    }

    public void collide(Fixture object) {
        if(object.getUserData().equals("feet") || object.getUserData().equals("head")){
            player.hitByBullet(20);
        }else if(object.getUserData() instanceof Barrel || object.getUserData() instanceof Boss || object.getUserData() instanceof Bullet){
            return;
        }
        destroyLocalBullet();
    }

    private void destroyLocalBullet() {
        toDestroy = true;
        setRegion(new TextureRegion(new Texture("soldier.png"), 209, 0, 16, 19));   //Transparent sprite
    }
}