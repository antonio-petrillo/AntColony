package com.gdd.game.ecs.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.ecs.misc.ScreenParams;

public class BoxRenderComp extends RenderComponent {

    private final Paint paint;

    public BoxRenderComp(int color, boolean filled) {
        paint = new Paint();
        paint.setColor(color);
        paint.setStyle(filled ? Paint.Style.FILL : Paint.Style.STROKE);
        paint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas, ScreenParams t, RectF dst) {

        canvas.save();
        canvas.translate(t.screenX, t.screenY);
        dst.set(-t.halfWidthPx, -t.halfHeightPx, t.halfWidthPx, t.halfHeightPx);
        canvas.rotate(t.rotation);
        canvas.drawRect(dst, paint);
        canvas.restore();
    }

}
