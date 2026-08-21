package com.gdd.game.ecs.misc;

public class PointerTracker {

    public static final int NO_POINTER = -1;
    public int pointer1 = NO_POINTER, pointer2 = NO_POINTER;

    // coordinate dei puntatori in pixel
    public float p1x, p1y, p2x, p2y;
    public float p1StartX, p1StartY, p2StartX, p2StartY;


    public void addPointer(int id, float x, float y) {
        if (pointer1 == NO_POINTER) {
            pointer1 = id;
            p1x = p1StartX = x;
            p1y = p1StartY = y;
        } else if (pointer2 == NO_POINTER) {
            pointer2 = id;
            p2x = p2StartX = x;
            p2y = p2StartY = y;
        }
    }

    public boolean updatePointer(int id, float x, float y) {
        if (id == pointer1) {
            p1x = x; p1y = y;
            return true;
        }
        else if (id == pointer2) {
            p2x = x; p2y = y;
            return true;
        }
        return false;
    }

    public void removePointer(int id) {
        if (id == pointer1) {
            if (pointer2 != NO_POINTER) {
                pointer1 = pointer2;
                p1x = p2x;
                p1y = p2y;
                p1StartX = p2StartX;
                p1StartY = p2StartY;

                pointer2 = NO_POINTER;
            } else {
                pointer1 = NO_POINTER;
            }
        } else if (id == pointer2) {
            pointer2 = NO_POINTER;
        }
    }

    public void removePointers() {
        pointer1 = NO_POINTER;
        pointer2 = NO_POINTER;
    }

    // ********************************
    //  Misc
    // ********************************

    public boolean hasPointer(int id) {
        return (id == pointer1 || id == pointer2);
    }

    public boolean hasFirstPointer() {
        return pointer1 != NO_POINTER;
    }

    public boolean hasSecondPointer() {
        return pointer2 != NO_POINTER;
    }

    // ********************************
    //  Panning utils
    // ********************************

    public float deltaX(int id, float x) {
        if (id == pointer1) return x - p1x;
        if (id == pointer2) return x - p2x;
        return 0f;
    }

    public float deltaY(int id, float y) {
        if (id == pointer1) return y - p1y;
        if (id == pointer2) return y - p2y;
        return 0f;
    }

    public float totalDeltaX(int id, float x) {
        if (id == pointer1) return x - p1StartX;
        if (id == pointer2) return x - p2StartX;
        return 0f;
    }

    public float totalDeltaY(int id, float y) {
        if (id == pointer1) return y - p1StartY;
        if (id == pointer2) return y - p2StartY;
        return 0f;
    }

    // ********************************
    //  Pinch utils
    // ********************************

    // distanza fra i due pointer attivi
    public float pinchDistance() {
        float dx = p2x - p1x;
        float dy = p2y - p1y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // punto medio X tra i due pointer attivi
    public float pinchMidX() { return (p1x + p2x) / 2f; }

    // punto medio Y tra i due pointer attivi
    public float pinchMidY() { return (p1y + p2y) / 2f; }
}
