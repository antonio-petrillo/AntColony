package com.gdd.game;

import android.graphics.Canvas;
import android.graphics.Matrix;

public class WorldCameraController implements CameraController {

    private final Box cameraView;
    private final float worldWidth, worldHeight;

    private float centerX = 0f, centerY = 0f;
    private float zoom = 1f;
    private float minZoom = 1f;
    private float maxZoom = 6f;

    private final int viewportWidthPx, viewportHeightPx;

    private boolean pinching = false;
    private float pinchStartDistancePx;
    private float zoomAtPinchStart;
    private float anchorWorldX, anchorWorldY;

    public WorldCameraController(Box cameraView, float worldWidth, float worldHeight,
                                 int viewportWidthPx, int viewportHeightPx) {
        this.cameraView = cameraView;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.viewportWidthPx = Math.max(viewportWidthPx, 1);
        this.viewportHeightPx = Math.max(viewportHeightPx, 1);
        applyToCameraView();
    }

    // ------------------------------------------------------------------
    // Configurazione
    // ------------------------------------------------------------------

    public void setZoomLimits(float minZoom, float maxZoom) {
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        zoom = clamp(zoom, minZoom, maxZoom);
        applyToCameraView();
    }

    public void setCamera(float centerX, float centerY, float zoom) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.zoom = clamp(zoom, minZoom, maxZoom);
        applyToCameraView();
    }

    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }

    @Override
    public float getZoom() { return zoom; }

    // ------------------------------------------------------------------
    // Conversioni framebuffer (pixel) <-> mondo (metri)
    // ------------------------------------------------------------------

    @Override
    public float screenToWorldX(float framebufferX) {
        return cameraView.xmin + (framebufferX / viewportWidthPx) * cameraView.width;
    }

    @Override
    public float screenToWorldY(float framebufferY) {
        return cameraView.ymin + (framebufferY / viewportHeightPx) * cameraView.height;
    }

    // ------------------------------------------------------------------
    // Pan
    // ------------------------------------------------------------------

    @Override
    public void pan(float dxFb, float dyFb) {
        centerX -= dxFb * (cameraView.width / viewportWidthPx);
        centerY -= dyFb * (cameraView.height / viewportHeightPx);
        applyToCameraView();
    }

    // ------------------------------------------------------------------
    // Pinch zoom, ancorato al punto medio delle due dita
    // ------------------------------------------------------------------

    @Override
    public void beginPinch(float midFbX, float midFbY, float initialDistance) {
        pinching = true;
        pinchStartDistancePx = Math.max(initialDistance, 1f);
        zoomAtPinchStart = zoom;
        anchorWorldX = screenToWorldX(midFbX);
        anchorWorldY = screenToWorldY(midFbY);
    }

    @Override
    public void updatePinch(float midFbX, float midFbY, float currentDistance) {
        if (!pinching || currentDistance < 1f) return;

        zoom = clamp(zoomAtPinchStart * (currentDistance / pinchStartDistancePx), minZoom, maxZoom);

        float width = worldWidth / zoom;
        float height = worldHeight / zoom;

        float tx = midFbX / viewportWidthPx;
        float ty = midFbY / viewportHeightPx;

        centerX = anchorWorldX + width * (0.5f - tx);
        centerY = anchorWorldY + height * (0.5f - ty);

        applyWithSize(width, height);
    }

    @Override
    public void endPinch() {
        pinching = false;
    }

    // ------------------------------------------------------------------
    // Rendering
    // ------------------------------------------------------------------

    @Override
    public void applyTransform(Canvas canvas) {
        float scaleX = viewportWidthPx / cameraView.width;
        float scaleY = viewportHeightPx / cameraView.height;

        Matrix matrix = new Matrix();
        matrix.postTranslate(-cameraView.xmin, -cameraView.ymin);
        matrix.postScale(scaleX, scaleY);
        canvas.concat(matrix);
    }

    private void applyToCameraView() {
        applyWithSize(worldWidth / zoom, worldHeight / zoom);
    }

    private void applyWithSize(float width, float height) {
        clampCenter(width, height);

        cameraView.width = width;
        cameraView.height = height;
        cameraView.xmin = centerX - width / 2f;
        cameraView.xmax = centerX + width / 2f;
        cameraView.ymin = centerY - height / 2f;
        cameraView.ymax = centerY + height / 2f;
    }

    private void clampCenter(float viewWidth, float viewHeight) {
        float worldXmin = -worldWidth / 2f;
        float worldXmax = worldWidth / 2f;
        float worldYmin = -worldHeight / 2f;
        float worldYmax = worldHeight / 2f;

        if (viewWidth >= worldWidth) {
            centerX = 0f;
        } else {
            centerX = clamp(centerX, worldXmin + viewWidth / 2f, worldXmax - viewWidth / 2f);
        }

        if (viewHeight >= worldHeight) {
            centerY = 0f;
        } else {
            centerY = clamp(centerY, worldYmin + viewHeight / 2f, worldYmax - viewHeight / 2f);
        }
    }

    private static float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }
}