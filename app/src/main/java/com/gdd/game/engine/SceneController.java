package com.gdd.game.engine;

import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.gdd.game.Game;
import com.gdd.game.Settings;
import com.gdd.game.engine.components.RectDrawable;
import com.gdd.game.engine.components.TransformComponent;

import java.util.ArrayList;
import java.util.List;

public class SceneController {

    private Game game;
    private Camera camera;

    private SceneInput sInput;
    private SceneGraphics sGraphics;
    private SceneAudio sAudio;
    private ScenePhysics sPhysics;

    private List<Actor> actors;


    // ------------------------------------------------------------------
    // Init
    // ------------------------------------------------------------------

    public SceneController(Game game) {
        this.game = game;

        // SCENE
        camera = new Camera(game.cameraView,
                Settings.worldWidth, Settings.worldHeight, // worldWidth, worldHeight in metri
                Settings.fbufferWidth, Settings.fbufferHeight // pixel, fisso, lo conosci già
        );

        sInput = new SceneInput(camera);
        sGraphics = new SceneGraphics();
        sAudio = new SceneAudio();
        sPhysics = new ScenePhysics();

        actors = new ArrayList<>();
        initActors();
    }


    public void initActors() {
        Actor a;
        TransformComponent tc;

        // 1
        a = new Actor();
        a.addComponent( new RectDrawable() );
        tc = a.getTransformComponent();
        tc.angle = 90;
        actors.add(a);

        // 2
        a = new Actor();
        a.addComponent( new RectDrawable() );
        tc = a.getTransformComponent();
        tc.y = 5;
        tc.angle = 50;
        actors.add(a);
    }

    // ------------------------------------------------------------------
    // Game Loop
    // ------------------------------------------------------------------

    public synchronized void processInput(Input.TouchEvent event)  {
        sInput.processInput(event);
    }

    public synchronized void update(float deltaTime)  {

    }


    public synchronized void render(Canvas canvas)
    {
        // draw actors
        sGraphics.render(canvas, camera, actors);
    }


}
