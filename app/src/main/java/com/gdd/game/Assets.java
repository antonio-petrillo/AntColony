package com.gdd.game;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

        loaded = true;
    }

    public static Bitmap ANT_BITMAP;
    public static Bitmap NEST_BITMAP;
    public static Bitmap FOOD_BITMAP;
    public static Bitmap WASP_BITMAP;
    public static Bitmap TERRAIN_BITMAP;
    public static Bitmap MAINBG_BITMAP;
    public static Bitmap MAINTITLE_BITMAP;

}
