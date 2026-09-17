package com.gdd.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.Sound;

import java.io.IOException;

public class Assets {

    private static boolean loaded = false;
    private Assets() {}

    public static AssetManager manager;

    public static final String APPNAME = "AntColony";

    private static final String DRAW_BUTTON_IDLE_PATH = "draw_button_idle_128x64.png";
    private static final String DRAW_BUTTON_PRESSED_PATH = "draw_button_pressed_128x64.png";
    private static final String DRAW_BUTTON_DISABLED_PATH = "draw_button_disabled_128x64.png";

    /*
    public static void load(AssetManager manager) {
       if (loaded)
           throw new IllegalStateException("Assets already loaded!");

        loaded = true;
    }
    */

    public static Bitmap ANT_BITMAP;
    public static Bitmap NEST_BITMAP;
    public static Bitmap FOOD_BITMAP;
    public static Bitmap WASP_BITMAP;
    public static Bitmap TERRAIN_BITMAP;
    public static Bitmap MAIN_MENU_BG;
    public static Bitmap PAUSEBUTTON_IDLE_BITMAP;
    public static Bitmap PAUSEBUTTON_PRESSED;
    public static Bitmap HUD_BADGE_ANT_BITMAP;
    public static Bitmap HUD_BADGE_SUGAR_PRESSED;
    public static Bitmap CONTINUEBUTTON_IDLE;
    public static Bitmap CONTINUEBUTTON_PRESSED;
    public static Bitmap TITLE_PAUSEMENU;

    public static Bitmap CARD_ATTACK_ALL;
    public static Bitmap CARD_ATTACK_ENEMIES;
    public static Bitmap CARD_HEAL_ALL;
    public static Bitmap CARD_HEAL_ALLIES;
    public static Bitmap CARD_DOUBLE_ENERGY;
    public static Bitmap CARD_DOUBLE_SPEED;
    public static Bitmap CARD_SHUFFLE_HAND;
    public static Bitmap CARD_INCREASE_FOV;


    public static Bitmap DRAW_BUTTON_IDLE;
    public static Bitmap DRAW_BUTTON_PRESSED;
    public static Bitmap DRAW_BUTTON_DISABLED;

    public static Bitmap STARTGAME_BUTTON_IDLE;
    public static Bitmap STARTGAME_BUTTON_PRESSED;

    public static Bitmap EFFECT_DAMAGE;
    public static Bitmap EFFECT_HEAL;
    public static Bitmap STATUS_FOV;
    public static Bitmap STATUS_SPEED;

    public static Music song;
    public static Sound click;

    public static Sound card_draw1, card_draw2, card_draw3, card_draw4, card_draw5;
    public static Sound card_disappear;
}
