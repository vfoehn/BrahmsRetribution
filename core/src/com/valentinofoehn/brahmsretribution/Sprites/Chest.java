package com.valentinofoehn.brahmsretribution.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;

/*
Chest is an object that can be opened by Brahms. Each chest contains a different item.
 */

public class Chest extends InteractiveTileObject {

    private boolean full = true;
    private int id;

    public Chest(World world, TiledMap map, Brahms player, Rectangle bounds, int id) {
        super(world, map, player, bounds, true);
        this.id = id;
        fixture.setUserData(this);
    }

    public void collide() {
        player.setNearChest(id, true);
        PlayScreen.chests[id] = this;
    }

    public void diverge() {
        player.setNearChest(id, false);
    }

    public void open() {
        if(full) {
            // Get the four cells of the chest so we can create the "opening" animation.
            TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1); // Layer 1 is called the "graphics" layer
            TiledMapTileLayer.Cell cell0 = layer.getCell((int)(body.getPosition().x / 16),(int)(body.getPosition().y / 16));    // bottom right
            TiledMapTileLayer.Cell cell1 = layer.getCell((int)(body.getPosition().x / 16 - 1),(int)(body.getPosition().y / 16)); // bottom left
            TiledMapTileLayer.Cell cell2 = layer.getCell((int)(body.getPosition().x / 16 - 1),(int)(body.getPosition().y / 16 + 1)); // top left
            TiledMapTileLayer.Cell cell3 = layer.getCell((int)(body.getPosition().x / 16),(int)(body.getPosition().y / 16 + 1)); // top right

            full = false;

            TiledMapTileSet tileset = map.getTileSets().getTileSet(2);
            tileset.getName();
            cell0.setTile(tileset.getTile(576));
            cell1.setTile(tileset.getTile(575));
            cell2.setTile(tileset.getTile(571));
            cell3.setTile(tileset.getTile(572));

            returnItem();
        }
    }

    private void returnItem() {
        if(id == 0) {
            PlayScreen.message.setMessage("You have found the holy snorkel.\n" +
                    "You are now immune to water.", 5);
            player.setWaterDamage(0);
        }else if(id == 1){
            PlayScreen.message.setMessage("You have found the holy boomerang.\n" +
                    "You can now shoot bullets using SPACE.", 5);
            player.setHasBoomerangs(true);
        }else if(id == 2){
            PlayScreen.message.setMessage("You have found the score of Brahms'\n" +
                    "Symphony No. 3.\n" +
                    "At last, Brahms can return\n" +
                    "to performing his music.", 7);
            PlayScreen.levelCompleted = true;
        }
    }
}