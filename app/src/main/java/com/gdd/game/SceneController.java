package com.gdd.game;

import com.badlogic.androidgames.framework.Input;
import android.util.Log;
import java.util.List;

public class SceneController {

    public enum GestureState { IDLE, PENDING, PANNING, PINCH_ZOOM, OBJECT_DRAG }

    public interface SceneInteractable {
        boolean isDraggable();

        void onTap();
        void onDragStart(float worldX, float worldY);
        void onDrag(float worldX, float worldY);
        void onDragEnd(float worldX, float worldY);

        void onDragCancel();
    }

    public interface SceneInteractableLocator {
        SceneInteractable hit(float worldX, float worldY);
    }


    private GestureState state = GestureState.IDLE;
    private final CameraController camera;

    private static final int NO_POINTER = -1;
    private static final float PAN_THRESHOLD = 20f;

    private int pointer1 = NO_POINTER, pointer2 = NO_POINTER;
    private float p1x, p1y, p2x, p2y;
    private float p1StartX, p1StartY;

    private SceneInteractableLocator locator;
    private SceneInteractable tappableCandidate;
    private SceneInteractable draggedTarget;


    public SceneController(CameraController camera) {
        this.camera = camera;
    }

    // ------------------------------------------------------------------
    // Getter / Setter
    // ------------------------------------------------------------------

    public GestureState getState() {
        return state;
    }

    public CameraController getCamera() {
        return camera;
    }

    public void setInteractableLocator(SceneInteractableLocator locator) {
        this.locator = locator;
    }


    // ------------------------------------------------------------------
    // Reset esplicito (es. chiamato dal bottone pausa)
    // ------------------------------------------------------------------

    public void reset() {
        if (draggedTarget != null) {
            draggedTarget.onDragCancel();
        }
        if (state == GestureState.PINCH_ZOOM) {
            camera.endPinch();
        }
        pointer1 = NO_POINTER;
        pointer2 = NO_POINTER;
        tappableCandidate = null;
        draggedTarget = null;
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
        if (state == GestureState.OBJECT_DRAG) {
            return;
        }

        if (pointer1 == NO_POINTER) {
            /*
            float worldX = camera.screenToWorldX(e.x);
            float worldY = camera.screenToWorldY(e.y);
            SceneInteractable hit = locator != null ? locator.hit(worldX, worldY) : null;
            */

            pointer1 = e.pointer;
            p1x = p1StartX = e.x;
            p1y = p1StartY = e.y;

            /*
            if (hit != null && hit.isDraggable()) {
                state = GestureState.OBJECT_DRAG;
                draggedTarget = hit;
                draggedTarget.onDragStart(worldX, worldY);
            } else {
                // Zona vuota, oppure oggetto solo "tappabile": aspettiamo
                // di vedere se il gesto diventa un pan.
                state = GestureState.PENDING;
                tappableCandidate = hit; // null se zona vuota
            }
            */
            state = GestureState.PENDING; // DA RIMUOVERE
            return;
        }

        if (pointer2 == NO_POINTER) {
            pointer2 = e.pointer;
            p2x = e.x;
            p2y = e.y;
            tappableCandidate = null;

            state = GestureState.PINCH_ZOOM;
            float midX = (p1x + p2x) / 2f;
            float midY = (p1y + p2y) / 2f;
            camera.beginPinch(midX, midY, distance(p1x, p1y, p2x, p2y));
        }

        // Terzo dito o oltre: ignorato completamente.
    }

    private void handleDragged(Input.TouchEvent event) {
        /*
        if (state == GestureState.OBJECT_DRAG) {
            if (event.pointer == pointer1) {
                p1x = event.x;
                p1y = event.y;
                draggedTarget.onDrag(camera.screenToWorldX(event.x), camera.screenToWorldY(event.y));
            }
            return;
        }
        */

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
                    tappableCandidate = null; // si è mosso troppo: non è più un tap
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
            return;
        }

        // Pointer non tracciato (es. dito rimasto giù dopo un reset()): ignorato.
    }

    private void handleUp(Input.TouchEvent event) {
        /*
        if (state == GestureState.OBJECT_DRAG) {
            if (event.pointer == pointer1) {
                draggedTarget.onDragEnd(camera.screenToWorldX(event.x), camera.screenToWorldY(event.y));
                draggedTarget = null;
                pointer1 = NO_POINTER;
                state = GestureState.IDLE;
            }
            return;
        }
        */

        if (event.pointer == pointer1) {
            if (state == GestureState.PENDING && tappableCandidate != null) {
                tappableCandidate.onTap();
            }
            tappableCandidate = null;

            if (pointer2 != NO_POINTER) {
                if (state == GestureState.PINCH_ZOOM) {
                    camera.endPinch();
                }
                // Il primo dito si solleva ma il secondo è ancora giù: quel
                // dito diventa naturalmente il nuovo dito di pan.
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
                // Resta un solo dito: si prosegue in pan senza ripartire da zero.
                state = GestureState.PANNING;
            }
            return;
        }

        // Pointer non tracciato: ignorato.
    }

    private static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
