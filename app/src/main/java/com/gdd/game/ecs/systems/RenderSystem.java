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

    /*
    @Override
    public void update(List<Entity> entities, float dt) {
        Box view = gw.cameraView;

        for (var entity : entities) {
            var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
            var render = (RenderComponent) entity.getComponent(ComponentType.RENDER);

            if (phys == null || render == null) continue;

            float x = phys.body.getPositionX();
            float y = phys.body.getPositionY();

            if (x < view.xmin || x > view.xmax || y < view.ymin || y > view.ymax)
                continue;

            switch (entity.tag) {
                case ANT: renderAnt(x, y, phys.body.getAngle(), render); break;
                case NEST: renderNest(render); break;
                case FOOD: renderFood(x, y, render); break;
                case WASP: renderWasp(x, y, phys.body.getAngle(), render); break;
            }
        }
    }

    private final RectF dst = new RectF(); // allocate once and used for every drawBitmap
    private void renderFood(float x, float y, RenderComponent rc) {
        float screenX = gw.camera.toPixelsX(x);
        float screenY = gw.camera.toPixelsY(y);
        float half = gw.camera.toPixelsXLength(FoodFactory.RADIUS);

        dst.set(screenX - half, screenY - half, screenX + half, screenY + half);
        canvas.save();
        canvas.drawBitmap(Assets.FOOD_BITMAP, null, dst, rc.paint);
        canvas.restore();
    }

    private void renderNest(RenderComponent rc) {
        final float SIDE = 0.5f;
        float screenX = gw.camera.toPixelsX(0);
        float screenY = gw.camera.toPixelsY(0);
        float halfWidth = gw.camera.toPixelsXLength(SIDE * 2);
        float halfHeight = gw.camera.toPixelsYLength(SIDE);

        dst.set(screenX - halfWidth, screenY - halfHeight, screenX + halfWidth, screenY + halfHeight);
        canvas.save();
        canvas.drawBitmap(Assets.NEST_BITMAP, null, dst, rc.paint);
        canvas.restore();
    }

    private void renderWasp(float x, float y, float angle, RenderComponent rc) {
        float screenX = gw.camera.toPixelsX(x);
        float screenY = gw.camera.toPixelsY(y);

        final float halfWidth = gw.camera.toPixelsXLength(0.3f);
        final float halfHeight = gw.camera.toPixelsYLength(0.3f);

        dst.set(-halfWidth, -halfHeight, halfWidth, halfHeight);

        canvas.save();
        canvas.translate(screenX, screenY);
        canvas.rotate((float) Math.toDegrees(angle) + 90.0f);
        canvas.drawBitmap(Assets.WASP_BITMAP, null, dst, rc.paint);
        canvas.restore();
    }

    private void renderAnt(float x, float y, float angle, RenderComponent rc) {
        float screenX = gw.camera.toPixelsX(x);
        float screenY = gw.camera.toPixelsY(y);

        final float halfWidth = gw.camera.toPixelsXLength(0.3f);
        final float halfHeight = gw.camera.toPixelsYLength(0.3f);

        dst.set(-halfWidth, -halfHeight, halfWidth, halfHeight);

        canvas.save();
        canvas.translate(screenX, screenY);
        canvas.rotate((float) Math.toDegrees(angle) + 90.0f);
        canvas.drawBitmap(Assets.ANT_BITMAP, null, dst, rc.paint);
        canvas.restore();
    }
*/
