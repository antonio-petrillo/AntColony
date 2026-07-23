package com.gdd.game.engine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;
import com.gdd.game.Game;
import com.gdd.game.Settings;
import com.gdd.game.engine.components.ComponentType;
import com.gdd.game.engine.components.PhysicsComponent;
import com.gdd.game.engine.components.PrimitiveDrawable;
import com.gdd.game.engine.core.Actor;
import com.gdd.game.engine.core.Shape;
import com.gdd.game.engine.core.Transform;
import com.gdd.game.engine.managers.AudioManager;
import com.gdd.game.engine.managers.InputManager;
import com.gdd.game.engine.managers.PhysicsManager;
import com.gdd.game.engine.managers.RenderManager;
import com.google.fpl.liquidfun.BodyType;

import java.util.ArrayList;
import java.util.List;

public class SceneController {

    private Game game;
    private Camera camera;

    private InputManager sInput;
    private RenderManager sGraphics;
    private AudioManager sAudio;
    private PhysicsManager sPhysics;

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

        sInput = new InputManager(camera);
        sGraphics = new RenderManager();
        sAudio = new AudioManager();
        sPhysics = new PhysicsManager(Settings.worldWidth, Settings.worldHeight);
        sPhysics.setGravity(0, 5);

        // TEST
        actors = new ArrayList<>();
        initPhysicsActors();
        initActors();
    }

    public void initActors() {

    }

    public void initPhysicsActors() {
        Actor a;
        float width, height, radius;
        PhysicsParams physicsParams = new PhysicsParams();
        physicsParams.bodyType = BodyType.dynamicBody;
        physicsParams.shapeType = PhysicsParams.ShapeType.BOX;

        // TEST: physic actors [box]
        width = 0.5f; height = 0.5f;
        for (int i = 0; i <= 5; i++) {

            a = new Actor();
            a.setShape(new Shape.Box(width, height));
            a.addComponent(new PrimitiveDrawable(
                    PrimitiveDrawable.Kind.BOX, Color.BLUE, true));
            physicsParams.x = 1.5f * i;
            physicsParams.y = -5f;
            physicsParams.direction = 15f * i;
            physicsParams.width = width;
            physicsParams.height = height;
            a.addComponent(sPhysics.createComponent(physicsParams));
            actors.add(a);
        }


        physicsParams.shapeType = PhysicsParams.ShapeType.CIRCLE;
        radius = 0.25f;
        // TEST: physic actors [box]
        for (int i = 5; i <= 10 ; i++) {

            a = new Actor();
            a.setShape(new Shape.Circle(radius));
            a.addComponent(new PrimitiveDrawable(
                    PrimitiveDrawable.Kind.CIRCLE, Color.RED, true));
            physicsParams.x = -1.5f * i;
            physicsParams.y = -5f;
            physicsParams.direction = 15f * i;
            physicsParams.radius = radius;
            a.addComponent(sPhysics.createComponent(physicsParams));
            actors.add(a);
        }
    }


    // ------------------------------------------------------------------
    // Game Loop
    // ------------------------------------------------------------------

    public synchronized void processInput(Input.TouchEvent event)  {
        sInput.processInput(event);
    }

    public synchronized void update(float deltaTime)  {

        // update physics
        sPhysics.step(deltaTime);
        // sync transform-physics (da spostare in physics?)
        for (Actor a : actors) {
            PhysicsComponent pc = (PhysicsComponent) a.getComponent(ComponentType.PHYSICS);
            Transform t = a.getTransform();
            if(pc != null) {
                t.x = pc.getX();
                t.y = pc.getY();
                t.angle = pc.getAngle();
            }
        }
    }


    public synchronized void render(Canvas canvas)
    {
        // draw actors
        sGraphics.render(canvas, camera, actors);
    }

}