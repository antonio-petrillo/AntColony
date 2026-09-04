package com.gdd.game.cards;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class Hand {

    private final int BOTTOM_BAR_COLOR = 0xB42D1C0E;

    private final List<CardView> cards = new ArrayList<>();

    private final float targetAnchorX, targetAnchorY; // posizione fissa per la carta selezionata (stato TARGETING)

    private final RectF handArea; // area entro cui la carta è "in mano" (drag libero)
    private final float cardPeek; // quanto spunta la carta sopra il bordo superiore dell'area
    private final float maxSpread; // larghezza massima occupata dalle carte nel layout a riposo

    private final Paint barPaint = new Paint();


    public Hand(RectF handArea, float cardPeek, float maxSpread) {
        this.handArea = handArea;
        this.cardPeek = cardPeek;
        this.maxSpread = maxSpread;

        targetAnchorX = handArea.centerX();
        targetAnchorY = handArea.top - 5f;

        barPaint.setColor(BOTTOM_BAR_COLOR);
    }

    // ***************************************
    //  Game loop
    // ***************************************

    public void update(float dt) {
        for (CardView c : cards) c.update(dt);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(handArea, barPaint);
        for (CardView c : cards) c.draw(canvas);
    }

    // ***************************************
    //  Input methods
    // ***************************************

    public CardView hit(float px, float py) {
        for (int i = cards.size() - 1; i >= 0; i--) {
            if (cards.get(i).contains(px, py)) return cards.get(i);
        }
        return null;
    }

    public boolean isInsideArea(float px, float py) {
        return handArea.contains(px, py);
    }

    // ***************************************
    //  Misc
    // ***************************************

    public void add(Card card) {
        cards.add(new CardView(card)); //TODO: riciclare i cardview
        layout();
    }

    public void remove(CardView view) {
        cards.remove(view);
        layout();
    }

    /*
     * Calcola e assegna la posizione ad ogni carta nella mano.
     */
    private void layout() {
        int n = cards.size();
        if (n == 0) return;

        float cardSpacing = 90f;
        float effectiveWidth = Math.min((n - 1) * cardSpacing, maxSpread);
        float baseY = handArea.bottom - cardPeek;

        for (int i = 0; i < n; i++) {
            float t = n == 1 ? 0.5f : (float) i / (n - 1);
            float tx = handArea.centerX() + t * effectiveWidth - effectiveWidth / 2f;
            cards.get(i).setTarget(tx, baseY);
        }
    }

    /*
     * Usato per l'animazione di quando la carta è toccata.
     */
    public void bringToFront(CardView view) {
        cards.remove(view);
        cards.add(view);
    }

    public float getTargetAnchorX() { return targetAnchorX; }
    public float getTargetAnchorY() { return targetAnchorY; }

    public float getTargetAnchorArrowY() {
        return targetAnchorY - CardView.HEIGHT / 2f;
    }
}