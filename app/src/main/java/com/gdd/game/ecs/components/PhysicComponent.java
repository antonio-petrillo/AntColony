package com.gdd.game.ecs.components;

import com.google.fpl.liquidfun.Body;

public class PhysicComponent extends Component {

    public Body body;
    public static float DEFAULT_SPEED_MODIFIER = 1.0f;
    public static float DEFAULT_FOV_PERCEPTION_MODIFIER = 1.0f;
    public float speedModifier = DEFAULT_SPEED_MODIFIER;
    public float fovPerceptionModifier = DEFAULT_FOV_PERCEPTION_MODIFIER;

    public PhysicComponent(Body body) {
        this.body = body;
    }

    @Override
    public ComponentType type() {
        return ComponentType.PHYSIC;
    }

    public void syncTransform() {
        owner.transform.x = body.getPositionX();
        owner.transform.y = body.getPositionY();
        owner.transform.angle = body.getAngle();
    }
}
