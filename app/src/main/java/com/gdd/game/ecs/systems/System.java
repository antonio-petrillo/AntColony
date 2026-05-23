package com.gdd.game.ecs.systems;

import com.gdd.game.ecs.entities.Entity;

import java.util.List;

public interface System {
    void update(List<Entity> entities, float dt);

}
