package com.gdd.game.ecs.components;

import android.graphics.Paint;

public class RenderComponent extends Component {

    public enum Kind {
        ANT, NEST, WASP, FOOD, CARD;
    }

    // TODO: support paint and bitmap
    public Paint paint;
    public Kind kind;

    public RenderComponent(Paint paint, Kind kind) {
        this.paint = paint;
        this.kind = kind;
    }

    @Override
    public final ComponentType type() {
        return ComponentType.RENDER;
    }


}
