package com.gdd.game.engine.components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.gdd.game.engine.ScreenTransform;

public abstract class DrawableComponent extends Component {

    @Override
    public final ComponentType type() {
        return ComponentType.DRAWABLE;
    }

    public abstract void draw(Canvas canvas, Paint paint, ScreenTransform st, RectF dst);
}