package com.gdd.game.ecs.misc;

import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.EntityTag;
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
            if (entityA.tag == entityB.tag && entityA.tag == EntityTag.ANT) {
                AiComponent aiA = (AiComponent) entityA.getComponent(ComponentType.AI);
                AiComponent aiB = (AiComponent) entityB.getComponent(ComponentType.AI);

                // I assume that ANT always have AiComponent so aiA and aiB cannot be null;
                assert(aiA != null && aiB != null);

                aiA.isColliding = true;
                aiB.isColliding = true;
            } else if (entityA.tag == EntityTag.WASP && entityB.tag == EntityTag.FOOD
                    || entityA.tag == EntityTag.FOOD && entityB.tag == EntityTag.WASP) {
               AiComponent food;
               HealthComponent wasp;
               if (entityA.tag == EntityTag.WASP) {
                   wasp = (HealthComponent) entityA.getComponent(ComponentType.HEALTH);
                   food = (AiComponent) entityB.getComponent(ComponentType.AI);
               } else {
                   food = (AiComponent) entityA.getComponent(ComponentType.AI);
                   wasp = (HealthComponent) entityB.getComponent(ComponentType.HEALTH);
               }
               assert(wasp != null);
               assert(food != null);

               if (food.pickedUp) return;

               food.pickedUp = true;
               food.canBeGarbageCollected = true;
               // TODO: remove hardcode values
               wasp.heal(10);

            } else if (entityA.tag == EntityTag.ANT && entityB.tag == EntityTag.FOOD
                    || entityA.tag == EntityTag.FOOD && entityB.tag == EntityTag.ANT) {

                var ant = entityA.tag == EntityTag.ANT ? entityA : entityB;
                var food = entityA.tag == EntityTag.FOOD ? entityA : entityB;
                var antAi = (AiComponent) ant.getComponent(ComponentType.AI);
                var foodAi = (AiComponent) food.getComponent(ComponentType.AI);
                if (foodAi.pickedUp) return;
                assert(antAi != null);

                if (antAi.foodToPickup != null) return;
                foodAi.pickedUp = true;
                antAi.foodToPickup = food;
            } else if (entityA.tag == EntityTag.ANT && entityB.tag == EntityTag.WASP
                    || entityA.tag == EntityTag.WASP && entityB.tag == EntityTag.ANT) {

                var ant = entityA.tag == EntityTag.ANT ? entityA : entityB;
                var wasp = entityA.tag == EntityTag.WASP ? entityA : entityB;

                var antAi = (AiComponent) ant.getComponent(ComponentType.AI);
                var waspAi = (AiComponent) wasp.getComponent(ComponentType.AI);

                antAi.transition(AiComponent.State.COMBAT);
                waspAi.transition(AiComponent.State.COMBAT);

                if (antAi.enemyToAttack == null) {
                    antAi.enemyToAttack = wasp;
                }
                if (waspAi.enemyToAttack == null) {
                    waspAi.enemyToAttack = ant;
                }
            } else if (entityA.tag.isInsect() && entityB.tag.isObstacle()
                    || entityA.tag.isObstacle() && entityB.tag.isInsect()) {
                var insect = entityA.tag.isInsect() ? entityA : entityB;
                var ai = (AiComponent) insect.getComponent(ComponentType.AI);
                assert (ai != null);
                ai.isColliding = true;
            }
        }

    }

}
