package com.gdd.game;

import android.graphics.Bitmap;

import com.badlogic.androidgames.framework.Audio;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.screen.Screen;
import com.gdd.game.ui.UIController;

public interface Game {

    // ***************************************
    //  BAG services
    // ***************************************

    public Audio getAudio();

    public void setScreen(Screen screen);

    public Screen getCurrentScreen();

    public Screen getStartScreen();

    // ***************************************
    //  AntColony services
    // ***************************************

    public TouchHandler getTouchHandler();

    public Bitmap getFramebuffer();

    public Box getScreensize();

    public UIController getUiController();
}
