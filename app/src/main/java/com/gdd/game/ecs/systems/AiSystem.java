package com.gdd.game.ecs.systems;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.RevoluteJointDef;
import com.google.fpl.liquidfun.Vec2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class AiSystem implements System {

    private static final Random rng = new Random();
    public final GameWorld gw;
    private final Vec2 nestPosition;
    private final Vec2 outsideTheWorld = new Vec2();
    private final float dropFoodDistance;
    public AiSystem(GameWorld gw, Vec2 nestPosition, float dropFoodDistance) {
        outsideTheWorld.setX(Float.POSITIVE_INFINITY);
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

            float x = phys.body.getPositionX();
            float y = phys.body.getPositionY();


            switch (entity.kind) {
                case ANT: ant(entity, phys, aiState, dt); break;
                case NEST: nest(dt); break;
                case WASP: break;
                case FOOD: break;
                case CARD: break;
            }
        }
    }

    public void ant(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        switch (aiState.current) {
            case WANDER: {
                if (aiState.foodToPickup != null) {
                    var jointDef = new RevoluteJointDef();
                    jointDef.setBodyA(phys.body);
                    jointDef.setBodyB(aiState.foodToPickup);

                    jointDef.setEnableMotor(true);
                    jointDef.setMotorSpeed(1.5f);
                    jointDef.setMaxMotorTorque(80f);

                    aiState.joint = gw.world.createJoint(jointDef);

                    jointDef.delete();
                    aiState.current = AiComponent.State.RETURN;
                    return;
                }

                if (aiState.isColliding) {
                    aiState.isColliding = false;

                    float nudge = Entity.ANT_MAX_STEERING_ANGLE * 0.5f;
                    float newDirection = phys.body.getAngle() + nudge;
                    phys.body.setTransform(
                            phys.body.getPositionX(),
                            phys.body.getPositionY(),
                            newDirection);

                }

                aiState.timeAccumulator += dt;
                if (aiState.timeAccumulator >= aiState.timeBetweenActions) {
                    aiState.timeAccumulator = 0.0f;
                    float newDirection = phys.body.getAngle() + rng.nextFloat(-Entity.ANT_MAX_STEERING_ANGLE, Entity.ANT_MAX_STEERING_ANGLE);
                    phys.body.setTransform(
                            phys.body.getPositionX(),
                            phys.body.getPositionY(),
                            newDirection
                    );
                }
                float angle = phys.body.getAngle();
                var vel = phys.body.getLinearVelocity();
                vel.setX(Entity.ANT_SPEED * (float) Math.cos(angle));
                vel.setY(Entity.ANT_SPEED * (float) Math.sin(angle));
                phys.body.setAngularVelocity(0);

            } break;
            case COMBAT: {
               // go toward enemy and fight like a true ant
            } break;
            case GATHER: {
               // go toward object
            } break;
            case RETURN: {
               // calc arctan with respect to nest pos and change direction of ant
                float x   = phys.body.getPositionX();
                float y   = phys.body.getPositionY();
                float dx = nestPosition.getX() - x;
                float dy = nestPosition.getY() - y;
                float distSquared = dx * dx + dy * dy;

                if (distSquared <= dropFoodDistance) {
                    aiState.current = AiComponent.State.WANDER;
                    aiState.timeAccumulator = 0f;

                    gw.world.destroyJoint(aiState.joint);
                    aiState.foodToPickup.setTransform(outsideTheWorld, 0); // place outside the world so it will be cleaned up by someone else
                    break;
                }

                aiState.timeAccumulator += dt;
                if (aiState.timeAccumulator >= aiState.timeBetweenActions) {
                    aiState.timeAccumulator = 0.0f;
                    float newDirection = phys.body.getAngle() + rng.nextFloat(-Entity.ANT_MAX_STEERING_ANGLE, Entity.ANT_MAX_STEERING_ANGLE);
                    phys.body.setTransform(
                            phys.body.getPositionX(),
                            phys.body.getPositionY(),
                            newDirection
                    );
                }
                float angle = phys.body.getAngle();
                var vel = phys.body.getLinearVelocity();
                vel.setX(Entity.ANT_SPEED * (float) Math.cos(angle));
                vel.setY(Entity.ANT_SPEED * (float) Math.sin(angle));
                phys.body.setAngularVelocity(0);

            }
        }
    }

    public void nest(float dt) {
        // just spawn ants
    }

}
