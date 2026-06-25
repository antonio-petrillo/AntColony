package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class UIElement {

    protected float x, y, width, height;
    protected boolean visible = true;

    public UIElement(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public boolean contains(float px, float py) {
        return visible && px >= x && px <= x + width
                && py >= y && py <= y + height;
    }
}
