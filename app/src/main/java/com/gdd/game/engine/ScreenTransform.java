package com.gdd.game.engine;

public class ScreenTransform {

    public float screenX, screenY, halfWidthPx, halfHeightPx, rotation;

    public void set(float sx, float sy, float hw, float hh, float rotDeg) {
        this.screenX = sx; this.screenY = sy;
        this.halfWidthPx = hw; this.halfHeightPx = hh;
        this.rotation = rotDeg;
    }
}
