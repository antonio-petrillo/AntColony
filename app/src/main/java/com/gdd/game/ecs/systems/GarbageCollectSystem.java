package com.gdd.game.ecs.systems;

import com.gdd.game.Box;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;

import java.util.List;
public record GarbageCollectSystem(GameWorld gw) implements System {

    @Override
    public void update(List<Entity> entities, float dt) {
        Box worldSize = gw.physicalSize;
        var iter = entities.iterator();
        while (iter.hasNext()) {
            var entity = iter.next();
            var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
            if (phys != null) {
                float x = phys.body.getPositionX();
                float y = phys.body.getPositionY();

                if (x < worldSize.xmin || x > worldSize.xmax || y < worldSize.ymin || y > worldSize.ymax) {
                    iter.remove();
                }
            }

            var ai = (AiComponent) entity.getComponent(ComponentType.AI);
            if (ai != null && ai.canBeGarbageCollected) {
                iter.remove();
            }
        }
    }
}
