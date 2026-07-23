package com.gdd.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.engine.Box;
import com.gdd.game.engine.SceneController;
import com.gdd.game.ui.Button;
import com.gdd.game.ui.UIController;
import com.gdd.game.ui.WidgetGroup;
import com.gdd.game.ui.WidgetGroupImp;

public class Game {

    enum State { RUNNING, PAUSED }

    State state = State.RUNNING;
    public final Activity activity;

    // Rendering
    public static final int fbufferWidth = Settings.fbufferWidth,
            fbufferHeight = Settings.fbufferHeight;
    public Bitmap frameBuffer;
    public final Canvas canvas;

    // Controller
    private final UIController uiController;
    private SceneController sceneController;

    public final Box worldSize, // physics world's size (in meters)
            screenSize, // smartphone's screen size (in pixel)
            cameraView; // camera position and size (in meters)

    // Input
    private TouchHandler touchHandler;
    boolean consumed;


    /*
     * Constructor.
     */
    public Game(Activity activity, Bitmap frameBuffer, Box worldSize, Box screenSize) {

        this.worldSize = worldSize;
        this.screenSize = screenSize;
        this.activity = activity;
        this.frameBuffer = frameBuffer;

        cameraView = new Box(worldSize); // di default vede l'intero mondo
        canvas = new Canvas(frameBuffer);

        sceneController = new SceneController(this);

        uiController = new UIController();
        initUI();
    }


    // ------------------------------------------------------------------
    // Initialize
    // ------------------------------------------------------------------

    public void initUI() {

        WidgetGroup mainLayout = new WidgetGroupImp(0, 0, fbufferWidth, fbufferHeight);
        Button pauseButton = new Button(50, 50, 200, 100, "PAUSE");
        mainLayout.addWidget(pauseButton);
        uiController.setMainLayout(mainLayout);

        WidgetGroup pauseLayout = new WidgetGroupImp(0, 0, fbufferWidth, fbufferHeight);
        Button resumeButton = new Button(500, 500, 200, 100, "RESUME");
        pauseLayout.addWidget(resumeButton);

        pauseButton.setOnClickListener(b -> {
            uiController.showPopup(pauseLayout);
            state = State.PAUSED;
        });

        resumeButton.setOnClickListener(b -> {
            uiController.hideTopPopup();
            state = State.RUNNING;
        });
    }


    // ------------------------------------------------------------------
    // Getter / Setter
    // ------------------------------------------------------------------

    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }


    // ------------------------------------------------------------------
    // Game Loop
    // ------------------------------------------------------------------

    public synchronized void update(float deltaTime)  {

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            consumed = uiController.processInput(event);
            if(!consumed && state == State.RUNNING)
                sceneController.processInput(event);
        }

        // update scene state
        if(state == State.RUNNING) {
            sceneController.update(deltaTime);
        }
    }


    public synchronized void render()
    {
        // clear the screen with white
        canvas.drawARGB(255, 255, 255, 255);
        // render scene
        sceneController.render(canvas);
        // render ui
        uiController.draw(canvas);
    }


    /*
    @Override
    protected void finalize() throws Throwable
    {
        try {
            world.delete();
        } finally {
            super.finalize();
        }
    }
    */
}