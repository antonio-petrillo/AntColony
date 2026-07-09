package com.gdd.game;

import android.graphics.Canvas;

public interface CameraController {

    float screenToWorldX(float framebufferX);

    float screenToWorldY(float framebufferY);

    void pan(float dxFb, float dyFb);

    void beginPinch(float midFbX, float midFbY, float initialDistance);

    void updatePinch(float midFbX, float midFbY, float currentDistance);

    void endPinch();

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    void applyTransform(Canvas canvas);

    float getZoom();
}