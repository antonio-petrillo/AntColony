package com.gdd.game.ecs.systems;

import com.badlogic.androidgames.framework.Input;
import com.gdd.game.Camera;
import com.gdd.game.PointerTracker;

public class InputSystem {

    public enum GestureState { IDLE, PENDING, PANNING, PINCH_ZOOM }
    private GestureState state = GestureState.IDLE;
    private final Camera camera;

    private static final float PAN_THRESHOLD = 20f;
    private PointerTracker pointers = new PointerTracker();


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

    // ------------------------------------------------------------------
    // Reset esplicito
    // ------------------------------------------------------------------

    public void reset() {
        if (state == GestureState.PINCH_ZOOM) {
            camera.endPinch();
        }
        pointers.removePointers();
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

    private void handleDown(Input.TouchEvent event) {

        if(state == GestureState.IDLE) {
            pointers.addPointer(event.pointer, event.x, event.y);
            state = GestureState.PENDING;
        }
        else if(state == GestureState.PENDING) {
            pointers.addPointer(event.pointer, event.x, event.y);
            state = GestureState.PINCH_ZOOM;

            camera.beginPinch(pointers.pinchMidX(), pointers.pinchMidY(), pointers.pinchDistance());
        }
        else if(state == GestureState.PANNING) {
            pointers.addPointer(event.pointer, event.x, event.y);
            state = GestureState.PINCH_ZOOM;

            camera.beginPinch(pointers.pinchMidX(), pointers.pinchMidY(), pointers.pinchDistance());
        }
    }

    private void handleDragged(Input.TouchEvent event) {

        if(!pointers.hasPointer(event.pointer))
            return;

        if (state == GestureState.PENDING) {
            float totalDx = pointers.totalDeltaX(event.pointer, event.x);
            float totalDy = pointers.totalDeltaY(event.pointer, event.y);
            pointers.updatePointer(event.pointer, event.x, event.y);
            // passa al panning se superata una certa soglia con il dito
            if (totalDx * totalDx + totalDy * totalDy > PAN_THRESHOLD * PAN_THRESHOLD) {
                state = GestureState.PANNING;
            }
        } else if (state == GestureState.PANNING) {
            float dx = pointers.deltaX(event.pointer, event.x);
            float dy = pointers.deltaY(event.pointer, event.y);
            pointers.updatePointer(event.pointer, event.x, event.y);
            camera.pan(dx, dy);
        } else if (state == GestureState.PINCH_ZOOM) {
            pointers.updatePointer(event.pointer, event.x, event.y);
            camera.updatePinch(pointers.pinchMidX(), pointers.pinchMidY(), pointers.pinchDistance());
        }
    }

    private void handleUp(Input.TouchEvent event) {

        if(!pointers.hasPointer(event.pointer))
            return;

        pointers.removePointer(event.pointer);

        if(state == GestureState.PENDING) {
            state = GestureState.IDLE;
        } else if (state == GestureState.PANNING) {
            state = GestureState.IDLE;
        } else if (state == GestureState.PINCH_ZOOM) {
            camera.endPinch();
            state = GestureState.PANNING;
        }
    }
}
