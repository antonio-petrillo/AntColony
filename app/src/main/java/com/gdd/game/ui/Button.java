package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.badlogic.androidgames.framework.Input;

public abstract class Button extends Widget {

    public interface OnClickListener {
        void onClick(Button button);
    }
    protected OnClickListener listener;

    public enum State { IDLE, PRESSED }
    protected State state = State.IDLE;

    protected int owningPointer = -1;


    public Button(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    // ***************************************
    //  Input
    // ***************************************

    @Override
    public boolean touchDown(float x, float y, int pointer) {
        if (touchable != Touchable.ENABLED || owningPointer != -1) {
            return false;
        }
        owningPointer = pointer;
        state = State.PRESSED;
        return true;
    }

    @Override
    public void touchDragged(float x, float y, int pointer) {
        if (pointer != owningPointer) return;
        state = contains(x, y) ? State.PRESSED : State.IDLE;
    }

    @Override
    public void touchUp(float x, float y, int pointer) {
        if (pointer != owningPointer) return;
        boolean wasInsideOnRelease = contains(x, y);
        owningPointer = -1;
        state = State.IDLE;

        if (wasInsideOnRelease && listener != null) {
            listener.onClick(this);
        }
    }

    @Override
    public void touchCancelled(int pointer) {
        if (pointer == owningPointer) {
            owningPointer = -1;
            state = State.IDLE;
        }
    }

    // ********************************
    //  Getter / Setter
    // ********************************

    public State getState() { return state; }

    public void setOnClickListener(OnClickListener listener) { this.listener = listener; }

    @Override
    public void setTouchable(Touchable touchable) {
        super.setTouchable(touchable);
        // * UIController andrebbe notificato *
        if (touchable != Touchable.ENABLED) {
            owningPointer = -1;
            state = State.IDLE;
        }
    }

    // ********************************
    //  Misc
    // ********************************

    // da fixare,per i problemi di "setTouchable"

    public void enable() {
        touchable = Touchable.ENABLED;
    }

    public void disable() {
        touchable = Touchable.DISABLED;
    }
}
