package com.gdd.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.factories.AntFactory;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.factories.NestFactory;
import com.gdd.game.ecs.factories.WaspFactory;
import com.gdd.game.ecs.misc.EntityContactListener;
import com.gdd.game.ecs.systems.AiSystem;
import com.gdd.game.ecs.systems.InputSystem;
import com.gdd.game.ecs.systems.RenderSystem;
import com.gdd.game.ecs.systems.SpawnSystem;
import com.gdd.game.ecs.systems.GarbageCollectSystem;
import com.gdd.game.ui.Button;
import com.gdd.game.ui.UIController;
import com.gdd.game.ui.WidgetGroup;
import com.gdd.game.ui.WidgetGroupImp;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {

    enum State { RUNNING, PAUSE }

    State state = State.RUNNING;
    public final Activity activity;

    // Rendering
    public static final int fbufferWidth = Settings.fbufferWidth,
            fbufferHeight = Settings.fbufferHeight;
    public Bitmap frameBuffer;
    public final Canvas canvas;

    // Controller
    private final UIController uiController;
    private InputSystem inputSystem;
    public Camera camera;

    // Physics Simulation
    public World world;
    public final Box worldSize, // physics world's size (in meters)
            screenSize, // smartphone's screen size (in pixel)
            cameraView; // camera position and size (in meters)
    private final EntityContactListener entityContactListener;

    // Input
    private TouchHandler touchHandler;

    // Particles
    public ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;

    // Parameters for world simulation
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    // Systems (ECS)
    public final RenderSystem rsys;
    public final GarbageCollectSystem wbsys;
    public final AiSystem aisys;
    public final SpawnSystem spawnsys;

    public List<Entity> entities = new ArrayList<>();

    private static final Random rng = new Random();
    private final float SPAWN_DIST = 1.0f;
    boolean consumed;


    /*
     * Constructor.
     */
    public GameWorld(Activity activity, Bitmap frameBuffer, Box worldSize, Box screenSize) {

        this.worldSize = worldSize;
        this.screenSize = screenSize;
        this.activity = activity;
        this.frameBuffer = frameBuffer;
        this.world = new World(0, 0);  // gravity vector

        cameraView = new Box(worldSize); // di default vede l'intero mondo
        canvas = new Canvas(frameBuffer);

        // SCENE
        camera = new Camera(cameraView,
                Settings.worldWidth, Settings.worldHeight, // worldWidth, worldHeight in metri
                Settings.fbufferWidth, Settings.fbufferHeight // pixel, fisso, lo conosci già
        );

        // SYSTEMS
        inputSystem = new InputSystem(camera);
        rsys = new RenderSystem(this, camera);
        wbsys = new GarbageCollectSystem(this);
        spawnsys = new SpawnSystem(this);

        // stored to prevent GC
        entityContactListener = new EntityContactListener();
        world.setContactListener(entityContactListener);

        var nestPosition = new Vec2(0, 0);
        var nest = NestFactory.makeNest(this, nestPosition);
        entities.add(nest);

        aisys = new AiSystem(this, nestPosition, 1.0f);

        initGameObjects();
        addWorldBoundaries();

        // UI
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
            state = State.PAUSE;
        });

        resumeButton.setOnClickListener(b -> {
            uiController.hideTopPopup();
            state = State.RUNNING;
        });
    }


    public void initGameObjects() {

        // spawn ants
        for (int i = 0; i < 100; i++) {
            float angle = rng.nextFloat(360.0f);
            float x = (float) Math.cos(angle) * SPAWN_DIST;
            float y = (float) Math.sin(angle) * SPAWN_DIST;

            var ant = AntFactory.makeAnt(this, x, y, angle);
            entities.add(ant);
        }

        // spawn wasps around the edges
        for (int i = 0; i < 5; i++) {
            float angle = rng.nextFloat(360.0f);
            float dist  = 4.0f ; // spawn far from nest
            float x = (float) Math.cos(angle) * dist;
            float y = (float) Math.sin(angle) * dist;
            entities.add(WaspFactory.makeWasp(this, x, y, dist));
        }
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
                 inputSystem.processInput(event);
        }

        if(state == State.RUNNING) {
            // Handle collisions: advance the physics simulation
            world.step(deltaTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
            syncTransform(); // update transform components

            // Update Systems
            wbsys.update(entities, deltaTime);
            spawnsys.update(entities, deltaTime);
            aisys.update(entities, deltaTime);
        }
    }


    public synchronized void render(float deltaTime)
    {
        // background (clear the screen with black)
        canvas.drawARGB(255, 0, 0, 0);
        // entities
        rsys.update(entities, deltaTime);
        // ui
        uiController.draw(canvas);
    }

    public synchronized void setGravity(float x, float y)
    {
        world.setGravity(x, y);
    }


    @Override
    protected void finalize() throws Throwable
    {
        try {
            world.delete();
        } finally {
            super.finalize();
        }
    }

    private void syncTransform() {

        int n = entities.size();
        for(int i=0; i<n; i++)  {

            Entity e = entities.get(i);
            PhysicComponent pc = (PhysicComponent) e.getComponent(ComponentType.PHYSIC);
            if(pc != null) {
                pc.syncTransform();
            }
        }
    }

    private void addWorldBoundaries() {

        float THICKNESS = 1f;
        float xmax = worldSize.width / 2;
        float xmin = -xmax;
        float ymax = worldSize.height / 2;
        float ymin = -ymax;

        BodyDef bdef = new BodyDef();

        var body = world.createBody(bdef);
        body.setSleepingAllowed(false);

        PolygonShape shape = new PolygonShape();

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setDensity(0.f);
        fdef.setFriction(0.f);
        fdef.setRestitution(0.8f);

        // top
        shape.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymin, 0);
        body.createFixture(fdef);
        // bottom
        shape.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymax, 0);
        body.createFixture(fdef);
        // left
        shape.setAsBox(THICKNESS, ymax-ymin, xmin, ymin+(ymax-ymin)/2, 0);
        body.createFixture(fdef);
        // right
        shape.setAsBox(THICKNESS, ymax-ymin, xmax, ymin+(ymax-ymin)/2, 0);
        body.createFixture(fdef);

        // clean up native objects
        bdef.delete();
        shape.delete();
        fdef.delete();
    }

}
