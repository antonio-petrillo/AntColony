package com.gdd.game.ecs.components;

// TODO: implement FSM
public class AiComponent extends Component {
    public enum State {
        WANDER,
        COMBAT,
        GATHER,
        RETURN;
    }

    @Override
    public ComponentType type() {
        return ComponentType.AI;
    }

    public State current;
    public boolean isColliding = false;
    public float timeAccumulator = 0.0f;
    public float timeBetweenActions;

    public AiComponent(State initial, float timeBetweenActions) {
        current = initial;
        this.timeBetweenActions = timeBetweenActions;
    }
}
