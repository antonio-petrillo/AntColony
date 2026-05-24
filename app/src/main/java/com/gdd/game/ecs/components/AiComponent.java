package com.gdd.game.ecs.components;

import com.gdd.game.ecs.entities.Entity;
import com.google.fpl.liquidfun.Joint;
import com.google.fpl.liquidfun.Vec2;

// TODO: implement FSM
public class AiComponent extends Component {

    public enum State {
        NONE, // for food
        WANDER,
        COMBAT,
        GATHER,
        RETURN;

    }

    @Override
    public ComponentType type() {
        return ComponentType.AI;
    }

    public State previous = State.GATHER;
    public State current;
    public boolean isColliding = false;
    public float timeWanderAccumulator = 0.0f;
    public float timeBetweenActions;

    public boolean canBeGarbageCollected = false;
    public Entity foodToPickup = null; // ref to the food to pickup
    public boolean pickedUp = false; // indicate whether the food is picked up or not
    public Joint joint = null;

    public float timeBetweenAttacks = 0.25f;
    public float timeAttackAccumulator = 0.0f;

    public Entity enemyToAttack = null;
    public int attackPower;

    public AiComponent(State initial, float timeBetweenActions, int attackPower) {
        current = initial;
        this.timeBetweenActions = timeBetweenActions;
        this.attackPower = attackPower;
    }

    public AiComponent(State initial) {
        this(initial, 0.0f, 0);
    }

    public void transition(State state) {
        if (state == current) return;
        previous = current;
        current = state;
    }

    public void restore() {
        current = previous;
    }

}
