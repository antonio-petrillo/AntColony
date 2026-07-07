package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

/**
 * Widget rappresenta un generico elemento rettangolare dell'interfaccia.
 */
public abstract class Widget {

    protected float x, y, width, height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected WidgetGroup parent;

    public Widget(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }


    // ***************************************
    //            Ciclo di vita
    // ***************************************

    public abstract void update(float deltaTime);

    public abstract void draw(Canvas canvas, Paint paint);
    // Gestione Input (Ritorna true se l'evento è stato "consumato")
    // public abstract boolean handleInput(Input.TouchEvent event);

    // Ritorna true se il touch è dentro i confini dell'elemento (Collision Box)
    public boolean contains(float px, float py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }


    // ***************************************
    //  Gestione input grezzi in base al type
    // ***************************************

    public boolean touchDown(float px, float py, int pointer) { return false; }

    public void touchDragged(float px, float py, int pointer) { }

    public void touchUp(float px, float py, int pointer) { }


    // ********************************
    //          Getter / Setter
    // ********************************

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public WidgetGroup getParent() { return parent; }
    public void setParent(WidgetGroup parent) { this.parent = parent; }


}
