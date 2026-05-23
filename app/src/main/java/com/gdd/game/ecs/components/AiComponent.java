package com.gdd.game.ecs.components;

import com.gdd.game.ecs.entities.Entity;
import com.google.fpl.liquidfun.Body;
import com.google.fpl.liquidfun.Joint;

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

    public Body foodToPickup = null; // ref to the food to pickup
    public boolean pickecUp = false; // indicate whether the food is picked up or not
    public Joint joint = null;

    public AiComponent(State initial, float timeBetweenActions) {
        current = initial;
        this.timeBetweenActions = timeBetweenActions;
    }
}
