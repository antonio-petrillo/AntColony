package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Label extends Widget {

    protected String text;
    private final Paint textPaint, backgroundPaint;
    protected RectF dst = new RectF();


    public Label(float x, float y, float width, float height)
    {
        super(x, y, width, height);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(0xFF3E7BFA);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextSize(height * 0.4f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public Label(float x, float y, float width, float height, String text)
    {
        this(x, y, width, height);
        this.text = text;
    }

    // ***************************************
    //  Rendering
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        //canvas.drawRoundRect(x, y, x + width, y + height, 12f, 12f, backgroundPaint);

        if (text != null) {
            float cx = x + width / 2f;
            float cy = y + height / 2f - (textPaint.ascent() + textPaint.descent()) / 2f;
            canvas.drawText(text, cx, cy, textPaint);
        }
    }

    // ********************************
    //  Getter / Setter
    // ********************************

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setTextSize(float size) { textPaint.setTextSize(height * size); }

    public void setTextColor(int color) {
        textPaint.setColor(color);
    }

    public void setBackgroundColor(int color) {
        backgroundPaint.setColor(color);
    }
}