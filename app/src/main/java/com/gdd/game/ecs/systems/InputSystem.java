package com.gdd.game.ecs.systems;

import com.badlogic.androidgames.framework.Input;
import com.gdd.game.Camera;

public class InputSystem {

    public enum GestureState { IDLE, PENDING, PANNING, PINCH_ZOOM }
    private GestureState state = GestureState.IDLE;
    private final Camera camera;

    private static final int NO_POINTER = -1;
    private static final float PAN_THRESHOLD = 20f;

    private int pointer1 = NO_POINTER, pointer2 = NO_POINTER;
    private float p1x, p1y, p2x, p2y;
    private float p1StartX, p1StartY;


    /*
     * Constructor.
     */
    public InputSystem(Camera camera) {
        this.camera = camera;
    }

    // ------------------------------------------------------------------
    // Getter / Setter
    // ------------------------------------------------------------------

    public GestureState getState() {
        return state;
    }

    public Camera getCamera() {
        return camera;
    }

    // ------------------------------------------------------------------
    // Reset esplicito
    // ------------------------------------------------------------------

    public void reset() {
        if (state == GestureState.PINCH_ZOOM) {
            camera.endPinch();
        }
        pointer1 = NO_POINTER;
        pointer2 = NO_POINTER;
        state = GestureState.IDLE;
    }

    // ------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------

    public void processInput(Input.TouchEvent event) {
        if(event == null) return;

        switch (event.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                handleDown(event);
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                handleDragged(event);
                break;
            case Input.TouchEvent.TOUCH_UP:
                handleUp(event);
                break;
        }
    }

    private void handleDown(Input.TouchEvent e) {

        if (pointer1 == NO_POINTER) {
            pointer1 = e.pointer;
            p1x = p1StartX = e.x;
            p1y = p1StartY = e.y;
            state = GestureState.PENDING;
            return;
        }

        if (pointer2 == NO_POINTER) {
            pointer2 = e.pointer;
            p2x = e.x;
            p2y = e.y;
            state = GestureState.PINCH_ZOOM;
            float midX = (p1x + p2x) / 2f;
            float midY = (p1y + p2y) / 2f;
            camera.beginPinch(midX, midY, distance(p1x, p1y, p2x, p2y));
        }
    }

    private void handleDragged(Input.TouchEvent event) {

        if (event.pointer == pointer1) {
            float dx = event.x - p1x;
            float dy = event.y - p1y;
            p1x = event.x;
            p1y = event.y;

            if (state == GestureState.PENDING) {
                float totalDx = p1x - p1StartX;
                float totalDy = p1y - p1StartY;
                if (totalDx * totalDx + totalDy * totalDy > PAN_THRESHOLD * PAN_THRESHOLD) {
                    state = GestureState.PANNING;
                }
            } else if (state == GestureState.PANNING) {
                camera.pan(dx, dy);
            } else if (state == GestureState.PINCH_ZOOM) {
                camera.updatePinch((p1x + p2x) / 2f, (p1y + p2y) / 2f, distance(p1x, p1y, p2x, p2y));
            }
            return;
        }

        if (event.pointer == pointer2) {
            p2x = event.x;
            p2y = event.y;
            if (state == GestureState.PINCH_ZOOM) {
                camera.updatePinch((p1x + p2x) / 2f, (p1y + p2y) / 2f, distance(p1x, p1y, p2x, p2y));
            }
        }
    }

    private void handleUp(Input.TouchEvent event) {

        if (event.pointer == pointer1) {
            if (pointer2 != NO_POINTER) {
                if (state == GestureState.PINCH_ZOOM) {
                    camera.endPinch();
                }
                pointer1 = pointer2;
                p1x = p2x;
                p1y = p2y;
                pointer2 = NO_POINTER;
                state = GestureState.PANNING;
            } else {
                pointer1 = NO_POINTER;
                state = GestureState.IDLE;
            }
            return;
        }

        if (event.pointer == pointer2) {
            pointer2 = NO_POINTER;
            if (state == GestureState.PINCH_ZOOM) {
                camera.endPinch();
                state = GestureState.PANNING;
            }
        }
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
