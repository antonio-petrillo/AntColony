package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe è un contenitore di Widget.
 */
public abstract class WidgetGroup extends Widget {

    protected List<Widget> children = new ArrayList<>();

    /*
     * Constructor.
     */
    public WidgetGroup(float x, float y, float width, float height) {
        super(x, y, width, height);
        touchable = Touchable.CHILDREN_ONLY;
    }

    // ***************************************
    //  Layout
    // ***************************************

    @Override
    protected void validateTransform() {
        super.validateTransform();
        for (int i = children.size() - 1; i >= 0; i--) {
            children.get(i).validateTransform();
        }
    }

    // ***************************************
    //  Drawing
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        for (int i = 0; i < children.size(); i++) {
            Widget c = children.get(i);
            if (c.visible) c.draw(canvas);
        }
    }

    // ***************************************
    //  Input
    // ***************************************

    /*
     * Ritorna il widget che viene "colpito" dal punto (x,y) in pixel.
     * Eventuali widget con enabled = false o visibile = false vengono ignorati.
     */
    @Override
    public Widget hit(float x, float y) {
        if (!visible) return null;
        for (int i = children.size() - 1; i >= 0; i--) {
            Widget hit = children.get(i).hit(x, y);
            if (hit != null) return hit;
        }
        if (touchable == Touchable.CHILDREN_ONLY) return null;
        return super.hit(x, y);
    }

    // ***************************************
    //  Misc
    // ***************************************

    public void addChild(Widget w) {
        w.setParent(this);
        w.transformDirty = true;
        children.add(w);
    }

    /*
     * Warning: NON usare mentre il child possiede un pointer.
     *          UIController non viene notificato.
     */
    public void removeChild(Widget w) {
        if (children.remove(w)) {
            w.setParent(null);
        }
    }

    /*
     * Warning: NON usare mentre il child possiede un pointer.
     *          UIController non viene notificato.
     */
    public void clearChildren() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setParent(null);
        }
        children.clear();
    }

    public List<Widget> getChildren() {
        return children;
    }
}
