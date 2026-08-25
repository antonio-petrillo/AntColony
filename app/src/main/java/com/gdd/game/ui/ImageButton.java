package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class ImageButton extends Button {

    protected Bitmap idleBitmap, pressedBitmap;
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

        dst.set(x, y, x+width, y+height);

        if(state == State.IDLE && idleBitmap != null)
            canvas.drawBitmap(idleBitmap, null, dst, paint);
        else if(state == State.PRESSED && pressedBitmap != null)
            canvas.drawBitmap(pressedBitmap, null, dst, paint);
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
}
