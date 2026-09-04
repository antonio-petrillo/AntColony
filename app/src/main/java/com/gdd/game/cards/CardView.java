package com.gdd.game.cards;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CardView {

    public enum State { IDLE, DRAGGING, TARGETING }
    private State state = State.IDLE;

    private final Card card;

    public static final float WIDTH = 60f, HEIGHT = 100f; // Dimensione della carta in pixel
    private static final float DRAG_LERP_SPEED = 20f;
    private static final float LERP_SPEED = 14f;

    private float x, y; // posizione corrente
    private float targetX, targetY; // posizione di riferimento per lo stato IDLE
    private float dragTargetX, dragTargetY; // posizione di riferimento per lo stato TARGETING

    private final Rect destRect = new Rect();
    private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);


    public CardView(Card card) {
        this.card = card;
        this.dragTargetX = 0;
        this.dragTargetY = 0;
    }

    // ***************************************
    //  Game loop
    // ***************************************

    /*
     * Gestisce l'animazione del movimento della carta tramite interpolazione.
     */
    public void update(float dt) {
        float t;
        if (state == State.IDLE) {
            t = Math.min(1f, LERP_SPEED * dt);
            x += (targetX - x) * t;
            y += (targetY - y) * t;
        } else {
            // state == DRAGGING or TARGETING
            t = Math.min(1f, DRAG_LERP_SPEED * dt);
            x += (dragTargetX - x) * t;
            y += (dragTargetY - y) * t;
        }
    }

    /*
     * Disegna la carta a schermo.
     */
    public void draw(Canvas canvas) {
        if(!card.hasArtwork()) return;

        destRect.set(
                (int) (x - WIDTH / 2f),
                (int) (y - HEIGHT),
                (int) (x + WIDTH / 2f),
                (int) y
        );

        canvas.drawBitmap(card.getArtwork(), null, destRect, paint);
    }

    // ***************************************
    //  Input & hit-test
    // ***************************************

    public boolean contains(float px, float py) {
        return px >= x - WIDTH / 2f && px <= x + WIDTH / 2f
                && py <= y && py >= y - HEIGHT;
    }

    public void followFinger(float px, float py) {
        x = px;
        y = py;
    }

    public void holdAt(float px, float py) {
        x = px;
        y = py;
    }

    // ***************************************
    //  Layout target (gestito da Hand)
    // ***************************************

    public void setTarget(float tx, float ty) {
        targetX = tx;
        targetY = ty;
        if (state == State.IDLE && x == 0 && y == 0) {
            // snap iniziale la prima volta, evita di partire da (0,0) con animazione visibile
            x = tx;
            y = ty;
        }
    }

    // ***************************************
    //  Getters / state
    // ***************************************

    public void setState(State s) { this.state = s; }

    public State getState() { return state; }
    public Card getCard() { return card; }

    public float getAnchorX() { return x; }
    public float getAnchorY() { return y - HEIGHT; }

    public void moveTowards(float tx, float ty) {
        dragTargetX = tx;
        dragTargetY = ty;
    }

    public float getPivotX() { return x; }
    public float getPivotY() { return y; }

    public float getCenterX() { return x; }
    public float getCenterY() { return y - HEIGHT / 2f; }
}