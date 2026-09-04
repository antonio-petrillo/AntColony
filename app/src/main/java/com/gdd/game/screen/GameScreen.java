package com.gdd.game.screen;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.RectF;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.Music;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.Assets;
import com.gdd.game.cards.Card;
import com.gdd.game.cards.CardController;
import com.gdd.game.cards.Hand;
import com.gdd.game.cards.TargetArrow;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.Game;
import com.gdd.game.GameWorld;
import com.gdd.game.Settings;
import com.gdd.game.ui.HorizontalGroup;
import com.gdd.game.ui.Image;
import com.gdd.game.ui.ImageButton;
import com.gdd.game.ui.Label;
import com.gdd.game.ui.Panel;
import com.gdd.game.ui.UIController;

/*
 * Schermata di gameplay.
 */
public class GameScreen extends Screen {

    public enum GameState { RUNNING, PAUSED }
    private GameState state;

    // Layers
    private UIController uiController;
    private CardController cardController;
    private GameWorld gw;


    private Label energyLabel, antsLabel;
    private float fbWidth, fbHeight;
    private Canvas canvas;
    private TouchHandler touchHandler;
    // UI
    private Panel rootPanel, pausePopup;

    private Music music;

    private TargetArrow arrow;
    private ImageButton drawButton;


    /*
    public final Box worldSize, // physics world's size (in meters)
            screenSize, // smartphone's screen size (in pixel)
            cameraView; // camera position and size (in meters)
    */

    private int cardCounter = 0; // TEST
    private Hand hand;

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

        // Build and apply UI
        buildRootPanel();
        buildPausePopup();
        uiController.reset();
        uiController.setRoot(rootPanel);
        uiController.updateLayout();

        // Music
        music = Assets.song;
        music.setLooping(true);
        music.setVolume(0.5f);
        music.play();

        // Cards
        buildHand();
        arrow = new TargetArrow(0xFFFFD54F, 3f);
        cardController = new CardController(hand, arrow, gw);
    }

    // ***************************************
    //  Game loop
    // ***************************************

    @SuppressLint("DefaultLocale")
    @Override
    public void update(float deltaTime) {

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            boolean consumed = uiController.processInput(event);

            if(!consumed && state == GameState.RUNNING) {
                consumed = cardController.processInput(event);

                if(!consumed)
                    gw.inputSystem.processInput(event);
            }
        }

        if(state == GameState.RUNNING) {
            cardController.update(deltaTime);
            gw.update(deltaTime);

            // Update UI
            energyLabel.setText(String.format("%d", gw.playerEnergy));
            antsLabel.setText(String.format("%d", gw.spawnsys.antCount));
        }
    }

    @Override
    public void render() {
        // clear the screen with white
        canvas.drawARGB(255, 200, 200, 200);
        // draw scene
        gw.render();
        cardController.draw(canvas);
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
    }

    @Override
    public void dispose() {
        music.dispose();
    }

    // ***************************************
    //  UI
    // ***************************************

    private void buildRootPanel() {

        rootPanel = new Panel(0, 0, fbWidth, fbHeight);

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

        // ***** DRAW BUTTON *****

        // TEST
        drawButton = new ImageButton(fbWidth - 300, fbHeight - 200, 150, 75);
        drawButton.setIdleBitmap(Assets.DRAW_BUTTON_IDLE);
        drawButton.setPressedBitmap(Assets.DRAW_BUTTON_PRESSED);
        drawButton.setDisabledBitmap(Assets.DRAW_BUTTON_DISABLED);
        rootPanel.addChild(drawButton);

        drawButton.setOnClickListener(b -> {
            Assets.click.play(1);

            // TEST
            cardCounter++;
            if(cardCounter % 2 == 0)
                hand.add(new Card(-1, Card.Type.ATTACK, Card.TargetType.WASP, Assets.CARD_ATTACK));
            else
                hand.add(new Card(-1, Card.Type.HEAL, Card.TargetType.ANT, Assets.CARD_HEAL));
        });

    }

    private void buildPausePopup() {

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
    //  Cards
    // ***************************************

    private void buildHand() {

        float cardPeek = 10f;
        float maxSpread = 200f;
        float areaWidthPadding = 40f;

        float areaHeight = 100f;
        float areaWidth = maxSpread + areaWidthPadding * 2f;

        RectF handArea = new RectF(
                fbWidth / 2f - areaWidth / 2f, // left
                fbHeight - areaHeight,             // top
                fbWidth / 2f + areaWidth / 2f,     // right
                fbHeight                           // bottom
        );

        hand = new Hand(handArea, cardPeek, maxSpread);
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
