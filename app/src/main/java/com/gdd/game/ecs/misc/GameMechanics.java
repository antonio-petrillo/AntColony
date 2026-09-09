package com.gdd.game.ecs.misc;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;

import com.gdd.game.ecs.entities.EntityTag;
import com.gdd.game.ecs.entities.Transform;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.QueryCallback;
import static  com.gdd.game.cards.Card.Action;

public class GameMechanics {

    public final GameWorld gw;

    public GameMechanics(GameWorld gw) {
        this.gw = gw;
    }


    private static final class QueryContext {
        Action action;
    }

    private final QueryContext ctx = new QueryContext();

    private final QueryCallback callback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                var userData = fixture.getBody().getUserData();
                if (!(userData instanceof Entity entity) || !entity.tag.isInsect()) return true;

                var health = (HealthComponent) entity.getComponent(ComponentType.HEALTH);
                assert (health != null);
                var ai = (AiComponent) entity.getComponent(ComponentType.AI);
                assert (ai != null);
                switch (ctx.action) {
                    case HEAL_ALL -> {
                        health.heal(ctx.action.amount);
                    }
                    // NOTE: heal only ants
                    case HEAL_ALLIES -> {
                        if (entity.tag == EntityTag.ANT) {
                            health.heal(ctx.action.amount);
                        }
                    }
                    case ATTACK_ALL -> {
                        health.takeDamage(ctx.action.amount);

                        if (!health.isAlive()) {
                            ai.canBeGarbageCollected = true;
                            ai.restore();
                            ai.enemyToAttack = null;
                        }
                    }
                    // NOTE: attack only wasps
                    case ATTACK_ENEMY -> {
                        if (entity.tag == EntityTag.WASP) {
                            health.takeDamage(ctx.action.amount);

                            if (!health.isAlive()) {
                                ai.canBeGarbageCollected = true;
                                ai.restore();
                                ai.enemyToAttack = null;
                            }
                        }
                    }
                    case DOUBLE_ENERGY -> {
                        //NOTE: just to silence compiler, this case is handled elsewhere
                    }
                    case INCREASE_FOV -> {
                       var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
                       assert(phys != null);

                       phys.speedModifier = 2.0f;
                       ai.timerFOVModifier = 3.0f;
                    }
                    // NOTE: applies only to ants
                    case DOUBLE_SPEED -> {
                        var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
                        assert (phys != null);
                        phys.speedModifier = 2.0f;
                        ai.timerSpeedModifier = 3.0f;
                    }
                }

                return true;
            }
        };

    public void action(Action action, Transform transform) {
        ctx.action = action;

        if (action == Action.DOUBLE_ENERGY) {
            gw.playerEnergy <<= 1;
            if (gw.playerEnergy > 100) {
                gw.playerEnergy = 100;
            }
            return;
        }

        gw.world.queryAABB(callback,
                transform.x - transform.halfWidth, transform.y - transform.halfWidth,
                transform.x + transform.halfWidth, transform.y + transform.halfWidth);
    }
}
