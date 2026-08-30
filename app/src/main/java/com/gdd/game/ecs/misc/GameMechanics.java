package com.gdd.game.ecs.misc;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.entities.Entity;

import com.gdd.game.ecs.entities.EntityTag;
import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.QueryCallback;

public class GameMechanics {

    public final GameWorld gw;

    public GameMechanics(GameWorld gw) {
        this.gw = gw;
    }

    public enum Target {
        ALL, SELECTIVE;
    }

    public enum Action {
        ATTACK(25), HEAL(15);

        public final int amount;

        Action(int amount) {
            this.amount = amount;
        }
    }

    private static final class QueryContext {
        Action action;
        Target target;
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
                    case HEAL -> {
                        switch (ctx.target) {
                            case ALL -> {
                                health.heal(ctx.action.amount);
                            }
                            case SELECTIVE -> {
                                if (entity.tag == EntityTag.ANT) {
                                    health.heal(ctx.action.amount);
                                }
                            }
                        }
                    }
                    case ATTACK -> {
                        switch (ctx.target) {
                            case ALL -> {
                                health.takeDamage(ctx.action.amount);

                                if (!health.isAlive()) {
                                    ai.canBeGarbageCollected = true;
                                    ai.restore();
                                    ai.enemyToAttack = null;
                                }
                            }
                            case SELECTIVE -> {
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
                    }
                }

                return true;
            }
        };

    public void action(Action action, Target target, float x, float y, float width, float height) {
        ctx.action = action;
        ctx.target = target;

        gw.world.queryAABB(callback, x,  y, width, height);
    }
}
