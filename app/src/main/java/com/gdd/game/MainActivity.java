package com.gdd.game;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.MultiTouchHandler;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.screen.GameScreen;
import com.gdd.game.screen.Screen;

public class MainActivity extends Activity implements Game {

    private AndroidFastRenderView renderView;
    private MultiTouchHandler touchHandler;
    public Screen screen;
    public Box screenSize;
    public Bitmap frameBuffer;
    private Music backgroundMusic;

    // the tag used for logging
    public static String TAG;

    // ********************************
    //  Android callbacks
    // ********************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        System.loadLibrary("liquidfun");
        System.loadLibrary("liquidfun_jni");

        TAG = Assets.APPNAME;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        enableImmersiveMode();

        var manager = getAssets();
        Assets.load(manager);

        // ***** GAME *****
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenSize = new Box(0, 0, metrics.widthPixels, metrics.heightPixels);
        frameBuffer = Bitmap.createBitmap(Settings.fbufferWidth, Settings.fbufferHeight,
                Bitmap.Config.ARGB_8888);


        // ***** SURFACE VIEW *****
        renderView = new AndroidFastRenderView(this, frameBuffer);

        // ***** INPUT (TOUCH) *****
        // Scale for input coordinates (screen to framebuffer)
        float scaleX = (float) Settings.fbufferWidth / metrics.widthPixels;
        float scaleY = (float) Settings.fbufferHeight / metrics.heightPixels;
        touchHandler = new MultiTouchHandler(renderView, scaleX, scaleY);

        // ***** AUDIO *****
        //TODO va gestito in GameWorld
        /*
        Audio audio = new AndroidAudio(this);
        backgroundMusic = audio.newMusic("soundtrack.mp3");
        backgroundMusic.play();
        */

        screen = getStartScreen();
        setContentView(renderView);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("Main thread", "pause");
        renderView.pause(); // stops the main loop
        screen.pause();
        //backgroundMusic.pause();

        if (isFinishing())
            screen.dispose();
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
        screen.resume();
        renderView.resume(); // starts game loop in a separate thread
        //backgroundMusic.play();

        // persistence example
        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        int counter = pref.getInt("INFO", -1); // default value
        Log.i("Main thread", "read counter " + counter);
    }

    // ********************************
    //  Misc
    // ********************************

    private void enableImmersiveMode() {
        Window window = getWindow();

        // Consente al gioco di estendersi anche dietro il punch-hole
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }

        // Nasconde la barra di navigazione e la barra di stato
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(window, window.getDecorView());
        if (controller != null) {
            controller.hide(WindowInsetsCompat.Type.systemBars());
            controller.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            );
        }
    }

    // ********************************
    //  Methods for screen
    // ********************************

    @Override
    public void setScreen(Screen screen) {
        if (screen == null)
            throw new IllegalArgumentException("Screen must not be null");

        this.screen.pause();
        this.screen.dispose();
        screen.resume();
        screen.update(0);
        this.screen = screen;
    }

    public Screen getCurrentScreen() {
        return screen;
    }

    public Screen getStartScreen() {
        return new GameScreen(this);
    }

    public TouchHandler getTouchHandler() {
        return touchHandler;
    }

    public Bitmap getFramebuffer() {
        return frameBuffer;
    }

    public Box getScreensize() {
        return screenSize;
    }
}
