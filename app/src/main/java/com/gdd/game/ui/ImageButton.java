package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class ImageButton extends Button {

    protected Bitmap bitmap;
    protected Paint paint;
    protected RectF dst = new RectF();


    public ImageButton(float x, float y, float width, float height) {
        super(x, y, width, height);
        this.paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    // ***************************************
    //  Rendering
    // ***************************************

    @Override
    public void draw(Canvas canvas) {

        if(bitmap == null) return;

        dst.set(x, y, x+width, y+height);
        canvas.drawBitmap(bitmap, null, dst, paint);
    }

    // ********************************
    //  Getter / Setter
    // ********************************

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }
}
