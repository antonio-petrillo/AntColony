package com.gdd.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public class Assets {

    private static boolean loaded = false;
    private Assets() {}


    public static final String APPNAME = "AntColony";
    private static final String ANT_BITMAP_PATH = "Ant.png";
    private static final String NEST_BITMAP_PATH = "Nest.png";
    private static final String FOOD_BITMAP_PATH = "Food.png";
    private static final String WASP_BITMAP_PATH = "Wasp.png";
    private static final String TERRAIN_BITMAP_PATH = "Terrain.png";


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

        if (WASP_BITMAP== null) {
            throw new RuntimeException("Can't load bitmap: " + WASP_BITMAP_PATH);
        }

        try {
            BUTTON_PAUSE = BitmapFactory.decodeStream( manager.open("bpause.png") );
            BUTTON_UP = BitmapFactory.decodeStream( manager.open("bup.png") );
            BUTTON_DOWN = BitmapFactory.decodeStream( manager.open("bdown.png") );
            BUTTON_LEFT = BitmapFactory.decodeStream( manager.open("bleft.png") );
            BUTTON_RIGHT = BitmapFactory.decodeStream( manager.open("bright.png") );
            BUTTON_PLUS = BitmapFactory.decodeStream( manager.open("bplus.png") );
            BUTTON_MINUS = BitmapFactory.decodeStream( manager.open("bminus.png") );
        } catch (IOException e) {
            throw new RuntimeException("Assets loading failed: ", e);
        }

        loaded = true;
    }

    public static Bitmap ANT_BITMAP;
    // TODO: find some sprites for these
    public static Bitmap NEST_BITMAP;
    public static Bitmap FOOD_BITMAP;
    public static Bitmap WASP_BITMAP;
    public static Bitmap TERRAIN_BITMAP;

    // UI TEST
    public static Bitmap BUTTON_PAUSE;
    public static Bitmap BUTTON_UP;
    public static Bitmap BUTTON_DOWN;
    public static Bitmap BUTTON_LEFT;
    public static Bitmap BUTTON_RIGHT;
    public static Bitmap BUTTON_PLUS;
    public static Bitmap BUTTON_MINUS;

}
