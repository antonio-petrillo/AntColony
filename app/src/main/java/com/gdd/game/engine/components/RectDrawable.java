package com.gdd.game.engine.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.ScreenTransform;

public class RectDrawable extends DrawableComponent {

    @Override
    public void draw(Canvas canvas, Paint paint, ScreenTransform t, RectF dst) {

        if(canvas == null || paint == null || t == null || dst == null)
            return;

        dst.set(-t.halfWidthPx, -t.halfHeightPx, t.halfWidthPx, t.halfHeightPx);

        canvas.save();
        canvas.translate(t.screenX, t.screenY);
        canvas.rotate(t.rotation);
        // canvas.rotate((float) Math.toDegrees(t.rotation) + 90.0f);
        // canvas.drawBitmap(Assets.ANT_BITMAP, null, dst, rc.paint);
        canvas.drawRect(dst, paint);
        canvas.restore();
    }
}
