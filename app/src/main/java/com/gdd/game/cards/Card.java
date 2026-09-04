package com.gdd.game.cards;

import android.graphics.Bitmap;

public class Card {

    public enum Type { ATTACK, HEAL }
    public enum TargetType { NONE, ANT, WASP }

    private final int id;
    private final Type type;
    private final TargetType targetType;
    private final Bitmap artwork;

    public Card(int id, Type type, TargetType targetType, Bitmap artwork) {
        this.id = id;
        this.type = type;
        this.targetType = targetType;
        this.artwork = artwork;
    }

    public int getId() { return id; }

    public Type getType() { return type; }

    public TargetType getTargetType() { return targetType; }

    public Bitmap getArtwork() { return artwork; }

    public boolean hasArtwork() { return artwork != null; }
}
