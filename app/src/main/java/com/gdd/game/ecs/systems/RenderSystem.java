package com.gdd.game.ecs.systems;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.RenderEffect;

import com.gdd.game.Assets;
import com.gdd.game.Box;
import com.gdd.game.Camera;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.Transform;
import com.gdd.game.ecs.factories.FoodFactory;
import com.gdd.game.ecs.misc.ScreenParams;

import java.util.List;

public class RenderSystem implements System {

    public final GameWorld gw;
    private final Canvas canvas;
    private final Camera camera;

    // allocate once and used for every draw
    private final ScreenParams scratchTransform = new ScreenParams();
    private final RectF scratchDst = new RectF();

    public RenderSystem(GameWorld gw, Camera camera) {
        this.gw = gw;
        this.camera = camera;
        this.canvas = gw.canvas;
        //canvas = new Canvas(gw.frameBuffer);
    }

    public void update(List<Entity> entities, float dt) {

        if(canvas == null || entities == null)
            return;

        int n = entities.size();
        for(int i=0; i<n; i++)  {

            Entity entity = entities.get(i);
            RenderComponent rc = (RenderComponent) entity.getComponent(ComponentType.RENDER);
            if(rc == null)
                continue;

            Transform transform = entity.transform;

            // 1. CULLING
            // TODO: draw the entity if partially inside the camera
            if (!camera.isVisible(transform.x, transform.y, transform.halfWidth, transform.halfHeight)) {
                continue;
            }

            // 2. CONVERSIONE WORLD->SCREEN
            float xPixel = camera.toPixelsX(transform.x);
            float yPixel = camera.toPixelsY(transform.y);
            float hWidthPixel = camera.toPixelsXLength(transform.halfWidth);
            float hHeightPixel = camera.toPixelsYLength(transform.halfHeight);
            float rotationDeg = (float) Math.toDegrees(transform.angle)
                    + rc.getVisualAngleOffsetDeg(); // serve +90f?

            scratchTransform.set(xPixel, yPixel, hWidthPixel, hHeightPixel,
                    rotationDeg);

            // 3. DRAW ACTOR
            rc.draw(canvas, scratchTransform, scratchDst);
        }
    }

}