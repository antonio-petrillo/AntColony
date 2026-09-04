package com.gdd.game.cards;

import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;

public class CardController {

    private final CardWorldListener worldListener;

    private enum State { IDLE, DRAGGING, TARGETING }
    private State state = State.IDLE;

    private int activePointerId = -1;

    private final Hand hand;
    private CardView card;
    private final TargetArrow arrow;

    private float grabOffsetX, grabOffsetY; // offset tra pivot carta e punto di touch al DOWN


    public CardController(Hand hand, TargetArrow arrow, CardWorldListener worldListener) {
        this.hand = hand;
        this.arrow = arrow;
        this.worldListener = worldListener;
    }

    // ***************************************
    //  Game loop
    // ***************************************

    public boolean processInput(Input.TouchEvent e) {
        if (e == null) return false;

        switch (e.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                if(activePointerId != -1) return false;
                CardView hit = hand.hit(e.x, e.y);
                if (hit != null) {
                    activePointerId = e.pointer;
                    onDragStart(hit, e.x, e.y);
                    return true;
                }
                // blocca l'input se clicchi sulla barra di sotto
                if (hand.isInsideArea(e.x, e.y)) {
                    return true;
                }
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                if (e.pointer != activePointerId) return false;
                onDrag(e.x, e.y);
                return true;
            case Input.TouchEvent.TOUCH_UP:
                if (e.pointer != activePointerId) return false;
                onDragEnd(e.x, e.y);
                activePointerId = -1;
                return true;
        }

        return false;
    }

    public void update(float dt) {
        hand.update(dt);
    }

    public void draw(Canvas canvas) {
        if (state == State.TARGETING)
            arrow.draw(canvas);
        hand.draw(canvas);
    }

    // ***************************************
    //  Card dragging
    // ***************************************

    private void onDragStart(CardView hit, float px, float py) {
        card = hit;
        grabOffsetX = card.getPivotX() - px;
        grabOffsetY = card.getPivotY() - py;

        card.moveTowards(card.getPivotX(), card.getPivotY());

        hand.bringToFront(card);
        card.setState(CardView.State.DRAGGING);
        state = State.DRAGGING;
    }

    private void onDrag(float px, float py) {
        boolean insideArea = hand.isInsideArea(px, py);

        if (state == State.DRAGGING && !insideArea) {
            state = State.TARGETING;
            card.setState(CardView.State.TARGETING);
            arrow.startAt(hand.getTargetAnchorX(), hand.getTargetAnchorArrowY());
            arrow.show();
            worldListener.onArrowShown();
        } else if (state == State.TARGETING && insideArea) {
            state = State.DRAGGING;
            card.setState(CardView.State.DRAGGING);
            arrow.hide();
            worldListener.onArrowHidden();
        }

        if (state == State.TARGETING) {
            card.moveTowards(hand.getTargetAnchorX(), hand.getTargetAnchorY());
            arrow.updateTip(px, py);
            worldListener.onArrowTipMoved(px, py);
        } else {
            card.moveTowards(px + grabOffsetX, py + grabOffsetY);
        }
    }

    private void onDragEnd(float px, float py) {
        if (state == State.TARGETING) {
            worldListener.onCardPlayed(card.getCard());
            hand.remove(card);
        } else {
            card.setState(CardView.State.IDLE);
        }
        arrow.hide();
        worldListener.onArrowHidden();
        state = State.IDLE;
        card = null;
    }
}