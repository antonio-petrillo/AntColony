package com.gdd.game.engine;

public class ScreenParams {

    public float screenX, screenY, halfWidthPx, halfHeightPx, rotation;
    public float radius;

    public void set(float sx, float sy, float hw, float hh, float rotDeg, float radius) {
        this.screenX = sx;
        this.screenY = sy;
        this.halfWidthPx = hw;
        this.halfHeightPx = hh;
        this.rotation = rotDeg;
        this.radius = radius;
    }
}