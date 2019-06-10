package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/*
Barrel blocks Brahms but lets enemy bullets pass.
 */

public class Barrel extends InteractiveTileObject {

    public Barrel(World world, TiledMap map, Brahms player, Rectangle bounds) {
        super(world, map, player, bounds, false);
        fixture.setUserData(this);
    }
}