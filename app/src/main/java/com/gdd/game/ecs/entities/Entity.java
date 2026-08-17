package com.gdd.game.ecs.entities;

import com.gdd.game.ecs.components.Component;
import com.gdd.game.ecs.components.ComponentType;

import java.util.EnumMap;
import java.util.Map;

public final class Entity {

    public static final float ANT_SPEED = 0.5f;
    public static final float ANT_MAX_STEERING_ANGLE = 1.0f;
    public static final float ANT_VIEW_ANGLE = (float) Math.toRadians(296); // From web search
    public static final float ANT_VIEW_RANGE = 0.5f;
    public static final float ANT_SPAWN_INTERVAL = 3.0f;
    public static final int ANT_MAX_COUNT = 250;

    public static final float WASP_SPEED = 0.8f;
    public static final float WASP_MAX_STEERING_ANGLE = 1.2f;
    public static final float WASP_VIEW_ANGLE = (float) Math.toRadians(300); // From web search
    public static final float WASP_VIEW_RANGE = 0.7f;
    public static final float WASP_SPAWN_INTERVAL = 3.0f;
    public static final int WASP_MAX_COUNT = 25;
    public static final float WASP_MAX_DEGREE_INWARD = (float) Math.toRadians(30);

    public static final float FOOD_SPAWN_INTERVAL = 3.0f;
    public static final int FOOD_MAX_COUNT = 15;

    public EntityTag tag;
    public Transform transform = new Transform();

    public Map<ComponentType, Component> components = new EnumMap<>(ComponentType.class);

    public Entity(EntityTag tag) {
        this.tag = tag;
    }

    public void addComponent(Component comp) {
        comp.owner = this;
        components.put(comp.type(), comp);
    }

    public Component getComponent(ComponentType type) {
        return components.get(type);
    }

}
