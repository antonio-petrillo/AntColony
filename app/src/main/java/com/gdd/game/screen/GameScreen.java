package com.gdd.game.screen;

import android.annotation.SuppressLint;
import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.Assets;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.Game;
import com.gdd.game.GameWorld;
import com.gdd.game.Settings;
import com.gdd.game.ui.HorizontalGroup;
import com.gdd.game.ui.ImageButton;
import com.gdd.game.ui.Label;
import com.gdd.game.ui.Panel;
import com.gdd.game.ui.TextButton;
import com.gdd.game.ui.UIController;
import com.gdd.game.ui.WidgetGroup;

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
    private WidgetGroup pauseLayout;

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
        setGameState(GameState.PAUSED);
    }

    @Override
    public void resume() {
        setGameState(GameState.RUNNING);
    }

    @Override
    public void dispose() {

    }

    // ***************************************
    //  UI
    // ***************************************

    public void initUI() {

        uiController.reset();

        WidgetGroup root = new Panel(0, 0, fbWidth, fbHeight);
        uiController.setRoot(root);

        // ***** BADGES *****

        float badgeWidth = 130;
        float badgeHeight = 75;

        HorizontalGroup badgeGroup = new HorizontalGroup(20, 20, 50 + badgeWidth*2, badgeHeight);
        root.addChild(badgeGroup);

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

        // ***** PAUSE BUTTON + POPUP *****

        ImageButton pauseButton = new ImageButton(fbWidth - 95, 20, 75, 75);
        pauseButton.setIdleBitmap(Assets.PAUSEBUTTON_IDLE_BITMAP);
        pauseButton.setPressedBitmap(Assets.PAUSEBUTTON_PRESSED);
        root.addChild(pauseButton);

        pauseLayout = new Panel(0, 0, fbWidth, fbHeight);
        TextButton resumeButton = new TextButton(500, 500, 200, 100);
        resumeButton.setText("RESUME");
        pauseLayout.addChild(resumeButton);

        // ***** ON_CLICK METHODS *****

        pauseButton.setOnClickListener(b -> {
            setGameState(GameState.PAUSED);
        });

        resumeButton.setOnClickListener(b -> {
            setGameState(GameState.RUNNING);
        });

        uiController.updateLayout();
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
                uiController.showPopup(pauseLayout);
                break;
        }
    }

}
