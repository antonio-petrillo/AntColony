package com.gdd.game.cards;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class CurvedTargetArrow extends TargetArrow {

    public CurvedTargetArrow(int color, float thickness) {
        super(color, thickness);
    }

    // ***************************************
    //  Rendering
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        if (!visible) return;

        // curva di Bézier semplice: controllo a metà strada, alzato verso l'alto
        float ctrlX = (startX + tipX) / 2f;
        float ctrlY = Math.min(startY, tipY) - 100f;

        // ***** CURVED LINE *****
        Path path = new Path();
        path.moveTo(startX, startY);
        path.quadTo(ctrlX, ctrlY, tipX, tipY);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, paint);

        // ***** ARROW HEAD *****
        float angle = (float) Math.atan2(tipY - startY, tipX - startX);
        float size = 20f;
        Path head = new Path();
        head.moveTo(tipX, tipY);
        head.lineTo(
                tipX - size * (float) Math.cos(angle - Math.PI / 6),
                tipY - size * (float) Math.sin(angle - Math.PI / 6));
        head.lineTo(
                tipX - size * (float) Math.cos(angle + Math.PI / 6),
                tipY - size * (float) Math.sin(angle + Math.PI / 6));
        head.close();
        canvas.drawPath(head, paint);
    }
}
