package com.gdd.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.components.ComponentType;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.EntityTag;
import com.gdd.game.ecs.entities.Transform;
import com.gdd.game.ecs.factories.NestFactory;
import com.gdd.game.ecs.misc.Box;
import com.gdd.game.ecs.misc.Camera;
import com.gdd.game.ecs.misc.EntityContactListener;
import com.gdd.game.ecs.systems.AiSystem;
import com.gdd.game.ecs.systems.InputSystem;
import com.gdd.game.ecs.systems.RenderSystem;
import com.gdd.game.ecs.systems.SpawnSystem;
import com.gdd.game.ecs.systems.GarbageCollectSystem;
import com.gdd.game.ecs.systems.PerceptionSystem;
import com.gdd.game.screen.Screen;
import com.gdd.game.ui.TextButton;
import com.gdd.game.ui.UIController;
import com.gdd.game.ui.WidgetGroup;
import com.gdd.game.ui.Panel;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;
import com.google.fpl.liquidfun.Body;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/*
 * Gestore principale della scena e degli oggetti di gioco.
 */
public class GameWorld {

    public final Bitmap frameBuffer;
    public final Canvas canvas;

    // Controller
    public final InputSystem inputSystem;
    public Camera camera;

    // Physics Simulation
    public World world;
    public Entity nest;
    // Used to constraint the moving objects in the world, see `addWorldBoundaries`
    public Body worldBoundaries;
    public final Box worldSize, // physics world's size (in meters)
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
    public final PerceptionSystem perceptionsys;

    public List<Entity> entities = new ArrayList<>();

    private static final Random rng = new Random();
    private final float SPAWN_DIST = 1.0f;
    private Screen gameScreen;
    private TiledBackgroundRenderer background;


    /*
     * Constructor.
     */
    public GameWorld(Screen gameScreen, Bitmap frameBuffer, Box screenSize, Box worldSize) {

        this.gameScreen = gameScreen;

        touchHandler = gameScreen.game.getTouchHandler();
        this.frameBuffer = frameBuffer;
        this.worldSize = worldSize;
        this.world = new World(0, 0);  // gravity vector

        cameraView = new Box(worldSize); // di default vede l'intero mondo
        canvas = new Canvas(frameBuffer);

        // INIT NEST
        var nestPosition = new Vec2(0, 0);
        nest = NestFactory.makeNest(this, nestPosition);
        entities.add(nest);

        // SCENE
        camera = new Camera(cameraView,
                Settings.worldWidth, Settings.worldHeight, // worldWidth, worldHeight in metri
                Settings.fbufferWidth, Settings.fbufferHeight // pixel, fisso, lo conosci già
        );

        // SYSTEMS
        inputSystem = new InputSystem(this, camera);
        rsys = new RenderSystem(this, camera);
        spawnsys = new SpawnSystem(this, nest);
        wbsys = new GarbageCollectSystem(this, spawnsys);

        // stored to prevent GC
        entityContactListener = new EntityContactListener();
        world.setContactListener(entityContactListener);

        aisys = new AiSystem(this, nestPosition, 1.0f);
        perceptionsys = new PerceptionSystem(this);

        addWorldBoundaries();

        background = new TiledBackgroundRenderer(Assets.TERRAIN_BITMAP, 3f);
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

        // Handle collisions: advance the physics simulation
        world.step(deltaTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
        syncTransform(); // update transform components

        // Update Systems
        wbsys.update(entities, deltaTime);
        perceptionsys.update(entities, deltaTime);
        spawnsys.update(entities, deltaTime);
        aisys.update(entities, deltaTime);
    }


    public synchronized void render()
    {
        background.draw(canvas, camera);
        rsys.update(entities, 0f);
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

        worldBoundaries = world.createBody(bdef);
        worldBoundaries.setSleepingAllowed(false);

        PolygonShape shape = new PolygonShape();

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setDensity(0.f);
        fdef.setFriction(0.f);
        fdef.setRestitution(0.0f);

        // top
        shape.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymin, 0);
        worldBoundaries.createFixture(fdef);
        // bottom
        shape.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymax, 0);
        worldBoundaries.createFixture(fdef);
        // left
        shape.setAsBox(THICKNESS, ymax-ymin, xmin, ymin+(ymax-ymin)/2, 0);
        worldBoundaries.createFixture(fdef);
        // right
        shape.setAsBox(THICKNESS, ymax-ymin, xmax, ymin+(ymax-ymin)/2, 0);
        worldBoundaries.createFixture(fdef);

        worldBoundaries.setUserData(new Entity(EntityTag.WALL));

        // clean up native objects
        bdef.delete();
        shape.delete();
        fdef.delete();
    }

    // TEST METHOD
    public Entity hit(float worldX, float worldY) {
        int n = entities.size();
        for(int i=0; i<n; i++) {

            Entity entity = entities.get(i);
            Transform t = entity.transform;

            if(worldX >= t.x - t.halfWidth && worldX <= t.x + t.halfWidth &&
                    worldY >= t.y - t.halfHeight && worldY <= t.y + t.halfHeight) {
                return entity;
            }
        }
        return null;
    }
}
