package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

public abstract class UIElement {

    // L'interfaccia (il "contratto")
    public interface OnClickListener {
        void onClick(UIButton button);
    }

    protected float xmin, ymin, xmax, ymax;
    protected float width, height;
    protected boolean visible = true;

    public UIElement(float x, float y, float width, float height) {
        this.xmin = x;
        this.ymin = y;
        this.width = width;
        this.height = height;

        xmax = xmin + width;
        ymax = ymin + height;
    }


    public boolean contains(float px, float py) {

        if(px >= xmin && px <= xmax &&
                py >= ymin && py <= ymax)
            return true;

        return false;
    }


    public abstract void draw(Canvas canvas, Paint paint);

    public abstract boolean handleInput(Input.TouchEvent event);
}
