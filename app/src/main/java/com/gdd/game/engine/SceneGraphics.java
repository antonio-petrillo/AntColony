package com.gdd.game.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.components.Component;
import com.gdd.game.engine.components.ComponentType;
import com.gdd.game.engine.components.DrawableComponent;
import com.gdd.game.engine.components.TransformComponent;

import java.util.List;

public class SceneGraphics {


    // allocate once and used for every draw
    // scratch riusati, MAI allocati nel loop
    private final ScreenTransform scratchTransform = new ScreenTransform();
    private final RectF scratchDst = new RectF();
    private Paint paint;


    public SceneGraphics() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
    }


    public void render(Canvas canvas, Camera camera, List<Actor> actors) {

        if(canvas == null || actors == null)
            return;


        for(Actor a : actors) {
            Component c = a.getComponent(ComponentType.DRAWABLE);
            if(c != null) {
                DrawableComponent dc = (DrawableComponent) c;
                TransformComponent tc = a.getTransformComponent();

                // 1. CULLING: chiede alla Camera
                if( !camera.isVisible(tc.x, tc.y, tc.width/2, tc.height/2) )
                    continue;

                // 2. CONVERSIONE: unico punto, delegata alla Camera
                float screenX = camera.toPixelsX(tc.x);
                float screenY = camera.toPixelsY(tc.y);
                float halfWidthPx = camera.toPixelsXLength(0.3f);
                float halfHeightPx = camera.toPixelsYLength(0.3f);
                float rotationDeg = (float) Math.toDegrees(tc.angle) + 90f;

                scratchTransform.set(screenX, screenY, halfWidthPx, halfHeightPx, rotationDeg);

                // 3. DRAW: il component riceve solo dati già pronti
                dc.draw(canvas, paint, scratchTransform, scratchDst);
            }
        }
    }

}
