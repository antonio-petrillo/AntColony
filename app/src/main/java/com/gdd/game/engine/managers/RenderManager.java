package com.gdd.game.engine.managers;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.core.Actor;
import com.gdd.game.engine.Camera;
import com.gdd.game.engine.ScreenParams;
import com.gdd.game.engine.components.ComponentType;
import com.gdd.game.engine.core.Shape;
import com.gdd.game.engine.core.Transform;
import com.gdd.game.engine.components.DrawableComponent;

import java.util.List;

public class RenderManager {

    // allocate once and used for every draw
    // scratch riusati, MAI allocati nel loop
    private final ScreenParams scratchTransform = new ScreenParams();
    private final RectF scratchDst = new RectF();


    public RenderManager() {
    }


    public void render(Canvas canvas, Camera camera, List<Actor> actors) {

        if(canvas == null || actors == null)
            return;

        for(Actor a : actors) {
            DrawableComponent dc = (DrawableComponent) a.getComponent(ComponentType.DRAWABLE);
            if(dc == null)
                continue;

            Transform transform = a.getTransform();
            Shape shape = a.getShape();
            if(shape == null)
                continue;

            // 1. CULLING: il raggio di culling dipende dal tipo di shape
            float boundHalfW, boundHalfH;
            if (shape instanceof Shape.Circle) {
                Shape.Circle sc = (Shape.Circle) shape;
                float r = sc.radius;
                boundHalfW = r;
                boundHalfH = r;
            } else if (shape instanceof Shape.Box) {
                Shape.Box sb = (Shape.Box) shape;
                boundHalfW = sb.halfWidth;
                boundHalfH = sb.halfHeight;
            } else {
                continue; // shape sconosciuta, skip difensivo
            }

            if (!camera.isVisible(transform.x, transform.y, boundHalfW, boundHalfH)) {
                continue;
            }

            // 2. CONVERSIONE: unico punto, delegata alla Camera
            float screenX = camera.toPixelsX(transform.x);
            float screenY = camera.toPixelsY(transform.y);
            float halfWidthPx = camera.toPixelsXLength(boundHalfW);
            float halfHeightPx = camera.toPixelsYLength(boundHalfH);
            // float rotationDeg = (float) Math.toDegrees(tc.angle) + 90f; // perché +90f?
            float rotationDeg = (float) Math.toDegrees(transform.angle) + dc.getVisualAngleOffsetDeg();

            scratchTransform.set(screenX, screenY, halfWidthPx, halfHeightPx, rotationDeg, halfWidthPx);

            // 3. DRAW: il component riceve solo dati già pronti
            // dispatch virtuale, RenderManager non sa se è Rect o Circle
            dc.draw(canvas, scratchTransform, scratchDst);
        }
    }

}