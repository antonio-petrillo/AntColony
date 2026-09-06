package com.gdd.game.cards;

import android.graphics.Bitmap;

public class Card {

    public enum Action {
        ATTACK_ALL(30, 1), HEAL_ALL(15, 1),
        ATTACK_ENEMY(20, 3), HEAL_ALLIES(10, 3),
        DOUBLE_ENERGY(-1, 0);

        public final int amount, cost;

        Action(int amount, int cost) {
            this.amount = amount;
            this.cost = cost;
        }

        public boolean canActivate(int energy) { return energy >= cost; }
    }

    private final int id;
    private final Bitmap artwork;
    public final Action action;
    public Card(int id, Action action, Bitmap artwork) {
        this.id = id;
        this.action = action;
        this.artwork = artwork;
    }

    public int getId() { return id; }

    public Action getAction() { return action; }

    public Bitmap getArtwork() { return artwork; }

    public boolean hasArtwork() { return artwork != null; }
}
