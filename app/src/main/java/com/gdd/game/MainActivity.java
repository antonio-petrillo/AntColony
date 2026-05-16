package com.gdd.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;

import java.io.IOException;

public class MainActivity extends Activity {

    private AndroidFastRenderView renderView;
    private Music backgroundMusic;

    // boundaries of the physical simulation
    private static final float XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;

    // the tag used for logging
    public static String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        TAG = Assets.APPNAME;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        var manager = getAssets();
        Assets.load(manager);

        // Sound
        Audio audio = new AndroidAudio(this);
        backgroundMusic = audio.newMusic("soundtrack.mp3");
        backgroundMusic.play();

        // Game world
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box physicalSize = new Box(XMIN, YMIN, XMAX, YMAX),
            screenSize   = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        GameWorld gw = new GameWorld(physicalSize, screenSize, this);

        // View
        renderView = new AndroidFastRenderView(this, gw);
        setContentView(renderView);

        // Touch
        MultiTouchHandler touch = new MultiTouchHandler(renderView, 1, 1);
        // Setter needed due to cyclic dependency
        gw.setTouchHandler(touch);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Main thread", "pause");
        renderView.pause(); // stops the main loop
        backgroundMusic.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("Main thread", "stop");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("Main thread", "resume");

        renderView.resume(); // starts game loop in a separate thread
        backgroundMusic.play();

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int counter = pref.getInt("INFO", -1); // default value
        Log.i("Main thread", "read counter " + counter);
    }
}
