package com.gdd.game.cards;

import static com.gdd.game.cards.Card.Action;
import static com.gdd.game.cards.Card.Rarity;

import com.gdd.game.Assets;

import java.util.Random;

public class Draw {

    private static int DECK_CARD_ID_BASE_OFFSET = 0xff;

    private static final Card[] commonPool;
    private static final Card[] uncommonPool;
    private static final Card[] rarePool;

    private static final Random rng = new Random();

    static {
        // COMMON
        commonPool = new Card[9];
        commonPool[0] = new Card(DECK_CARD_ID_BASE_OFFSET + 0, Action.ATTACK_ALL, Rarity.COMMON, Assets.CARD_ATTACK);
        commonPool[1] = new Card(DECK_CARD_ID_BASE_OFFSET + 1, Action.ATTACK_ALL, Rarity.COMMON, Assets.CARD_ATTACK);
        commonPool[2] = new Card(DECK_CARD_ID_BASE_OFFSET + 2, Action.ATTACK_ALL, Rarity.COMMON, Assets.CARD_ATTACK);
        commonPool[3] = new Card(DECK_CARD_ID_BASE_OFFSET + 12, Action.INCREASE_FOV, Rarity.COMMON, Assets.CARD_ATTACK);
        commonPool[4] = new Card(DECK_CARD_ID_BASE_OFFSET + 13, Action.INCREASE_FOV, Rarity.COMMON, Assets.CARD_ATTACK);
        commonPool[5] = new Card(DECK_CARD_ID_BASE_OFFSET + 15, Action.INCREASE_FOV, Rarity.COMMON, Assets.CARD_ATTACK);
        commonPool[6] = new Card(DECK_CARD_ID_BASE_OFFSET + 5, Action.HEAL_ALL, Rarity.COMMON, Assets.CARD_HEAL);
        commonPool[7] = new Card(DECK_CARD_ID_BASE_OFFSET + 6, Action.HEAL_ALL, Rarity.COMMON, Assets.CARD_HEAL);
        commonPool[8] = new Card(DECK_CARD_ID_BASE_OFFSET + 7, Action.HEAL_ALL, Rarity.COMMON, Assets.CARD_HEAL);

        uncommonPool = new Card[6];
        uncommonPool[0] = new Card(DECK_CARD_ID_BASE_OFFSET + 3, Action.ATTACK_ENEMY, Rarity.UNCOMMON, Assets.CARD_ATTACK);
        uncommonPool[1] = new Card(DECK_CARD_ID_BASE_OFFSET + 4, Action.ATTACK_ENEMY, Rarity.UNCOMMON, Assets.CARD_ATTACK);
        uncommonPool[2] = new Card(DECK_CARD_ID_BASE_OFFSET + 4, Action.ATTACK_ENEMY, Rarity.UNCOMMON, Assets.CARD_ATTACK);
        uncommonPool[3] = new Card(DECK_CARD_ID_BASE_OFFSET + 8, Action.HEAL_ALLIES, Rarity.UNCOMMON, Assets.CARD_HEAL);
        uncommonPool[4] = new Card(DECK_CARD_ID_BASE_OFFSET + 9, Action.HEAL_ALLIES, Rarity.UNCOMMON, Assets.CARD_HEAL);
        uncommonPool[5] = new Card(DECK_CARD_ID_BASE_OFFSET + 9, Action.HEAL_ALLIES, Rarity.UNCOMMON, Assets.CARD_HEAL);

        rarePool = new Card[3];
        rarePool[0] = new Card(DECK_CARD_ID_BASE_OFFSET + 10, Action.DOUBLE_SPEED, Rarity.RARE, Assets.CARD_ATTACK);
        rarePool[1] = new Card(DECK_CARD_ID_BASE_OFFSET + 11, Action.DOUBLE_ENERGY, Rarity.RARE, Assets.CARD_ATTACK);
        rarePool[2] = new Card(DECK_CARD_ID_BASE_OFFSET + 14, Action.SHUFFLE_HAND, Rarity.RARE, Assets.CARD_ATTACK);
    }

    public static Card nextCard() {
       // TODO: implement a proper distribution based on rarity and etc...
        int card = rng.nextInt(1 << 12); // damn Random class, nextInt return also negative numbers

        return switch (Rarity.getRandomRarity()) {
            case COMMON -> commonPool[card % commonPool.length];
            case UNCOMMON -> uncommonPool[card % uncommonPool.length];
            case RARE -> rarePool[card % rarePool.length];
        };
    }

    private Draw() {}
}
