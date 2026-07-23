package com.gdd.game.engine.core;

import com.gdd.game.engine.components.Component;
import com.gdd.game.engine.components.ComponentType;

import java.util.EnumMap;
import java.util.Map;

public class Actor {

    private final Transform transform;
    private Shape shape;
    private Map<ComponentType, Component> components = new EnumMap<>(ComponentType.class);

    public Actor() {
        transform = new Transform();
    }

    public Actor(float x, float y) {
        this();
        transform.x = x;
        transform.y = y;
    }

    public Actor(float x, float y, float angle) {
        this(x,y);
        transform.angle = angle;
    }


    public void addComponent(Component c) {
        c.setOwner(this);
        components.put(c.type(), c);
    }

    public Component getComponent(ComponentType type) {
        return components.get(type);
    }

    public Transform getTransform() {
        return transform;
    }

    public Shape getShape() { return shape; }

    public void setShape(Shape shape) { this.shape = shape; }
}