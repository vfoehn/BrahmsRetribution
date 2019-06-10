package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/*
Wall is a impenetrable object. It is used to constrain all actors within the world.
 */

public class Wall extends InteractiveTileObject { // Wall is not actually interactive
    public Wall(World world, TiledMap map, Brahms player, Rectangle bounds) {
        super(world, map, player, bounds, false);
        fixture.setUserData(this);
    }
}