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
    private OnClickListener listener;

    public enum State { IDLE, PRESSED }
    private State state = State.IDLE;

    private int owningPointer = -1;

    // RENDER
    private String text;
    private final Paint paintUp;
    private final Paint paintPressed;
    private final Paint paintDisabled;
    private final Paint textPaint;

    /*
     * Costruttore.
     */
    public Button(float x, float y, float width, float height)
    {
        super(x, y, width, height);

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

    /*
     * Costruttore.
     */
    public Button(float x, float y, float width, float height, String text) {
        this(x, y, width, height);
        this.text = text;
    }

    // ***************************************
    //  Rendering
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        validateTransform(); // <- richiamalo altrove!

        Paint background = touchable != Touchable.ENABLED
                ? paintDisabled
                : (state == State.PRESSED ? paintPressed : paintUp);

        canvas.drawRoundRect(absX, absY, absX + width, absY + height,
                12f, 12f, background);

        if (text != null) {
            float cx = absX + width / 2f;
            float cy = absY + height / 2f - (textPaint.ascent() + textPaint.descent()) / 2f;
            canvas.drawText(text, cx, cy, textPaint);
        }
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

    public void setText(String text) { this.text = text; }

    public void setTextSize(float size) {
        textPaint.setTextSize(height * size);
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    public void setColor(int color) {
        // TODO: gestire i 3 diversi colori per stato
        paintUp.setColor(color);
    }

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
