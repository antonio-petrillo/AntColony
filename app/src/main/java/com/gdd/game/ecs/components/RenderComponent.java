package com.gdd.game.ecs.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.misc.ScreenParams;

public abstract class RenderComponent extends Component {

    protected float visualAngleOffsetDeg = 0f;

    @Override
    public final ComponentType type() {
        return ComponentType.RENDER;
    }

    public float getVisualAngleOffsetDeg() {
        return visualAngleOffsetDeg;
    }

    public abstract void draw(Canvas canvas, ScreenParams st, RectF dst);
}
