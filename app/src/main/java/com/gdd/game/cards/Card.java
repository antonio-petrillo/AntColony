package com.gdd.game.cards;

import android.graphics.Bitmap;

import java.util.Random;

public class Card {

    public enum Rarity {
        COMMON(2.0f / 3),
        UNCOMMON(8.0f / 9),
        RARE (1.0f);

        private static final Random rng = new Random();
        private float threshold;

        Rarity(float threshold) {
            this.threshold = threshold;
        }
        public static Rarity getRandomRarity() {
            var pull = rng.nextFloat();
            if (pull <= COMMON.threshold) return COMMON;
            else if (pull <= UNCOMMON.threshold) return  UNCOMMON;
            else  return RARE;
        }
    }
    public enum Action {
        ATTACK_ALL(30, 1), HEAL_ALL(15, 1),
        ATTACK_ENEMY(20, 3), HEAL_ALLIES(10, 3),
        DOUBLE_ENERGY(-1, 1), SHUFFLE_HAND(-1, 1),
        DOUBLE_SPEED(-1, 1), INCREASE_FOV(-1, 1);

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
    public final Rarity rarity;
    public Card(int id, Action action, Rarity rarity, Bitmap artwork) {
        this.id = id;
        this.action = action;
        this.artwork = artwork;
        this.rarity = rarity;
    }

    public int getId() { return id; }

    public Action getAction() { return action; }

    public Bitmap getArtwork() { return artwork; }

    public boolean hasArtwork() { return artwork != null; }
}
