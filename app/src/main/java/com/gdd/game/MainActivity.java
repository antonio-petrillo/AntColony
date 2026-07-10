package com.gdd.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.AndroidAudio;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;

public class MainActivity extends Activity {

    private AndroidFastRenderView renderView;
    private Music backgroundMusic;

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

        // ***** GAME *****
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Box screenSize   = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);

        // World: physical simulation
        float halfWorldWidth = Settings.worldWidth / 2;
        float halfWorldHeight = Settings.worldHeight / 2;
        Box worldSize = new Box(-halfWorldWidth, -halfWorldHeight,
                halfWorldWidth, halfWorldHeight);

        Bitmap frameBuffer = Bitmap.createBitmap(Settings.fbufferWidth, Settings.fbufferHeight,
                Bitmap.Config.ARGB_8888);
        GameWorld gw = new GameWorld(this, frameBuffer, worldSize, screenSize);

        // ***** SURFACE VIEW *****
        renderView = new AndroidFastRenderView(this, gw);
        setContentView(renderView);

        // ***** INPUT (TOUCH) *****
        // Scale for input coordinates (screen to framebuffer)
        float scaleX = (float) Settings.fbufferWidth / metrics.widthPixels;
        float scaleY = (float) Settings.fbufferHeight / metrics.heightPixels;
        MultiTouchHandler touch = new MultiTouchHandler(renderView, scaleX, scaleY);
        // Setter needed due to cyclic dependency
        gw.setTouchHandler(touch);

        // ***** AUDIO *****
        //TODO va gestito in GameWorld
        Audio audio = new AndroidAudio(this);
        backgroundMusic = audio.newMusic("soundtrack.mp3");
        backgroundMusic.play();
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
