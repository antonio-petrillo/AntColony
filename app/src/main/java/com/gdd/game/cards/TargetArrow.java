package com.gdd.game.cards;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class TargetArrow {

    protected boolean visible = false;
    protected float startX, startY;
    protected float tipX, tipY;

    // Rendering
    protected final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /*
     * Valori di esempio:
     *  - color = 0xFFFFD54F (giallo)
     *  - thickness = 5f
     */
    public TargetArrow(int color, float thickness) {
        paint.setColor(color);
        paint.setStrokeWidth(thickness);
    }

    // ***************************************
    //  Rendering
    // ***************************************

    public void draw(Canvas canvas) {
        if (!visible) return;

        canvas.drawLine(startX, startY, tipX, tipY, paint);

        float angle = (float) Math.atan2(tipY - startY, tipX - startX);
        float size = 22f;
        Path head = new Path();
        head.moveTo(tipX, tipY);
        head.lineTo(tipX - size * (float) Math.cos(angle - Math.PI / 6),
                tipY - size * (float) Math.sin(angle - Math.PI / 6));
        head.lineTo(tipX - size * (float) Math.cos(angle + Math.PI / 6),
                tipY - size * (float) Math.sin(angle + Math.PI / 6));
        head.close();
        canvas.drawPath(head, paint);
    }

    // ***************************************
    //  Misc
    // ***************************************

    public void startAt(float startX, float startY) {
        this.startX = startX;
        this.startY = startY;
        this.tipX = startX;
        this.tipY = startY;
    }

    public void updateTip(float x, float y) { tipX = x; tipY = y; }

    public void show() { visible = true; }

    public void hide() { visible = false; }
}
