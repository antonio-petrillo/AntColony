package com.gdd.game.screen;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.Assets;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.Game;
import com.gdd.game.GameWorld;
import com.gdd.game.Settings;
import com.gdd.game.ui.HorizontalGroup;
import com.gdd.game.ui.Image;
import com.gdd.game.ui.ImageButton;
import com.gdd.game.ui.Label;
import com.gdd.game.ui.Panel;
import com.gdd.game.ui.TextButton;
import com.gdd.game.ui.UIController;

/*
 * Schermata di gameplay.
 */
public class GameScreen extends Screen {

    public enum GameState { RUNNING, PAUSED }
    private GameState state;

    private GameWorld gw;
    private Label energyLabel, antsLabel;
    private UIController uiController;
    private float fbWidth, fbHeight;
    private Canvas canvas;
    private TouchHandler touchHandler;
    private boolean consumed;

    // UI
    private Panel rootPanel, pausePopup;

    private Music music;

    /*
    public final Box worldSize, // physics world's size (in meters)
            screenSize, // smartphone's screen size (in pixel)
            cameraView; // camera position and size (in meters)
    */

    public GameScreen(Game game) {
        super(game);

        state = GameState.RUNNING;

        canvas = new Canvas(game.getFramebuffer());

        fbWidth = game.getFramebuffer().getWidth();
        fbHeight = game.getFramebuffer().getHeight();

        uiController = game.getUiController();

        touchHandler = game.getTouchHandler();

        // World: physical simulation
        float halfWorldWidth = Settings.worldWidth / 2;
        float halfWorldHeight = Settings.worldHeight / 2;
        Box worldSize = new Box(-halfWorldWidth, -halfWorldHeight,
                halfWorldWidth, halfWorldHeight);

        gw = new GameWorld(this, game.getFramebuffer(), game.getScreensize(), worldSize);
        gw.setTouchHandler(game.getTouchHandler());

        initUI();

        // Music
        music = Assets.song;
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();
    }

    // ***************************************
    //  Game loop
    // ***************************************

    @SuppressLint("DefaultLocale")
    @Override
    public void update(float deltaTime) {

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            consumed = uiController.processInput(event);
            if(!consumed && state == GameState.RUNNING)
                gw.inputSystem.processInput(event);
        }

        if(state == GameState.RUNNING) {
            gw.update(deltaTime);
        }

