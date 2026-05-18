package com.gdd.game.ecs.components;

import android.graphics.Paint;

import com.gdd.game.ecs.entities.Entity;

public class RenderComponent extends Component {

    // TODO: support paint and bitmap
    public Paint paint;

    public RenderComponent(Paint paint) {
        this.paint = paint;
    }

    @Override
    public final ComponentType type() {
        return ComponentType.RENDER;
    }


}
