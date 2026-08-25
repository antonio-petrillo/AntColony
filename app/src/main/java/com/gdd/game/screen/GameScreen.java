package com.gdd.game.screen;

import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.Assets;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.Game;
import com.gdd.game.GameWorld;
import com.gdd.game.Settings;
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


    public enum State { RUNNING, PAUSE }

    public State state;

    private GameWorld gw;
    private Label energyLabel;
    private UIController uiController;
    private float fbWidth, fbHeight;
    private Canvas canvas;
    private TouchHandler touchHandler;
    private boolean consumed;

    /*
    public final Box worldSize, // physics world's size (in meters)
            screenSize, // smartphone's screen size (in pixel)
            cameraView; // camera position and size (in meters)
    */

    public GameScreen(Game game) {
        super(game);

        state = State.RUNNING;

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

    @Override
    public void update(float deltaTime) {

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            consumed = uiController.processInput(event);
            if(!consumed && state == State.RUNNING)
                gw.inputSystem.processInput(event);
        }

        if(state == State.RUNNING) {
            gw.update(deltaTime);
        }
    }

    @Override
    public void render() {
        // clear the screen with white
        canvas.drawARGB(255, 200, 200, 200);
        // draw entities
        gw.render();


        energyLabel.setText(String.format("Energy: %d", gw.playerEnergy));

        // draw widgets
        uiController.draw(canvas);
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
    //  UI
    // ***************************************

    public void initUI() {

        uiController.reset();

        WidgetGroup root = new Panel(0, 0, fbWidth, fbHeight);
        ImageButton pauseButton = new ImageButton(fbWidth - 95, 25, 75, 75);
        pauseButton.setIdleImage(Assets.PAUSEBUTTON_IDLE_BITMAP);
        pauseButton.setPressedImage(Assets.PAUSEBUTTON_PRESSED);
        root.addChild(pauseButton);

        energyLabel = new Label(100, 100, 100, 50);
        energyLabel.setText("Energy: 0");
        energyLabel.setTextColor(0xFF000000);
        root.addChild(energyLabel);

        WidgetGroup pauseLayout = new Panel(0, 0, fbWidth, fbHeight);
        TextButton resumeButton = new TextButton(500, 500, 200, 100);
        resumeButton.setText("RESUME");
        pauseLayout.addChild(resumeButton);

        pauseButton.setOnClickListener(b -> {
            gw.inputSystem.reset();
            uiController.showPopup(pauseLayout);
            state = State.PAUSE;
        });

        resumeButton.setOnClickListener(b -> {
            uiController.hideTopPopup();
            state = State.RUNNING;
        });

        uiController.setRoot(root);
        uiController.updateLayout();
    }
}
