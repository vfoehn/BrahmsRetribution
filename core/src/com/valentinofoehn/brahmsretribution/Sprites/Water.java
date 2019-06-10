package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/*
Water is a material on the floor that Brahms needs to traverse. If Brahms does not have a snorkel,
he will take damage for the whole duration that he is in the water.
 */

public class Water extends InteractiveTileObject {

    public Water(World world, TiledMap map, Brahms player, Rectangle bounds) {
        super(world, map, player, bounds, true);
        fixture.setUserData(this);
    }

    public void collide() {
        player.setInWater(player.getInWater() + 1);
    }

    public void diverge() {
        player.setInWater(player.getInWater() - 1);
    }
}