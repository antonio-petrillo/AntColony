package com.gdd.game.cards;

/**
 * Contratto che GameWorld implementa per ricevere notifiche dal layer Cards.
 */
public interface CardWorldListener {

    // Richiamato quando la freccia compare a schermo
    void onArrowShown();

    // Richiamato quando la freccia scompare dallo schermo
    void onArrowHidden();

    // Richiamato ad ogni frame mentre la freccia si muove
    void onArrowTipMoved(float x, float y);

    // Richimato quando il giocatore ha giocato una carta
    void onCardPlayed(Card card);
}
