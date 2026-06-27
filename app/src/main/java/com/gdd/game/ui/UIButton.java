package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.gdd.game.Assets;

public class UIButton extends UIElement {

    // ***** 'STATE MACHINE' PER POINTER *****
    private enum State { IDLE, PRESSED, PRESSED_OUTSIDE }
    private State state = State.IDLE;
    private int trackedPointerId = -1; // il pointer che "possiede" il button

    private OnClickListener clickListener;

    // ***** DRAW *****
    private Bitmap bitmap;
    private final RectF dst = new RectF();


    public UIButton(float x, float y, float width, float height)
    {
        super(x, y, width, height);
        dst.set(x, y, x+width, y+height);
    }



    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!visible) return;

        canvas.drawBitmap(bitmap, null, dst, paint);
        // canvas.drawRect(x, y, x+width, y+height, paint);
    }


    public void setOnClickListener(OnClickListener listener) {
        this.clickListener = listener;
    }

    private void fireClick() {
        if (clickListener != null) {
            clickListener.onClick(this);
        }
    }

    public boolean handleInput(Input.TouchEvent event) {

        switch (state) {
            case IDLE:
                if (event.type == Input.TouchEvent.TOUCH_DOWN && contains(event.x, event.y)) {
                    state = State.PRESSED;
                    trackedPointerId = event.pointer;
                    return true; // evento consumato
                }
                break;

            case PRESSED:
                // ignora eventi di altri pointer
                if (event.pointer != trackedPointerId) return false;

                if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                    if (!contains(event.x, event.y)) {
                        state = State.PRESSED_OUTSIDE; // uscito col dito
                    }
                    return true;
                }
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    state = State.IDLE;
                    trackedPointerId = -1;
                    if (contains(event.x, event.y)) {
                        fireClick(); // click valido
                    }
                    return true;
                }
                break;

            case PRESSED_OUTSIDE:
                if (event.pointer != trackedPointerId) return false;

                if (event.type == Input.TouchEvent.TOUCH_DRAGGED) {
                    if (contains(event.x, event.y)) {
                        state = State.PRESSED;  // rientrato
                    }
                    return true;
                }
                if (event.type == Input.TouchEvent.TOUCH_UP) {
                    state = State.IDLE; // nessun click, era fuori
                    trackedPointerId = -1;
                    return true; // ma l'evento è comunque "nostro"
                }
                break;
        }

        return false;
    }

}
