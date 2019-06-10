package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

/*
InteractiveTileObject is used to define physical properties for all the objects.
 */

public abstract class InteractiveTileObject {

    protected World world;
    protected Brahms player;
    protected TiledMap map;
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;

    public InteractiveTileObject(World world, TiledMap map, Brahms player, Rectangle bounds, boolean sensor) {
        this.world = world;
        this.player = player;
        this.map = map;
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set(bounds.getX() + bounds.getWidth() / 2, bounds.getY() + bounds.getHeight() / 2);

        body = world.createBody(bdef);

        shape.setAsBox(bounds.getWidth() / 2, bounds.getHeight() / 2);
        fdef.shape = shape;
        fdef.isSensor = sensor;
        fdef.filter.categoryBits = PlayScreen.CATEGORY_ORDINARY;
        fdef.filter.maskBits = PlayScreen.CATEGORY_BARREL | PlayScreen.CATEGORY_BULLET | PlayScreen.CATEGORY_ORDINARY;
        fixture = body.createFixture(fdef);
    }
}