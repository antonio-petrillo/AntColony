package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

/**
 * Questa classe rappresenta un generico componente dell'interfaccia utente.
 * Occupa un'area rettangolare a schermo.
 */
public abstract class Widget {

    public enum Touchable { ENABLED, DISABLED, CHILDREN_ONLY }

    protected float x, y, width, height; // coordinate locali
    protected float absX, absY; // coordinate assolute
    protected boolean transformDirty = true;
    protected boolean visible = true;
    protected Touchable touchable = Touchable.ENABLED;
    protected WidgetGroup parent;

    /*
     * Richiede valori locali in pixel (relativi al parent).
     */
    public Widget(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    // ***************************************
    //  Layout
    // ***************************************

    /*
     * Aggiorna la posizione assoluta.
     * Funziona se il parent è stato già validato.
     */
    protected void validateTransform() {
        if (!transformDirty) return;

        if (parent != null) {
            absX = parent.absX + x;
            absY = parent.absY + y;
        } else {
            absX = x; absY = y;
        }

        transformDirty = false;
    }

    // ***************************************
    //  Render
    // ***************************************

    /*
     * Deve disegnare in coordinate assolute
     */
    public abstract void draw(Canvas canvas);

    // ***************************************
    //  Input
    // ***************************************

    /*
     * Verifica l'input, in coordinate assolute.
     */
    public Widget hit(float x, float y) {
        if (!visible || touchable == Touchable.DISABLED) return null;
        return (x >= absX && x < absX + width && y >= absY && y < absY + height) ? this : null;
    }

    /*
     * Verifica se il punto (x,y) in coordinate assolute è dentro il widget.
     */
    public boolean contains(float x, float y) {
        return (x >= absX && x < absX + width && y >= absY && y < absY + height);
    }

    public boolean touchDown(float x, float y, int pointer) { return false; }

    public void touchDragged(float x, float y, int pointer) { }

    public void touchUp(float x, float y, int pointer) { }

    public void touchCancelled(int pointer) { }

    // ********************************
    //  Getter / Setter
    // ********************************

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        transformDirty = true;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        transformDirty = true;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public WidgetGroup getParent() { return parent; }
    public void setParent(WidgetGroup parent) { this.parent = parent; }

    /*
     * Warning: NON usare mentre this possiede un pointer.
     *      UIController non viene notificato.
     */
    public void setTouchable(Touchable t) {
        this.touchable = t;
    }
}
