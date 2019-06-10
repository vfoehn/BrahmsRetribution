package com.valentinofoehn.brahmsretribution.Tools;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.valentinofoehn.brahmsretribution.Screens.PlayScreen;
import com.valentinofoehn.brahmsretribution.Sprites.Barrel;
import com.valentinofoehn.brahmsretribution.Sprites.Boss;
import com.valentinofoehn.brahmsretribution.Sprites.Brahms;
import com.valentinofoehn.brahmsretribution.Sprites.Chest;
import com.valentinofoehn.brahmsretribution.Sprites.Gate;
import com.valentinofoehn.brahmsretribution.Sprites.Portal;
import com.valentinofoehn.brahmsretribution.Sprites.Soldier;
import com.valentinofoehn.brahmsretribution.Sprites.Wall;
import com.valentinofoehn.brahmsretribution.Sprites.Water;

/*
B2WorldCreator reads the different elements that define the map from a tmx file. This file was
created using the program 'Tiled'.
 */

public class B2WorldCreator {

    private final int numOfObjectTypes = 8;
    private final int tileCreatorOffset = 2;
    private boolean firstPortal = true;

    public  B2WorldCreator(World world, TiledMap map, Brahms player, PlayScreen playScreen){
        for(int i = 0; i < numOfObjectTypes; i++){
            for(MapObject object: map.getLayers().get(tileCreatorOffset + i).getObjects().getByType(RectangleMapObject.class)) {
                Rectangle rect = ((RectangleMapObject) object).getRectangle();

                switch(i){
                    case 0:
                        new Wall(world, map, player, rect);
                        break;
                    case 1:
                        new Water(world, map, player, rect);
                        break;
                    case 2:
                        int id = Integer.parseInt((String)object.getProperties().get("id"));
                        PlayScreen.chests[id] = new Chest(world, map, player, rect, id);
                        break;
                    case 3:
                        addPortal(world, map, player, playScreen, rect);
                        break;
                    case 4:
                        addSoldier(world, map, player, playScreen, rect, object);
                        break;
                    case 5:
                        new Barrel(world, map, player, rect);
                        break;
                    case 6:
                        playScreen.boss = new Boss(world, map, rect, player, playScreen);
                        break;
                    case 7:
                        playScreen.gate = new Gate(world, map, player, rect);
                        break;
                }
            }
        }
    }

    private void addPortal(World world, TiledMap map, Brahms player, PlayScreen playScreen, Rectangle rect) {
        if(firstPortal) {
            PlayScreen.portalA = new Portal(world, map, rect, player, playScreen, 0);
            firstPortal = false;
        }else {
            PlayScreen.portalB = new Portal(world, map, rect, player, playScreen, 1);
        }
    }

    private void addSoldier(World world, TiledMap map, Brahms player, PlayScreen playScreen, Rectangle rect, MapObject object) {
        if(object.getName().equals("lookingLeft")) {
            playScreen.soldiers.add(new Soldier(world, map, rect, player, playScreen,
                    true, (String)object.getProperties().get("shootingInterval")));
        } else {
            playScreen.soldiers.add(new Soldier(world, map, rect, player, playScreen,
                    false, (String) object.getProperties().get("shootingInterval")));
        }
    }
}