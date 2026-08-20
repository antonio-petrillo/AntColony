package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public class TextButton extends Button {

    private String text;
    private final Paint paintUp;
    private final Paint paintPressed;
    private final Paint paintDisabled;
    private final Paint textPaint;


    public TextButton(float x, float y, float width, float height) {
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

    // ***************************************
    //  Rendering
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
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

    // ********************************
    //  Getter / Setter
    // ********************************

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
}
