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


    public static final String APPNAME = "AntColony";
    private static final String ANT_BITMAP_PATH = "ant_32x32.png";
    private static final String NEST_BITMAP_PATH = "nest_128x128.png";
    private static final String FOOD_BITMAP_PATH = "sugar_cube_64x64.png";
    private static final String WASP_BITMAP_PATH = "wasp_32x32.png";
    private static final String TERRAIN_BITMAP_PATH = "grass_tile_128x128.png";
    private static final String MAINBG_BITMAP_PATH = "menu_bg_landscape_1280x720.png";
    private static final String MAINTITLE_BITMAP_PATH = "title_ant_colony.png";
    private static final String PAUSEBUTTON_IDLE_PATH = "pause_button_idle_64x64.png";
    private static final String PAUSEBUTTON_PRESSED_PATH = "pause_button_pressed_64x64.png";
    private static final String HUD_BADGE_ANT_PATH = "hud_badge_ant_150x64.png";
    private static final String HUD_BADGE_SUGAR_PATH = "hud_badge_sugar_150x64.png";
    private static final String CONTINUEBUTTON_IDLE_PATH = "continue_button_idle_128x64.png";
    private static final String CONTINUEBUTTON_PRESSED_PATH = "continue_button_pressed_128x64.png";
    private static final String TITLE_PAUSEMENU_PATH = "title_pause_menu.png";
    private static final String CARD_ATTACK_PATH = "card_attack_64x128.png";
    private static final String CARD_HEAL_PATH = "card_heal_64x128.png";

    public static void load(AssetManager manager) {
       if (loaded)
           throw new IllegalStateException("Assets already loaded!");

        try (var stream = manager.open(ANT_BITMAP_PATH)) {
            ANT_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (ANT_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + ANT_BITMAP_PATH);
        }

        try (var stream = manager.open(NEST_BITMAP_PATH)) {
            NEST_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (ANT_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + ANT_BITMAP_PATH);
        }

        try (var stream = manager.open(FOOD_BITMAP_PATH)) {
            FOOD_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (FOOD_BITMAP== null) {
            throw new RuntimeException("Can't load bitmap: " + FOOD_BITMAP_PATH);
        }

        try (var stream = manager.open(WASP_BITMAP_PATH)) {
            WASP_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (WASP_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + WASP_BITMAP_PATH);
        }

        try (var stream = manager.open(TERRAIN_BITMAP_PATH)) {
            TERRAIN_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (TERRAIN_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + TERRAIN_BITMAP);
        }

        try (var stream = manager.open(MAINBG_BITMAP_PATH)) {
            MAINBG_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (MAINBG_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + MAINBG_BITMAP_PATH);
        }

        try (var stream = manager.open(MAINTITLE_BITMAP_PATH)) {
            MAINTITLE_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (MAINTITLE_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + MAINTITLE_BITMAP);
        }

        // ***** GameScreen UI *****

        try (var stream = manager.open(PAUSEBUTTON_IDLE_PATH)) {
            PAUSEBUTTON_IDLE_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (PAUSEBUTTON_IDLE_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + PAUSEBUTTON_IDLE_BITMAP);
        }

        try (var stream = manager.open(PAUSEBUTTON_PRESSED_PATH)) {
            PAUSEBUTTON_PRESSED = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (PAUSEBUTTON_PRESSED == null) {
            throw new RuntimeException("Can't load bitmap: " + PAUSEBUTTON_PRESSED);
        }

        // ***** GameScreen HUD *****

        try (var stream = manager.open(HUD_BADGE_ANT_PATH)) {
            HUD_BADGE_ANT_BITMAP = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (HUD_BADGE_ANT_BITMAP == null) {
            throw new RuntimeException("Can't load bitmap: " + HUD_BADGE_ANT_BITMAP);
        }

        try (var stream = manager.open(HUD_BADGE_SUGAR_PATH)) {
            HUD_BADGE_SUGAR_PRESSED = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (HUD_BADGE_SUGAR_PRESSED == null) {
            throw new RuntimeException("Can't load bitmap: " + HUD_BADGE_SUGAR_PRESSED);
        }

        try (var stream = manager.open(CONTINUEBUTTON_IDLE_PATH)) {
            CONTINUEBUTTON_IDLE = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (CONTINUEBUTTON_IDLE == null) {
            throw new RuntimeException("Can't load bitmap: " + CONTINUEBUTTON_IDLE);
        }

        try (var stream = manager.open(CONTINUEBUTTON_PRESSED_PATH)) {
            CONTINUEBUTTON_PRESSED = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (CONTINUEBUTTON_PRESSED == null) {
            throw new RuntimeException("Can't load bitmap: " + CONTINUEBUTTON_PRESSED);
        }

        try (var stream = manager.open(TITLE_PAUSEMENU_PATH)) {
            TITLE_PAUSEMENU = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (TITLE_PAUSEMENU == null) {
            throw new RuntimeException("Can't load bitmap: " + TITLE_PAUSEMENU);
        }


        try (var stream = manager.open(CARD_ATTACK_PATH)) {
            CARD_ATTACK = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (CARD_ATTACK == null) {
            throw new RuntimeException("Can't load bitmap: " + CARD_ATTACK);
        }



        try (var stream = manager.open(CARD_HEAL_PATH)) {
            CARD_HEAL = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (CARD_HEAL == null) {
            throw new RuntimeException("Can't load bitmap: " + CARD_HEAL);
        }


        loaded = true;
    }

    public static Bitmap ANT_BITMAP;
    public static Bitmap NEST_BITMAP;
    public static Bitmap FOOD_BITMAP;
    public static Bitmap WASP_BITMAP;
    public static Bitmap TERRAIN_BITMAP;
    public static Bitmap MAINBG_BITMAP;
    public static Bitmap MAINTITLE_BITMAP;
    public static Bitmap PAUSEBUTTON_IDLE_BITMAP;
    public static Bitmap PAUSEBUTTON_PRESSED;
    public static Bitmap HUD_BADGE_ANT_BITMAP;
    public static Bitmap HUD_BADGE_SUGAR_PRESSED;
    public static Bitmap CONTINUEBUTTON_IDLE;
    public static Bitmap CONTINUEBUTTON_PRESSED;
    public static Bitmap TITLE_PAUSEMENU;

    public static Bitmap CARD_ATTACK;
    public static Bitmap CARD_HEAL;

    public static Music song;
    public static Sound click;
}
