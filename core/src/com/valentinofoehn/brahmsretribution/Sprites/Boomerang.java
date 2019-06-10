package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

/*
Boomerang is Brahms' weapon. Brahms can shoot boomerangs in the direction he is headed. When a
boomerang hits an enemy, it deals damage.
 */

public class Boomerang extends Sprite {

    protected World world;
    public Body body;
    protected Fixture fixture;
    private Brahms player;
    private PlayScreen playScreen;

    private Animation animation;
    private float x , y;
    private final int VELOCITY = 10000;
    private float stateTimer = 0;
    private Brahms.State state;
    private boolean toDestroy;
    private boolean destroyed;

    public Boomerang(World world, Brahms player, PlayScreen playScreen,
                     Brahms.State state, float x, float y) {
        super(new TextureRegion(new Texture("bullet.png"),0,0,16,19));

        this.world = world;
        this.player = player;
        this.playScreen = playScreen;
        this.state = state;
        this.x = x + 12;
        this.y = y + 16;
        initializeAnimations();
        defineBoomerang();
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

    private void defineBoomerang() {
        setPosition(x,y);
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(x, y);
        body = world.createBody(bdef);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = playScreen.CATEGORY_BULLET;
        fdef.filter.maskBits = playScreen.CATEGORY_ORDINARY;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
        setInitialVelocity();
    }

    public void update(float dt) {
        stateTimer += dt;
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
            setRegion(new TextureRegion(new Texture("soldier.png"),
                    209, 0, 16, 19));   //Transparent sprite

        }else{
            setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
            setRegion((TextureRegion)animation.getKeyFrame(stateTimer, true));
        }
    }

    private void setInitialVelocity() {
        switch(state){
            case LEFT_UP:
                body.setLinearVelocity(-(float)Math.sqrt(VELOCITY),(float)Math.sqrt(VELOCITY));
                break;
            case UP_RIGHT:
                body.setLinearVelocity((float)Math.sqrt(VELOCITY),(float)Math.sqrt(VELOCITY));
                break;
            case UP:
                body.setLinearVelocity(0,(float)Math.sqrt(VELOCITY));
                break;
            case RIGHT:
                body.setLinearVelocity((float)Math.sqrt(VELOCITY), 0);
                break;
            case DOWN_LEFT:
                body.setLinearVelocity(-(float)Math.sqrt(VELOCITY),-(float)Math.sqrt(VELOCITY));
                break;
            case RIGHT_DOWN:
                body.setLinearVelocity((float)Math.sqrt(VELOCITY),-(float)Math.sqrt(VELOCITY));
                break;
            case DOWN:
                body.setLinearVelocity(0,-(float)Math.sqrt(VELOCITY));
                break;
            case LEFT:
                body.setLinearVelocity(-(float)Math.sqrt(VELOCITY),0);
                break;
            default:
                body.setLinearVelocity(0,(float)Math.sqrt(VELOCITY)); // Default Brahms looking upwards
        }
    }

    public void collide(Fixture object) {
        if(object.getUserData() instanceof Soldier){
            ((Soldier)object.getUserData()).die();
        }else if(object.getUserData() instanceof Boss){
            ((Boss)object.getUserData()).takeDamage();
        }
        else if(object.getUserData().equals("feet") || object.getUserData().equals("head")){
            return;     //Initially the boomerang spawns within Brahms, but we want to ignore that body
        }
        destroyLocalBoomerang();
    }

    private void destroyLocalBoomerang() {
        toDestroy = true;
        setRegion(new TextureRegion(new Texture("soldier.png"), 209, 0, 16, 19));
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}