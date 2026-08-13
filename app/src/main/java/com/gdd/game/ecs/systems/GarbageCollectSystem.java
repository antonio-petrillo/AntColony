package com.gdd.game.ecs.systems;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GarbageCollectSystem implements System {

    private final Set<Entity> toRemove = new HashSet<>(128);
    private final GameWorld gw;

    public GarbageCollectSystem(GameWorld gw) {
        this.gw = gw;
    }

    @Override
    public void update(List<Entity> entities, float dt) {
        for (var entity : entities) {
            var ai = (AiComponent) entity.getComponent(ComponentType.AI);
            if (ai != null && ai.canBeGarbageCollected) {
                if (ai.joint != null) {
                    if (ai.foodToPickup != null) {
                        var foodAi = (AiComponent) ai.foodToPickup.getComponent(ComponentType.AI);
                        assert (foodAi != null);
                        foodAi.pickedUp = false;
                        ai.foodToPickup = null;
                    }
                }
                toRemove.add(entity);
            }
        }
        if (!toRemove.isEmpty()) {
            for (var entity : toRemove) {
                var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
                if (phys == null) continue;

                gw.world.destroyBody(phys.body);
            }
            entities.removeAll(toRemove);
            toRemove.clear();
        }
    }
}
