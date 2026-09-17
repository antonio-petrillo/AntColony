package com.gdd.game.screen;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.gdd.game.Assets;
import com.gdd.game.Game;

import java.io.IOException;

public class LoadingScreen extends Screen {

    public LoadingScreen(Game game) {
        super(game);
    }

    // ***************************************
    //  Game loop
    // ***************************************

    public void update(float deltaTime) {

        AssetManager manager = Assets.manager;

        // ***** MUSIC & SOUNDS *****

        Assets.song = game.getAudio().newMusic("mario64_theme.mp3");

        Assets.click = game.getAudio().newSound("click.ogg");
        Assets.card_draw1 = game.getAudio().newSound("card_draw1.mp3");
        Assets.card_draw2 = game.getAudio().newSound("card_draw2.mp3");
        Assets.card_disappear = game.getAudio().newSound("card_disappear.mp3");

        // ***** BITMAPS *****

        // --- GAME WORLD

        Assets.ANT_BITMAP = loadBitmap(manager, "ant_32x32.png");
        Assets.WASP_BITMAP = loadBitmap(manager, "wasp_32x32.png");
        Assets.FOOD_BITMAP = loadBitmap(manager, "sugar_cube_64x64.png");
        Assets.NEST_BITMAP = loadBitmap(manager, "nest_128x128.png");
        Assets.TERRAIN_BITMAP = loadBitmap(manager, "grass_tile_128x128.png");

        // --- UI: BUTTONS

        Assets.STARTGAME_BUTTON_IDLE = loadBitmap(manager, "startgame_button_idle_128x64.png");
        Assets.STARTGAME_BUTTON_PRESSED = loadBitmap(manager, "startgame_button_pressed_128x64.png");

        Assets.PAUSEBUTTON_IDLE_BITMAP = loadBitmap(manager, "pause_button_idle_64x64.png");
        Assets.PAUSEBUTTON_IDLE_BITMAP = loadBitmap(manager, "pause_button_idle_64x64.png");

        Assets.CONTINUEBUTTON_IDLE = loadBitmap(manager, "continue_button_idle_128x64.png");
        Assets.CONTINUEBUTTON_PRESSED = loadBitmap(manager, "continue_button_pressed_128x64.png");

        // --- UI: HUD

        Assets.MAIN_MENU_BG = loadBitmap(manager, "main_menu_bg.jpg");
        Assets.TITLE_PAUSEMENU = loadBitmap(manager, "title_pause_menu.png");

        Assets.HUD_BADGE_ANT_BITMAP = loadBitmap(manager, "hud_badge_ant_150x64.png");
        Assets.HUD_BADGE_SUGAR_PRESSED = loadBitmap(manager, "hud_badge_sugar_150x64.png");

        // --- CARDS

        Assets.CARD_ATTACK_ALL = loadBitmap(manager, "card_damage_all_64x128.png");
        Assets.CARD_ATTACK_ENEMIES = loadBitmap(manager, "card_damage_enemies_64x128.png");
        Assets.CARD_HEAL_ALL = loadBitmap(manager, "card_heal_all_64x128.png");
        Assets.CARD_HEAL_ALLIES = loadBitmap(manager, "card_heal_allies_64x128.png");
        Assets.CARD_DOUBLE_ENERGY = loadBitmap(manager, "card_double_energy_64x128.png");
        Assets.CARD_DOUBLE_SPEED = loadBitmap(manager, "card_speed_64x128.png");
        Assets.CARD_SHUFFLE_HAND = loadBitmap(manager, "card_shuffle_64x128.png");
        Assets.CARD_INCREASE_FOV = loadBitmap(manager, "card_fov_64x128.png");

        // --- VFX

        Assets.STATUS_FOV = loadBitmap(manager, "status_fov_16x16.png");
        Assets.STATUS_SPEED = loadBitmap(manager, "status_speed_16x16.png");
        Assets.EFFECT_DAMAGE = loadBitmap(manager, "effect_damage_48x48.png");
        Assets.EFFECT_HEAL = loadBitmap(manager, "effect_heal_48x48.png");

        game.setScreen(new MainMenuScreen(game));
    }

    public void render() {

    }

    // ***************************************
    //  Android callbacks
    // ***************************************

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    // ***************************************
    //  Load bitmap
    // ***************************************

    public Bitmap loadBitmap(AssetManager manager, String path) {

        Bitmap bitmap;

        try (var stream = manager.open(path)) {
            bitmap = BitmapFactory.decodeStream(stream);
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        if (bitmap == null) {
            throw new RuntimeException("Can't load bitmap: " + path);
        }

        return bitmap;
    }
}
