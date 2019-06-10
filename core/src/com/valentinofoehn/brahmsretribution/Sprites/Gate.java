package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;

/*
Gate is a part of the wall that opens once the Boss is defeated.
 */

public class Gate extends InteractiveTileObject {

    private boolean closed = true;
    private boolean destroyed, toDestroy;

    public Gate(World world, TiledMap map, Brahms player, Rectangle bounds) {
        super(world, map, player, bounds, false);
        fixture.setUserData(this);
    }

    public void update(float dt) {
        if(toDestroy && !destroyed){
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public void open() {
        if(closed) {
            // Get the two cells of the chest so we can create the "opening" animation.
            TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);        // Layer 1 is called the "graphics" layer
            TiledMapTileLayer.Cell cell0 = layer.getCell((int)(body.getPosition().x / 16),(int)(body.getPosition().y / 16));    // Right
            TiledMapTileLayer.Cell cell1 = layer.getCell((int)(body.getPosition().x / 16 - 1),(int)(body.getPosition().y / 16)); // Left
            cell0.setTile(null);
            cell1.setTile(null);

            closed = false;
            toDestroy = true;
        }
    }
}