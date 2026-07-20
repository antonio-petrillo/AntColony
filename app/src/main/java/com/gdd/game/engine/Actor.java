package com.gdd.game.engine;

import com.gdd.game.engine.components.Component;
import com.gdd.game.engine.components.ComponentType;
import com.gdd.game.engine.components.TransformComponent;

import java.util.EnumMap;
import java.util.Map;

public class Actor {

    private TransformComponent transform;
    private Map<ComponentType, Component> components = new EnumMap<>(ComponentType.class);

    public Actor() {
        transform = new TransformComponent();
    }

    public void addComponent(Component c) {
        if(c.type() == ComponentType.TRANSFORM)
            return;

        c.setOwner(this);
        components.put(c.type(), c);
    }

    public Component getComponent(ComponentType type) {
        if(type == ComponentType.TRANSFORM)
            return transform;

        return components.get(type);
    }

    public TransformComponent getTransformComponent() {
        return transform;
    }
}
