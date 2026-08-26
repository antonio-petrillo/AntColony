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

    public float getSpeed() {
       return switch (this) {
           case ANT -> Entity.ANT_SPEED;
           case WASP -> Entity.WASP_SPEED;
           case NEST, EMPTY, FOOD, WALL -> {
               throw new IllegalArgumentException("get speed must be called on ANT or WASP tags");
           }
       };
    }
}
