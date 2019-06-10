package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
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
import com.valentinofoehn.brahmsretribution.BrahmsRetribution;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

/*
Portal teleports Brahms from one side to the other if he stands on it for a brief period of time.
 */

public class Portal extends Sprite {

    protected World world;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    private Brahms player;
    private PlayScreen playScreen;

    private Animation staticPortal, dynamicPortal;
    private float stateTimer = 0;
    private boolean teleportingAnimation = false;
    private final float TELEPORT_WAIT_TIME = 1.5f; // Time it takes until teleportation process starts
    private final float TELEPORT_TIME = 1.5f; // Time it takes to teleport
    private float waitingTime = 0; // Time spent waiting on the portal
    private boolean wantToTeleport = false;
    private int id;

    public Portal(World world, TiledMap map, Rectangle bounds, Brahms player, PlayScreen playScreen, int id) {
        super(new TextureRegion(new Texture("brahms.png"),0,0, 24, 32));    //Default texture
        this.world = world;
        this.map = map;
        this.bounds = bounds;
        this.player = player;
        this.playScreen = playScreen;
        this.id = id;

        initializeAnimations();
        definePortal();
    }

    private void initializeAnimations() {
        Array<TextureRegion> frames = new Array<TextureRegion>();

        //dynamicPortal
        Texture portalText = new Texture("portalRings2.png");
        for(int i = 0; i < 5; i++){
            frames.add(new TextureRegion(portalText,i*32, 0, 32, 32));      //Portal is 32x32
        }
        dynamicPortal = new Animation(0.1f,frames);
        frames.clear();

        //staticPortal
        portalText = new Texture("portalRings1.png");
        for(int j = 0; j < 4; j++) {
            for (int i = 0; i < 4; i++) {
                frames.add(new TextureRegion(portalText, i * 32, j * 32, 32, 32));
            }
        }
        staticPortal = new Animation(0.1f,frames);
    }

    private void definePortal() {
        setPosition(bounds.getX(), bounds.getY());
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);
        body = world.createBody(bdef);
        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
        fdef.shape = shape;
        fdef.isSensor = true;
        fixture = body.createFixture(fdef);
        fixture.setUserData(this);
    }

    public void update(float dt) {
        if(wantToTeleport){
            waitingTime += dt;
            BrahmsRetribution.manager.get("sound/portal.ogg", Sound.class).play();
        }

        // We first wait for brahms to settle on the portal
        if(waitingTime > TELEPORT_WAIT_TIME) {
            teleportingAnimation = true;
            playScreen.ignoreInput = true; // Brahms can no longer move, because he is about to teleport
        }
        // Now we initiate teleportation
        if(waitingTime > TELEPORT_WAIT_TIME + TELEPORT_TIME) {
            teleportingAnimation = false;
            playScreen.ignoreInput = false; // Brahms can no longer move, because he is about to teleport
            playScreen.teleporting[id] = true;
        }

        if(teleportingAnimation) {
            setRegion((TextureRegion)staticPortal.getKeyFrame(stateTimer, true));
        }else{
            setRegion((TextureRegion)dynamicPortal.getKeyFrame(stateTimer, true));
        }
        stateTimer += dt;
    }

    public void collide() {
        wantToTeleport = true;
    }

    public void diverge() {
        wantToTeleport = false;
        waitingTime = 0;
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
    }
}