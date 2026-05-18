package com.gdd.game.ecs.systems;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
import com.gdd.game.ecs.entities.Entity;
import com.google.fpl.liquidfun.Vec2;

import java.util.List;
import java.util.Random;

public final class AiSystem implements System {

    private static final Random rng = new Random();
    public final GameWorld gw;

    public AiSystem(GameWorld gw) {
        this.gw = gw;
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

    public final float ANT_TIME_THRESHOLD = 3.0f;
    private final Vec2 antV = new Vec2(); // to avoid reallocation in update loop
    public void ant(Entity entity, PhysicComponent phys, AiComponent aiState, float dt) {
        switch (aiState.current) {
            case WANDER: {
                aiState.timeAccumulator += dt;
                if (aiState.timeAccumulator >= ANT_TIME_THRESHOLD) {
                    aiState.timeAccumulator = 0.0f;
                    float newdir = phys.body.getAngle() + rng.nextFloat(-Entity.ANT_MAX_STEERING_ANGLE, Entity.ANT_MAX_STEERING_ANGLE);
                    phys.body.setTransform(
                            phys.body.getPositionX(),
                            phys.body.getPositionY(),
                            newdir
                    );
                }
                float angle = (float) Math.toDegrees(phys.body.getAngle());
                antV.setX(Entity.ANT_SPEED * (float) Math.cos(angle));
                antV.setY(Entity.ANT_SPEED * (float) Math.sin(angle));
                phys.body.setLinearVelocity(antV);
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
            }
        }
    }

    public void nest(float dt) {
        // just spawn ants
    }

}
