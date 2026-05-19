package com.gdd.game.ecs.misc;

import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.entities.Entity;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Contact;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.Fixture;

public class EntityContactListener extends ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fa = contact.getFixtureA(),
                fb = contact.getFixtureB();
        Body ba = fa.getBody(), bb = fb.getBody();
        Object userDataA = ba.getUserData(), userDataB = bb.getUserData();

        if (userDataA instanceof Entity entityA && userDataB instanceof Entity entityB) {
            if (entityA.kind == entityB.kind && entityA.kind == Entity.Kind.ANT) {
                AiComponent aiA = (AiComponent) entityA.getComponent(ComponentType.AI);
                AiComponent aiB = (AiComponent) entityB.getComponent(ComponentType.AI);

                // I assume that ANT always have AiComponent so aiA and aiB cannot be null;
                assert(aiA != null && aiB != null);

                aiA.isColliding = true;
                aiB.isColliding = true;
            }
        }

    }

}
