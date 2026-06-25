package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public class UIButton extends UIElement {

    public UIButton(float x, float y, float width, float height)
    {
        super(x, y, width, height);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!visible) return;

        canvas.drawRect(x, y, x+width, y+height, paint);
    }
}
