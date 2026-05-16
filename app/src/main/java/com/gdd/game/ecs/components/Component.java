package com.gdd.game.ecs.components;

import com.gdd.game.ecs.entities.Entity;

public abstract class Component {
    public Entity owner;

    public abstract ComponentType type();
}
