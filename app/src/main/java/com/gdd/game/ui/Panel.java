package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Panel extends WidgetGroup {

    private boolean border = false;
    private final Paint paint;
    private int owningPointer = -1;


    public Panel(float x, float y, float width, float height) {
        super(x, y, width, height);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFF3E7BFA);
    }

    // ***************************************
    //  Render
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        if(border)
            canvas.drawRoundRect(absX, absY, absX + width, absY + height,
                    12f, 12f, paint);

        super.draw(canvas);
    }

    // TEST
    public void setBorder(boolean border, int color) {
        this.border = border;
        paint.setColor(color);
    }

    // ***************************************
    //  Input
    // ***************************************

    // TEST
    @Override
    public boolean touchDown(float x, float y, int pointer) {
        if (owningPointer != -1) {
            return false;
        }

        owningPointer = pointer;
        return true;
    }

    // TEST
    @Override
    public void touchUp(float x, float y, int pointer) {
        if (pointer != owningPointer) return;
        owningPointer = -1;
    }

    // TEST
    @Override
    public void touchCancelled(int pointer) {
        if (pointer == owningPointer) {
            owningPointer = -1;
        }
    }
}
