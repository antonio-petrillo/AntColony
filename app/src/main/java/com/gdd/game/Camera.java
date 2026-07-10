package com.gdd.game;

public class Camera {

    private final Box cameraView; // camera in World (in metri)
    private final float worldWidth, worldHeight; // dimensione di World (in metri)
    private final int fbufferWidth, fbufferHeight; // dimensione framebuffer (in pixel)

    private float centerX = 0f, centerY = 0f;
    private float minZoom = 1f; // zoom = 1 (100%) significa "vedi tutto il World"
    private float maxZoom = 3f; // zoom > 1 significa "più vicino" (ingrandisci)
    private float zoom = 2f;

    // stato del pinch in corso, in metri/pixel framebuffer
    private boolean pinching = false;
    private float pinchStartDistancePx;
    private float zoomAtPinchStart;
    private float anchorWorldX, anchorWorldY;


    /*
     * Constructor.
     */
    public Camera(Box cameraView, float worldWidth, float worldHeight,
                  int viewportWidthPx, int viewportHeightPx) {
        this.cameraView = cameraView;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.fbufferWidth = Math.max(viewportWidthPx, 1);
        this.fbufferHeight = Math.max(viewportHeightPx, 1);
        update();
    }

    // ------------------------------------------------------------------
    // Getter / Setter
    // ------------------------------------------------------------------

    public float getCenterX() { return centerX; }
    public float getCenterY() { return centerY; }

    public float getZoom() { return zoom; }

    public void setZoom(float zoom) {
        if(zoom < minZoom || zoom > maxZoom)
            return;

        this.zoom = zoom;
        update();
    }

    public void setZoomLimits(float minZoom, float maxZoom) {
        if(minZoom < 1)
            minZoom = 1f;
        if(maxZoom < 1)
            maxZoom = 1f;
        this.minZoom = minZoom;
        this.maxZoom = maxZoom;
        zoom = clamp(zoom, minZoom, maxZoom);
        update();
    }

    /*
     * Riposiziona la camera manualmente.
     */
    public void setCamera(float centerX, float centerY, float zoom) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.zoom = clamp(zoom, minZoom, maxZoom);
        update();
    }

    // ------------------------------------------------------------------
    // Conversioni framebuffer (pixel) <-> world (metri)
    // ------------------------------------------------------------------

    public float fbufferToWorldX(float fbufferX) {
        return cameraView.xmin + (fbufferX / fbufferWidth) * cameraView.width;
    }

    public float fbufferToWorldY(float fbufferY) {
        return cameraView.ymin + (fbufferY / fbufferHeight) * cameraView.height;
    }

    // ------------------------------------------------------------------
    // Pan
    // ------------------------------------------------------------------

    /*
     * Sposta la camera di quantità delta(x,y) in pixel.
     */
    public void pan(float deltaX, float deltaY) {
        centerX -= deltaX * (cameraView.width / fbufferWidth);
        centerY -= deltaY * (cameraView.height / fbufferHeight);
        update();
    }

    // ------------------------------------------------------------------
    // Pinch zoom, ancorato al punto medio delle due dita
    // ------------------------------------------------------------------

    public void beginPinch(float midFbX, float midFbY, float initialDistance) {
        pinching = true;
        pinchStartDistancePx = Math.max(initialDistance, 1f);
        zoomAtPinchStart = zoom;
        anchorWorldX = fbufferToWorldX(midFbX);
        anchorWorldY = fbufferToWorldY(midFbY);
    }

    public void updatePinch(float midFbX, float midFbY, float currentDistance) {
        if (!pinching || currentDistance < 1f) return;

        zoom = clamp(zoomAtPinchStart * (currentDistance / pinchStartDistancePx), minZoom, maxZoom);

        float width = worldWidth / zoom;
        float height = worldHeight / zoom;

        float tx = midFbX / fbufferWidth; // 0..1 da sinistra a destra
        float ty = midFbY / fbufferHeight; // 0..1 dall'alto in basso

        //  Risolve il nuovo centro imponendo che il punto mondo agganciato
        //  all'inizio del pinch resti sotto il punto medio corrente delle dita
        centerX = anchorWorldX + width * (0.5f - tx);
        centerY = anchorWorldY + height * (0.5f - ty);

        applyWithSize(width, height);
    }

    public void endPinch() {
        pinching = false;
    }

    // ------------------------------------------------------------------
    // Metodi per aggiornare il Box cameraView a partire da centro/zoom correnti
    // ------------------------------------------------------------------

    /*
     * Applica le nuove impostazioni alla camera.
     * Richiamato in automatico.
     */
    private void update() {
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

    /*
     * Assicura che il centro della camera non faccia uscire cameraView
     * dai bordi del World.
     */
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