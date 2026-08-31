package com.gdd.game.ecs.misc;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.entities.Entity;

import com.gdd.game.ecs.entities.EntityTag;
import com.gdd.game.ecs.entities.Transform;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.QueryCallback;

public class GameMechanics {

    public final GameWorld gw;

    public GameMechanics(GameWorld gw) {
        this.gw = gw;
    }

    public enum Action {
        ATTACK_ALL(30, 1), HEAL_ALL(15, 1), ATTACK_ENEMY(20, 3), HEAL_ALLIES(10, 3);

        public final int amount, cost;

        Action(int amount, int cost) {
            this.amount = amount;
            this.cost = cost;
        }
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
                switch (ctx.action) {
                    case HEAL_ALL -> {
                        health.heal(ctx.action.amount);
                    }
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
                }

                return true;
            }
        };

    public void action(Action action, Transform transform) {
        ctx.action = action;

        gw.world.queryAABB(callback,
                transform.x - transform.halfWidth, transform.y - transform.halfWidth,
                transform.x + transform.halfWidth, transform.y + transform.halfWidth);
    }
}
