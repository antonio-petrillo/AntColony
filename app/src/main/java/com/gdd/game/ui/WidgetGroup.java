package com.gdd.game.ui;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

/**
 * Questa classe è un contenitore di Widget.
 */
public abstract class WidgetGroup extends Widget {

    protected List<Widget> children = new ArrayList<>();
    protected boolean layoutDirty = true;


    public WidgetGroup(float x, float y, float width, float height) {
        super(x, y, width, height);
        touchable = Touchable.CHILDREN_ONLY;
    }

    // ***************************************
    //  Layout
    // ***************************************

    /*
     * Calcola e assegna x, y, width, height dei figli
     * in base allo spazio disponibile in questo gruppo.
     * Non va chiamato direttamente: lo invoca validate().
     */
    protected void layout() {

    }

    /*
     * Segna "layout da ricalcolare" su questo gruppo.
     * Risale al parent: se il mio arrangiamento cambia,
     * anche quello del parent potrebbe non essere più valido.
     */
    public void invalidate() {
        layoutDirty = true;
        if (parent != null) parent.invalidate();
    }

    /*
     * Se il layout è "sporco", lo ricalcola (layout()) e pulisce il flag.
     * Poi scende sui figli che sono gruppi, per validare anche loro.
     * Va chiamato prima di disegnare o gestire input.
     */
    public void validate() {
        if (layoutDirty) {
            layout();
            layoutDirty = false;
        }
        for (Widget c : children) {
            if (c instanceof WidgetGroup) {
                ((WidgetGroup) c).validate();
            }
        }
    }

    /*
     * Traduce x, y locali (già decisi) in coordinate assolute a schermo,
     * sommando la posizione assoluta del parent.
     * Si attiva quando x/y locali cambiano o quando si muove il parent.
     */
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
        if (!visible || touchable == Touchable.DISABLED) return null;
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

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        invalidate();
    }

    public void addChild(Widget w) {
        w.setParent(this);
        w.transformDirty = true;
        children.add(w);
        invalidate();
    }

    /*
     * Warning: NON usare mentre il child possiede un pointer.
     *          UIController non viene notificato.
     */
    public void removeChild(Widget w) {
        if (children.remove(w)) {
            w.setParent(null);
            invalidate();
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
        invalidate();
    }

    public List<Widget> getChildren() {
        return children;
    }
}
