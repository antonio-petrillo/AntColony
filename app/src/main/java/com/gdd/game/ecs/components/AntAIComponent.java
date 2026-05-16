package com.gdd.game.ecs.components;

// TODO: implement FSM
public class AntAIComponent extends Component {
    public enum States {
        WANDER,
        COMBAT,
        TARGET_ACQUIRED,
        BACK_TO_THE_NEST
    }

    @Override
    public ComponentType type() {
        return ComponentType.AI;
    }
}
