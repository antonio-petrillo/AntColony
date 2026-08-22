package com.gdd.game.ecs.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.ecs.misc.ScreenParams;

public class BitmapRenderComp extends RenderComponent {

    private final Bitmap bitmap;
    private final Paint paint;
    public float visualAngleOffsetDeg = 0f;

    public BitmapRenderComp(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    public void draw(Canvas canvas, ScreenParams t, RectF dst) {

        if(bitmap == null) return;

        dst.set(-t.halfWidthPx, -t.halfHeightPx, t.halfWidthPx, t.halfHeightPx);

        canvas.save();
        canvas.translate(t.screenX, t.screenY);
        canvas.rotate(t.rotation + visualAngleOffsetDeg);
        canvas.drawBitmap(bitmap, null, dst, paint);
        canvas.restore();
    }
}
