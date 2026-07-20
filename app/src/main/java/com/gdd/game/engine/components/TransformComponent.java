package com.gdd.game.engine.components;

public class TransformComponent extends Component {

    public float x, y, width, height, angle;

    @Override
    public final ComponentType type() {
        return ComponentType.TRANSFORM;
    }
}
