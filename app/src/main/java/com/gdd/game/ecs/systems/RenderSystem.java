package com.gdd.game.ecs.systems;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.gdd.game.Assets;
import com.gdd.game.Box;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
import com.gdd.game.ecs.entities.Entity;

import java.util.List;

public class RenderSystem implements System {
    public final GameWorld gw;
    private final Canvas canvas;

    public RenderSystem(GameWorld gw) {
        this.gw = gw;
        canvas = new Canvas(gw.buffer);
    }

    // TODO: bodge
    @Override
    public void update(List<Entity> entities, float dt) {
        Box view = gw.currentView;

        for (var entity : entities) {
            var phys = (PhysicComponent) entity.getComponent(ComponentType.PHYSIC);
            var render = (RenderComponent) entity.getComponent(ComponentType.RENDER);

            if (phys == null || render == null) continue;

            float x = phys.body.getPositionX();
            float y = phys.body.getPositionY();

            if (x < view.xmin || x > view.xmax || y < view.ymin || y > view.ymax)
                continue;

            switch (entity.kind) {
                case ANT: renderAnt(x, y, phys.body.getAngle(), render); break;
                case NEST: renderNest(render); break;
                case WASP: break;
                case FOOD: break;
                case CARD: break;
            }
        }
    }

    private RectF dst = new RectF();
    private void renderNest(RenderComponent render) {
        final float SIDE = 0.5f;
        float screenX = gw.toPixelsX(0);
        float screenY = gw.toPixelsY(0);
        float halfWidth = gw.toPixelsXLength(SIDE * 2);
        float halfHeight = gw.toPixelsYLength(SIDE);

        dst.set(screenX - halfWidth, screenY - halfHeight, screenX + halfWidth, screenY + halfHeight);
        canvas.save();
        canvas.drawBitmap(Assets.NEST_BITMAP, null, dst, render.paint);
        canvas.restore();
    }

    private void renderAnt(float x, float y, float angle, RenderComponent render) {
        float screenX = gw.toPixelsX(x);
        float screenY = gw.toPixelsY(y);

//        angle = (float) Math.atan2(Math.sin(angle), Math.cos(angle));

        final float halfWidth = gw.toPixelsXLength(0.3f);
        final float halfHeight = gw.toPixelsYLength(0.3f);

        dst.set(-halfWidth, -halfHeight, halfWidth, halfHeight);

        canvas.save();
        canvas.translate(screenX, screenY);
        canvas.rotate((float) Math.toDegrees(angle) + 90.0f);
        canvas.drawBitmap(Assets.ANT_BITMAP, null, dst, render.paint);
        canvas.restore();
    }

}
