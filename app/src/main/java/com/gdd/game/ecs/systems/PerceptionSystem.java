package com.gdd.game.ecs.systems;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.EntityTag;

import java.util.List;
import java.util.ArrayList;

import com.google.fpl.liquidfun.Fixture;
import com.google.fpl.liquidfun.QueryCallback;

public class PerceptionSystem implements System {
    private final GameWorld gw;

    public PerceptionSystem(GameWorld gw) { this.gw = gw; }

    private static final class QueryContext {
        // Subject of the query
        Entity entity;
        EntityTag enemyTag;
        float x, y, facing, range, angle;
        boolean canGatherFood;

        // Result of the query
        Entity enemy;
        float enemyDistSquared;
        Entity food;
        float foodDistSquared;
    }

    private final QueryContext ctx = new QueryContext();

    private final QueryCallback callback = new QueryCallback() {
            @Override
            public boolean reportFixture(Fixture fixture) {
                var userData = fixture.getBody().getUserData();
                if (!(userData instanceof Entity other) || other == ctx.entity) return true;

                var otherPhys = (PhysicComponent) other.getComponent(ComponentType.PHYSIC);
                if (otherPhys == null) return true;

                float dx = otherPhys.body.getPositionX() - ctx.x;
                float dy = otherPhys.body.getPositionY() - ctx.y;
                float distSquared = dx * dx + dy * dy;

                if (distSquared > ctx.range * ctx.range) return true;

                if (!inFieldOfView(ctx.facing, dx, dy, ctx.angle)) return true;

                if (other.tag == ctx.enemyTag
                    && distSquared < ctx.enemyDistSquared) {
                    ctx.enemyDistSquared = distSquared;
                    ctx.enemy = other;
                } else if (other.tag == EntityTag.FOOD) {
                    var foodAi = (AiComponent) other.getComponent(ComponentType.AI);
                    if (foodAi == null || foodAi.pickedUp) return true;
                    if (distSquared < ctx.foodDistSquared) {
                        ctx.foodDistSquared = distSquared;
                        ctx.food = other;
                    }
                }
                return true;
            }
    };

    @Override
    public void update(List<Entity> entities, float dt) {
        for (var entity : entities) {
            var state = (AiComponent) entity.getComponent(ComponentType.AI);
            if (state == null) continue;

            if (state.current != AiComponent.State.WANDER
                    && state.current != AiComponent.State.GATHER
                    && state.current != AiComponent.State.CHASE) continue;

            var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
            if (phys == null) continue;

            switch (entity.tag) {
            case ANT: {
                ctx.range = Entity.ANT_VIEW_RANGE;
                ctx.angle = Entity.ANT_VIEW_ANGLE;
                ctx.enemyTag = EntityTag.WASP;
                ctx.canGatherFood = true;
            } break;
            case WASP: {
                ctx.range = Entity.WASP_VIEW_RANGE;
                ctx.angle = Entity.WASP_VIEW_ANGLE;
                ctx.enemyTag = EntityTag.ANT;
                ctx.canGatherFood = true;
            } break;
            default:
                continue;
            }

            ctx.entity = entity;
            ctx.x = phys.body.getPositionX();
            ctx.y = phys.body.getPositionY();
            ctx.facing = phys.body.getAngle();

            doQuery(state);
        }
    }

    private static final float TWO_PI = (float) (2 * Math.PI);
    private boolean inFieldOfView(float facing, float dx, float dy, float angle) {
        float angleToTarget = (float) Math.atan2(dy, dx);
        float diff = angleToTarget - facing;
        while (diff > Math.PI) diff -= TWO_PI;
        while (diff < -Math.PI) diff += TWO_PI;
        return Math.abs(diff) <= angle * 0.5f;
    }

    private void doQuery(AiComponent state) {
        // clear ctx for query
        ctx.enemy = null;
        ctx.enemyDistSquared = Float.POSITIVE_INFINITY;
        ctx.food = null;
        ctx.foodDistSquared = Float.POSITIVE_INFINITY;

        gw.world.queryAABB(callback,
                           ctx.x - ctx.range, ctx.y - ctx.range,
                           ctx.x + ctx.range, ctx.y + ctx.range);

        if (ctx.enemy != null) {
            state.foodInSight = null;
            state.enemyInSight = ctx.enemy;
            state.transition(AiComponent.State.CHASE, true);
        } else if (ctx.food != null) {
            state.foodInSight = ctx.food;
            state.enemyInSight = null;
            state.transition(AiComponent.State.GATHER, true);
        } else if (state.current == AiComponent.State.CHASE
        || state.current == AiComponent.State.GATHER) {
            state.enemyInSight = null;
            state.foodInSight = null;
            state.transition(AiComponent.State.WANDER, true);
        }
    }
}
