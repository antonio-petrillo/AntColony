package com.gdd.game.ecs.systems;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

import java.util.List;
import java.util.Random;

public final class AiSystem implements System {

    private static final Random rng = new Random();
    public final GameWorld gw;
    private final Vec2 nestPosition;
    private final float dropFoodDistance;
    public AiSystem(GameWorld gw, Vec2 nestPosition, float dropFoodDistance) {
        this.gw = gw;
        this.nestPosition = nestPosition;
        this.dropFoodDistance = dropFoodDistance * dropFoodDistance;
    }

    @Override
    public void update(List<Entity> entities, float dt) {
        for (var entity : entities) {
            var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
            var aiState = (AiComponent) entity.getComponent(ComponentType.AI);

            if (aiState == null) continue;

            switch (entity.tag) {
                case ANT: ant(entity, phys, aiState, dt); break;
                case NEST: nest(dt); break;
                case WASP: wasp(entity, phys, aiState, dt); break;
                case FOOD: break;
            }
        }
    }

    public void steerToward(PhysicComponent phys, float targetX, float targetY, float speed) {
        var x = phys.body.getPositionX();
        var y = phys.body.getPositionY();
        var angle = (float) Math.atan2(targetY - y, targetX - x);
        phys.body.setTransform(x, y, angle);

        var vel = phys.body.getLinearVelocity();
        vel.setX(speed * (float) Math.cos(angle));
        vel.setY(speed * (float) Math.sin(angle));
        phys.body.setLinearVelocity(vel);
        phys.body.setAngularVelocity(0);
    }

    public void wander(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        if (aiState.isColliding) {
            aiState.isColliding = false;

            var newDirection = phys.body.getAngle() + (float) Math.PI
                    + rng.nextFloat(-Entity.WASP_MAX_STEERING_ANGLE / 2, Entity.WASP_MAX_STEERING_ANGLE / 2);
            phys.body.setTransform(phys.body.getPositionX(), phys.body.getPositionY(), newDirection);
            aiState.timeWanderAccumulator = 0;
        }
        aiState.timeWanderAccumulator += dt;
        if (aiState.timeWanderAccumulator >= aiState.timeBetweenActions) {
            aiState.timeWanderAccumulator = 0f;
            float newDir = phys.body.getAngle()
                    + rng.nextFloat(-Entity.WASP_MAX_STEERING_ANGLE, Entity.WASP_MAX_STEERING_ANGLE);
            phys.body.setTransform(
                    phys.body.getPositionX(),
                    phys.body.getPositionY(),
                    newDir);
        }
        float angle = phys.body.getAngle();
        var vel = phys.body.getLinearVelocity();
        vel.setX(entity.tag.getSpeed() * (float) Math.cos(angle));
        vel.setY(entity.tag.getSpeed() * (float) Math.sin(angle));
        phys.body.setAngularVelocity(0);
    }

    public void combat(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        var vel = phys.body.getLinearVelocity();
        vel.set(0, 0);
        phys.body.setLinearVelocity(vel);
        phys.body.setAngularVelocity(0);

        aiState.timeAttackAccumulator += dt;
        if (aiState.timeAttackAccumulator >= aiState.timeBetweenAttacks ) {
            aiState.timeAttackAccumulator = 0;
            if (aiState.enemyToAttack == null) return;
            var enemy = aiState.enemyToAttack;
            var healthEnemy = (HealthComponent) enemy.getComponent(ComponentType.HEALTH);
            assert(healthEnemy != null);
            healthEnemy.takeDamage(aiState.attackPower);

            if (!healthEnemy.isAlive()) {
                var enemyAi = (AiComponent) enemy.getComponent(ComponentType.AI);
                enemyAi.canBeGarbageCollected = true;
                aiState.restore();
                aiState.enemyToAttack = null;
            }
        }
        var health = (HealthComponent) entity.getComponent(ComponentType.HEALTH);
        aiState.canBeGarbageCollected = !health.isAlive();
    }
    public void chase(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        if (aiState.enemyInSight == null) {
            aiState.transition(AiComponent.State.WANDER, true);
            return;
        }
        var enemyPhys = (PhysicComponent) aiState.enemyInSight.getComponent(ComponentType.PHYSIC);
        assert(enemyPhys != null);
        steerToward(phys, enemyPhys.body.getPositionX(), enemyPhys.body.getPositionY(), Entity.WASP_SPEED);
    }
    public void gather(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        if (aiState.foodInSight == null) {
            aiState.transition(AiComponent.State.WANDER, true);
            return;
        }
        var foodPhys = (PhysicComponent) aiState.foodInSight.getComponent(ComponentType.PHYSIC);
        assert(foodPhys != null);
        steerToward(phys, foodPhys.body.getPositionX(), foodPhys.body.getPositionY(), Entity.ANT_SPEED);
    }

