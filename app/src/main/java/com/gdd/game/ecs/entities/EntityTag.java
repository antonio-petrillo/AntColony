package com.gdd.game.ecs.entities;

public enum EntityTag {
    EMPTY, ANT, WASP, NEST, FOOD, WALL;

    public boolean isObstacle() {
        return this == NEST || this == WALL;
    }

    // maybe `isAlive` is better but it get confused with the health and combat systems
    public boolean isInsect() {
       return this == ANT || this == WASP;
    }
}
