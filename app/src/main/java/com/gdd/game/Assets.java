package com.gdd.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;

public class Assets {

    public static final String APPNAME = "AntColony";
    public static AssetManager manager;
    private static boolean loaded = false;

    private Assets() {}


    // ***** MUSIC & SOUNDS *****

    public static Music SONG_GAMEPLAY;
    public static Sound CLICK;
    public static Sound CARD_DRAW1, CARD_DRAW2;
    public static Sound CARD_DISAPPEAR;

    // ***** BITMAPS *****

    // --- GAME WORLD

    public static Bitmap ANT_BITMAP;
    public static Bitmap WASP_BITMAP;
    public static Bitmap NEST_BITMAP;
    public static Bitmap FOOD_BITMAP;
    public static Bitmap TERRAIN_BITMAP;

    // --- UI: BUTTONS

    public static Bitmap BUTTON_STARTGAME_IDLE, BUTTON_STARTGAME_PRESSED;
    public static Bitmap BUTTON_PAUSE_IDLE, BUTTON_PAUSE_PRESSED;
    public static Bitmap BUTTON_CONTINUE_IDLE, BUTTON_CONTINUE_PRESSED;

    // --- UI: HUD
    public static Bitmap MAIN_MENU_BG;
    public static Bitmap TITLE_PAUSE_MENU;
    public static Bitmap BADGE_ANT;
    public static Bitmap BADGE_ENERGY;

    // --- CARDS

    public static Bitmap CARD_ATTACK_ALL, CARD_ATTACK_ENEMIES;
    public static Bitmap CARD_HEAL_ALL, CARD_HEAL_ALLIES;
    public static Bitmap CARD_DOUBLE_ENERGY;
    public static Bitmap CARD_DOUBLE_SPEED;
    public static Bitmap CARD_SHUFFLE_HAND;
    public static Bitmap CARD_INCREASE_FOV;

    // --- VFX

    public static Bitmap STATUS_FOV;
    public static Bitmap STATUS_SPEED;
    public static Bitmap EFFECT_DAMAGE;
    public static Bitmap EFFECT_HEAL;


    /*
    // Unused
    public static void load(AssetManager manager) {
       if (loaded)
           throw new IllegalStateException("Assets already loaded!");

        loaded = true;
    }
    */
}
