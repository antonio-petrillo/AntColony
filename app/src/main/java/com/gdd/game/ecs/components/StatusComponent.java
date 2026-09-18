package com.gdd.game.ecs.components;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.ecs.misc.ScreenParams;

public class StatusComponent extends Component {

    public enum Type { NONE, INJURED, HASTE, INCREASED_SIGHT }

    public Type type;
    public Bitmap bitmap;
    private final Paint paint;


    public StatusComponent() {
        type = Type.NONE;
        bitmap = null;
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);
    }

    @Override
    public ComponentType type() {
        return ComponentType.STATUS;
    }

    public void reset() {
        type = Type.NONE;
        bitmap = null;
    }

    /*
     * Disegna l'icona di stato in alto a destra rispetto l'entity.
     */
    public void draw(Canvas canvas, ScreenParams t, RectF dst) {

        if(type == Type.NONE || bitmap == null) return;

        dst.set(-t.halfWidthPx, -t.halfHeightPx, t.halfWidthPx, t.halfHeightPx);

        canvas.save();
        canvas.translate(t.screenX + t.halfHeightPx*2, t.screenY - t.halfHeightPx*2);
        canvas.drawBitmap(bitmap, null, dst, paint);
        canvas.restore();
    }
}