    public void wasp(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        switch (aiState.current) {
            case WANDER: {
                wander(entity, phys, aiState, dt);
            } break;
            case COMBAT: {
                // fight standing like a true here (more like die like an idiot)
                combat(entity, phys, aiState, dt);
            } break;
            case GATHER: {
                gather(entity, phys, aiState, dt);
            } break;
            case CHASE: {
                chase(entity, phys, aiState, dt);
            } break;
            default: break;
        }
    }

    public void ant(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        if (aiState.foodToPickup != null && aiState.joint == null) {
            var jointDef = new RevoluteJointDef();
            jointDef.setBodyA(phys.body);

            var foodPhys = (PhysicComponent) aiState.foodToPickup.getComponent(ComponentType.PHYSIC);
            assert (foodPhys != null);

            jointDef.setBodyB(foodPhys.body);
            jointDef.setLocalAnchorA(0.2f, 0);
            jointDef.setLocalAnchorB(0, 0);

            aiState.joint = gw.world.createJoint(jointDef);

            jointDef.delete();

            aiState.transition(AiComponent.State.RETURN);
            return;
        }
        switch (aiState.current) {
            case WANDER: {
                wander(entity, phys, aiState, dt);
            } break;
            case COMBAT: {
//                // go toward enemy and fight like a true ant
                combat(entity, phys, aiState, dt);
            } break;
            case GATHER: {
                gather(entity, phys, aiState, dt);
            } break;
            case CHASE: {
                chase(entity, phys, aiState, dt);
            } break;
            case RETURN: {
               // calc arctan with respect to nest pos and change direction of ant
                float x   = phys.body.getPositionX();
                float y   = phys.body.getPositionY();
                float dx = nestPosition.getX() - x;
                float dy = nestPosition.getY() - y;
                float distSquared = dx * dx + dy * dy;

                if (distSquared <= dropFoodDistance) {
                    aiState.transition(AiComponent.State.WANDER);
                    aiState.timeWanderAccumulator = 0f;

                    gw.world.destroyJoint(aiState.joint);
                    var foodAi = (AiComponent) aiState.foodToPickup.getComponent(ComponentType.AI);
                    foodAi.canBeGarbageCollected = true;
                    aiState.foodToPickup = null;
                    aiState.joint = null;
                    var vel = phys.body.getLinearVelocity();
                    vel.setX(0);
                    vel.setY(0);
                    phys.body.setLinearVelocity(vel);
                    phys.body.setAngularVelocity(0);

                    phys.body.setTransform(x, y, rng.nextFloat(30.0f) - 15.0f);
                    aiState.timeWanderAccumulator = aiState.timeBetweenActions + 1.0f;
                    gw.playerEnergy++; // TODO: clamp between 0 and max energy like 255
                    return;
                }

                var angleNest = (float) Math.atan2(dy, dx);

                phys.body.setTransform(x, y, angleNest);
                var vel = phys.body.getLinearVelocity();
                vel.setX(Entity.ANT_SPEED * (float) Math.cos(angleNest));
                vel.setY(Entity.ANT_SPEED * (float) Math.sin(angleNest));
                phys.body.setAngularVelocity(0);

            }
        }
    }

    public void nest(float dt) {
        // just spawn ants
    }

}