package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.badlogic.androidgames.framework.Input;

public class Button extends Widget {

    public interface OnClickListener {
        void onClick(Button button);
    }

    // public enum State { IDLE, PRESSED, PRESSED_OUTSIDE }
    public enum State { IDLE, PRESSED, DISABLED }

    private State state = State.IDLE;
    private int owningPointer = -1; // il pointer che "possiede" il button
    private OnClickListener listener;


    // ***** DRAW *****
    private String label;
    private final Paint paintUp;
    private final Paint paintPressed;
    private final Paint paintDisabled;
    private final Paint textPaint;

    // ***** DRAW *****
    private Bitmap bitmap;
    private final RectF dst = new RectF();


    /*
     * Costruttore.
     */
    public Button(float x, float y, float width, float height)
    {
        this(x, y, width, height, "");
    }

    public Button(float x, float y, float width, float height, String label) {
        super(x, y, width, height);
        this.label = label;

        paintUp = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintUp.setColor(0xFF3E7BFA);

        paintPressed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintPressed.setColor(0xFF2856C4);

        paintDisabled = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintDisabled.setColor(0xFF9E9E9E);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(height * 0.4f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }



    // ********************************
    //          Getter / Setter
    // ********************************

    public State getState() { return state; }

    public void setLabel(String label) { this.label = label; }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setOnClickListener(OnClickListener listener) { this.listener = listener; }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        if (!enabled) {
            owningPointer = -1;
            state = State.IDLE;
        }
    }


    // ***************************************
    //            Ciclo di vita
    // ***************************************

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        Paint background = !enabled
                ? paintDisabled
                : (state == State.PRESSED ? paintPressed : paintUp);

        canvas.drawRoundRect(x, y, x + width, y + height, 12f, 12f, background);

        if (label != null) {
            float cx = x + width / 2f;
            float cy = y + height / 2f - (textPaint.ascent() + textPaint.descent()) / 2f;
            canvas.drawText(label, cx, cy, textPaint);
        }
    }


    // ***************************************
    //         Gestione input grezzi
    // ***************************************

    @Override
    public boolean touchDown(float px, float py, int pointer) {
        if (!enabled || owningPointer != -1) {
            return false;
        }
        owningPointer = pointer;
        state = State.PRESSED;
        return true;
    }

    @Override
    public void touchDragged(float px, float py, int pointer) {
        if (pointer != owningPointer) return;
        // Se il dito esce dall'area il bottone si "spegne" visivamente ma
        // resta posseduto: se il dito rientra prima del touchUp torna PRESSED,
        // esattamente come il comportamento standard dei bottoni Android.
        state = contains(px, py) ? State.PRESSED : State.IDLE;
    }

    @Override
    public void touchUp(float px, float py, int pointer) {
        if (pointer != owningPointer) return;
        boolean wasInsideOnRelease = contains(px, py);
        owningPointer = -1;
        state = State.IDLE;
        if (wasInsideOnRelease && listener != null) {
            listener.onClick(this);
        }
    }

}


/*
    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!visible) return;

        canvas.drawBitmap(bitmap, null, dst, paint);
        // canvas.drawRect(x, y, x+width, y+height, paint);
    }
*/
