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

    public enum VisualState { IDLE, PRESSED, DISABLED } // usato solo per il rendering
    public enum State { IDLE, PRESSED } // usato per l'interazione con l'utente
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

    public void setOnClickListener(OnClickListener listener) { this.listener = listener; }

    public State getState() { return state; }

    /*
     * Usato per decidere l'aspetto di rendering.
     */
    public VisualState getVisualState() {
        if (touchable != Touchable.ENABLED) return VisualState.DISABLED;
        return state == State.PRESSED ? VisualState.PRESSED : VisualState.IDLE;
    }

    @Override
    public void setTouchable(Touchable touchable) {
        if (touchable == Touchable.CHILDREN_ONLY) touchable = Touchable.DISABLED;
        super.setTouchable(touchable);
        if (touchable != Touchable.ENABLED) {
            owningPointer = -1;
            state = State.IDLE;
        }
    }

    // ********************************
    //  Misc
    // ********************************

    public void enable()  { setTouchable(Touchable.ENABLED); }
    public void disable() { setTouchable(Touchable.DISABLED); }
}
