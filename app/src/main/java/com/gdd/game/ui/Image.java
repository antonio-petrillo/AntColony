package com.gdd.game.ui;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Image extends Widget {

    protected Bitmap bitmap;
    protected Paint paint;
    protected RectF dst = new RectF();

    /*
     * Constructor.
     */
    public Image(float x, float y, float width, float height)
    {
        super(x, y, width, height);
        this.paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    /*
     * Constructor.
     */
    public Image(float x, float y, float width, float height, Bitmap bitmap)
    {
        this(x, y, width, height);
        this.bitmap = bitmap;
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
