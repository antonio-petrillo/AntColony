package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class ImageButton extends Button {

    protected Bitmap idleBitmap, pressedBitmap, disableBitmap;
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

        dst.set(absX, absY, absX+width, absY+height);

        Bitmap bmp;
        switch (getVisualState()) {
            case PRESSED:  bmp = pressedBitmap;  break;
            case DISABLED: bmp = disableBitmap;  break;
            default:       bmp = idleBitmap;
        }
        if (bmp != null) canvas.drawBitmap(bmp, null, dst, paint);
    }

    // ********************************
    //  Getter / Setter
    // ********************************

    public void setIdleBitmap(Bitmap bitmap) {
        this.idleBitmap = bitmap;
    }

    public void setPressedBitmap(Bitmap bitmap) {
        this.pressedBitmap = bitmap;
    }

    public void setDisabledBitmap(Bitmap bitmap) {
        this.disableBitmap = bitmap;
    }
}
