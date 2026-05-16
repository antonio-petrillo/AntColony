package com.gdd.game.ecs.entities;

import com.gdd.game.ecs.components.Component;
import com.gdd.game.ecs.components.ComponentType;

import java.util.EnumMap;
import java.util.Map;

public final class Entity {
    public Map<ComponentType, Component> components = new EnumMap<>(ComponentType.class);

    public void addComponent(Component comp) {
        comp.owner = this;
        components.put(comp.type(), comp);
    }

    public Component getComponent(ComponentType type) {
        return components.get(type);
    }
}
