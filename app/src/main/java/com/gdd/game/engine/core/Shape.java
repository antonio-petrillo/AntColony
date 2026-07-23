package com.gdd.game.engine.core;

public abstract class Shape {

    public static final class Circle extends Shape {

        // metri
        public float radius;

        public Circle(float radius) {
            this.radius = radius;
        }
    }

    public static final class Box extends Shape {

        // metri
        public float width, height;
        public float halfWidth, halfHeight;

        public Box(float w, float h) {
            this.width = w;
            this.height = h;
            this.halfWidth = w/2;
            this.halfHeight = h/2;

        }
    }

}