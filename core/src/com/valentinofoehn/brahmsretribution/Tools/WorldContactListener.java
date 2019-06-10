package com.valentinofoehn.brahmsretribution.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.valentinofoehn.brahmsretribution.Sprites.Boomerang;
import com.valentinofoehn.brahmsretribution.Sprites.Boss;
import com.valentinofoehn.brahmsretribution.Sprites.Bullet;
import com.valentinofoehn.brahmsretribution.Sprites.Chest;
import com.valentinofoehn.brahmsretribution.Sprites.Portal;
import com.valentinofoehn.brahmsretribution.Sprites.Water;

/*
WorldContactListener is responsible for keeping track of which objects are in contact with which
other objects. Depending on the class of the two objects that are in contact, WorldContactListener
calls the correct methods that deal with the situation.
 */

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA.getUserData().equals("feet") || fixB.getUserData().equals("feet")){
            Fixture feet = fixA.getUserData().equals("feet") ? fixA : fixB;
            Fixture object = (feet == fixA) ? fixB : fixA;

            if(object.getUserData()instanceof Water){
                ((Water)object.getUserData()).collide();
            }else if(object.getUserData()instanceof Chest){
                ((Chest)object.getUserData()).collide();
            }else if(object.getUserData()instanceof Portal){
                ((Portal)object.getUserData()).collide();
            }
        }

        //Bullet collision
        if(fixA.getUserData() instanceof Bullet || fixB.getUserData() instanceof Bullet) {
            Fixture bullet = fixA.getUserData() instanceof Bullet ? fixA : fixB;
            Fixture object = (bullet == fixA) ? fixB : fixA;
            ((Bullet)bullet.getUserData()).collide(object);
        }

        //Boomerang collision
        if(fixA.getUserData() instanceof Boomerang || fixB.getUserData() instanceof Boomerang) {
            Fixture boomerang = fixA.getUserData() instanceof Boomerang ? fixA : fixB;
            Fixture object = (boomerang == fixA) ? fixB : fixA;
            ((Boomerang)boomerang.getUserData()).collide(object);
        }

        //Boss collision
        if(fixA.getUserData() instanceof Boss || fixB.getUserData() instanceof Boss) {
            Fixture boss = fixA.getUserData() instanceof Boss ? fixA : fixB;
            Fixture object = (boss == fixA) ? fixB : fixA;
            ((Boss)boss.getUserData()).collide(object);
        }
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA.getUserData().equals("feet") || fixB.getUserData().equals("feet")){
            Fixture feet = fixA.getUserData().equals("feet") ? fixA : fixB;
            Fixture object = (feet == fixA) ? fixB : fixA;

            if(object.getUserData()instanceof Water){
                ((Water)object.getUserData()).diverge();
            }else if(object.getUserData()instanceof Chest){
                ((Chest)object.getUserData()).diverge();
            }else if(object.getUserData()instanceof Portal){
                ((Portal)object.getUserData()).diverge();
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}