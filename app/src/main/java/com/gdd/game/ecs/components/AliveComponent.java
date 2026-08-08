package com.gdd.game.ecs.components;

public class AliveComponent extends Component {

    @Override
    public ComponentType type() {
        return ComponentType.ALIVE;
    }
}
