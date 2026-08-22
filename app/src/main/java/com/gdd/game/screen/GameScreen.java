package com.gdd.game.screen;

import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.Game;
import com.gdd.game.GameWorld;
import com.gdd.game.Settings;
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
    private UIController uiController;
    private float fbufferWidth, fbufferHeight;
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

        fbufferWidth = game.getScreensize().width;
        fbufferHeight = game.getScreensize().height;

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

        gw.update(deltaTime);
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

        WidgetGroup root = new Panel(0, 0, fbufferWidth, fbufferHeight);
        TextButton pauseButton = new TextButton(25, 25, 100, 50);
        pauseButton.setText("PAUSE");
        root.addChild(pauseButton);

        WidgetGroup pauseLayout = new Panel(0, 0, fbufferWidth, fbufferHeight);
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