        // Update UI
        energyLabel.setText(String.format("%d", gw.playerEnergy));
        antsLabel.setText(String.format("%d", gw.spawnsys.antCount));
    }

    @Override
    public void render() {
        // clear the screen with white
        canvas.drawARGB(255, 200, 200, 200);
        // draw entities
        gw.render();
        // draw widgets
        uiController.draw(canvas);
    }

    // ***************************************
    //  Android callbacks
    // ***************************************

    @Override
    public void pause() {
        music.pause();
        setGameState(GameState.PAUSED);
    }

    @Override
    public void resume() {
        music.play();
        setGameState(GameState.RUNNING);
    }

    @Override
    public void dispose() {
        music.dispose();
    }

    // ***************************************
    //  UI
    // ***************************************

    private void initUI() {

        uiController.reset();

        rootPanel = new Panel(0, 0, fbWidth, fbHeight);
        uiController.setRoot(rootPanel);

        // ***** HUD BADGES *****

        float badgeWidth = 130;
        float badgeHeight = 75;

        HorizontalGroup badgeGroup = new HorizontalGroup(20, 20, 50 + badgeWidth*2, badgeHeight);
        rootPanel.addChild(badgeGroup);

        antsLabel = new Label(30, 20, badgeWidth, badgeHeight);
        antsLabel.setText("0");
        antsLabel.setTextColor(0xFF000000);
        antsLabel.setTextAlignment(Label.HAlign.RIGHT, Label.VAlign.CENTER, 20, 0);
        antsLabel.setBackgroundBitmap(Assets.HUD_BADGE_ANT_BITMAP);
        antsLabel.setBackgroundMode(Label.BackgroundMode.BITMAP);
        badgeGroup.addChild(antsLabel);

        energyLabel = new Label(50 + badgeWidth, 20, badgeWidth, badgeHeight);
        energyLabel.setText("0");
        energyLabel.setTextColor(0xFF000000);
        energyLabel.setTextAlignment(Label.HAlign.RIGHT, Label.VAlign.CENTER, 20, 0);
        energyLabel.setBackgroundBitmap(Assets.HUD_BADGE_SUGAR_PRESSED);
        energyLabel.setBackgroundMode(Label.BackgroundMode.BITMAP);
        badgeGroup.addChild(energyLabel);

        // ***** PAUSE BUTTON *****

        ImageButton pauseButton = new ImageButton(fbWidth - 95, 20, 75, 75);
        pauseButton.setIdleBitmap(Assets.PAUSEBUTTON_IDLE_BITMAP);
        pauseButton.setPressedBitmap(Assets.PAUSEBUTTON_PRESSED);
        rootPanel.addChild(pauseButton);

        pauseButton.setOnClickListener(b -> {
            Assets.click.play(1);
            setGameState(GameState.PAUSED);
        });

        initBottomUI();
        initPausePopup();

        uiController.updateLayout();
    }

    // TEST
    // TODO: da gestire in modo opportuno
    private void initBottomUI() {

        float bottomHeight = fbHeight/5;

        Panel bottomPanel = new Panel(0, fbHeight - bottomHeight, fbWidth, bottomHeight);
        rootPanel.addChild(bottomPanel);

        HorizontalGroup bottomHorizontal = new HorizontalGroup(0, 0, 500, bottomHeight);
        bottomPanel.addChild(bottomHorizontal);

        // ***** CARD BUTTONS *****

        TextButton healButton = new TextButton(0, 0, 100, 100);
        healButton.setText("HEAL");
        bottomHorizontal.addChild(healButton);

        healButton.setOnClickListener(b -> {
            gw.addCardArea();
        });

        TextButton attackButton = new TextButton(0, 0, 100, 100);
        attackButton.setText("ATTACK");
        bottomHorizontal.addChild(attackButton);

        attackButton.setOnClickListener(b -> {
            gw.addCardArea();
        });

        // ***** CONFIRM/CANCEL BUTTONS *****

        TextButton confirmButton = new TextButton(0, 0, 100, 100);
        confirmButton.setText("Y");
        confirmButton.setColor(Color.GREEN);
        bottomHorizontal.addChild(confirmButton);

        confirmButton.setOnClickListener(b -> {
            // TODO: query spaziale + attivare la carta
            gw.removeCardArea();
        });

        TextButton cancelButton = new TextButton(0, 0, 100, 100);
        cancelButton.setText("X");
        cancelButton.setColor(Color.RED);
        bottomHorizontal.addChild(cancelButton);

        cancelButton.setOnClickListener(b -> {
            gw.removeCardArea();
        });
    }

    private void initPausePopup() {

        float popupWidth = fbWidth * 0.25f;
        float popupHeight = fbHeight * 0.75f;
        float popupX = (fbWidth - popupWidth) / 2;
        float popupY = (fbHeight - popupHeight) / 2;

        pausePopup = new Panel(popupX, popupY, popupWidth, popupHeight);
        pausePopup.setBorder(true, 0xB42D1C0E);

        // ***** WIDGETS *****

        Image pauseImage = new Image(0, 0, popupWidth, 100);
        pauseImage.setBitmap(Assets.TITLE_PAUSEMENU);
        pausePopup.addChild(pauseImage);

        ImageButton resumeButton = new ImageButton(popupWidth/4, popupHeight/4, popupWidth/2, 100);
        resumeButton.setIdleBitmap(Assets.CONTINUEBUTTON_IDLE);
        resumeButton.setPressedBitmap(Assets.CONTINUEBUTTON_PRESSED);
        pausePopup.addChild(resumeButton);

        // ***** ON_CLICK METHODS *****

        resumeButton.setOnClickListener(b -> {
            Assets.click.play(1);
            setGameState(GameState.RUNNING);
        });
    }

    // ***************************************
    //  SCREEN
    // ***************************************

    private void setGameState(GameState newState) {
        if (state == newState) return;

        state = newState;
        gw.inputSystem.reset();

        switch (newState) {
            case RUNNING:
                uiController.hideTopPopup();
                break;

            case PAUSED:
                uiController.showPopup(pausePopup);
                break;
        }
    }

}
