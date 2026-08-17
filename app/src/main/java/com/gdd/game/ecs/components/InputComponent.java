package com.gdd.game.ecs.components;

public class InputComponent extends Component {

    public boolean isDraggable = true;

    @Override
    public final ComponentType type() {
        return ComponentType.INPUT;
    }

    public boolean isDraggable() {
        return isDraggable;
    }

    public void onTap() {
    }

    public void onDragStart(float x, float y) {
    }

    public void onDrag(float x, float y) {
        // TEST
        owner.transform.x += x;
        owner.transform.y += y;
    }

    public void onDragEnd(float x, float y) {
    }

    public void onDragCancel() {

    }
}
