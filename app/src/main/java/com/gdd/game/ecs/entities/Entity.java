package com.gdd.game.ecs.entities;

import com.gdd.game.ecs.components.Component;
import com.gdd.game.ecs.components.ComponentType;

import java.util.EnumMap;
import java.util.Map;

public final class Entity {
    public enum Kind {
        ANT, NEST, WASP, FOOD, CARD;
    }

    public static final float ANT_SPEED = 0.5f;
    public static final float ANT_MAX_STEERING_ANGLE = 1.0f;
    public static final float WASP_SPEED = 0.8f;
    public static final float WASP_MAX_STEERING_ANGLE = 1.2f;


    public Map<ComponentType, Component> components = new EnumMap<>(ComponentType.class);
    public Kind kind;

    public Entity(Kind kind) {
        this.kind = kind;
    }

    public void addComponent(Component comp) {
        comp.owner = this;
        components.put(comp.type(), comp);
    }

    public Component getComponent(ComponentType type) {
        return components.get(type);
    }

}
