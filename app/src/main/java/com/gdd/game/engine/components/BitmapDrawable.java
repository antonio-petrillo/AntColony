package com.gdd.game.engine.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.ScreenParams;

public class BitmapDrawable extends DrawableComponent {

    private final Bitmap bitmap;
    private final Paint paint; // con FILTER_BITMAP_FLAG

    public BitmapDrawable(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    public void draw(Canvas canvas, ScreenParams t, RectF dst) {

        if(bitmap == null) return;

        dst.set(-t.halfWidthPx, -t.halfHeightPx, t.halfWidthPx, t.halfHeightPx);

        canvas.save();
        canvas.translate(t.screenX, t.screenY);
        canvas.rotate(t.rotation);
        canvas.drawBitmap(bitmap, null, dst, paint);
        canvas.restore();
    }
}