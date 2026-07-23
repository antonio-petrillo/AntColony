package com.gdd.game.engine.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.ScreenParams;

public abstract class DrawableComponent extends Component {

    protected float visualAngleOffsetDeg = 0f;

    @Override
    public final ComponentType type() {
        return ComponentType.DRAWABLE;
    }

    /** Offset visivo in gradi, sommato alla rotazione fisica dal RenderManager */
    public float getVisualAngleOffsetDeg() {
        return visualAngleOffsetDeg;
    }

    public abstract void draw(Canvas canvas, ScreenParams st, RectF dst);
}