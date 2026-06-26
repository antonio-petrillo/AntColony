package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.Assets;

public class UIButton extends UIElement {

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
}
