package com.gdd.game.ecs.components;

import com.google.fpl.liquidfun.Body;

public class PhysicComponent extends Component {
    public Body body;

    public PhysicComponent(Body body) {
        this.body = body;
    }

    @Override
    public ComponentType type() {
        return ComponentType.PHYSIC;
    }
}
