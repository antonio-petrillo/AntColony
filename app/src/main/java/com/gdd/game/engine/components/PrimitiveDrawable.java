package com.gdd.game.engine.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.ScreenParams;

public class PrimitiveDrawable extends DrawableComponent {

    public enum Kind { CIRCLE, BOX }
    private final Kind kind;
    private final Paint paint;

    public PrimitiveDrawable(Kind kind, int color, boolean filled) {
        this.kind = kind;
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(filled ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas, ScreenParams t, RectF dst) {

        canvas.save();
        canvas.translate(t.screenX, t.screenY);

        switch (kind) {
            case CIRCLE -> {
                canvas.drawCircle(0, 0, t.radius, paint);
            }
            case BOX -> {
                dst.set(-t.halfWidthPx, -t.halfHeightPx, t.halfWidthPx, t.halfHeightPx);
                canvas.rotate(t.rotation);
                canvas.drawRect(dst, paint);
            }
        }

        canvas.restore();
    }

}