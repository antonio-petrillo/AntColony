package com.gdd.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.entities.AntFactory;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.NestFactory;
import com.gdd.game.ecs.misc.EntityContactListener;
import com.gdd.game.ecs.systems.AiSystem;
import com.gdd.game.ecs.systems.RenderSystem;
import com.gdd.game.ecs.systems.SpawnSystem;
import com.gdd.game.ecs.systems.WorldBoundSystem;
import com.google.fpl.liquidfun.ContactListener;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {
    // Rendering
    public final static int bufferWidth = 400, bufferHeight = 600;    // actual pixels
    public Bitmap buffer;
    private final Canvas canvas;

    // Simulation
    public List<GameObject> objects;
    public World world;
    public final Box physicalSize, screenSize, currentView;
    private final TouchConsumer touchConsumer;
    private final EntityContactListener entityContactListener;
    private TouchHandler touchHandler;

    // Particles
    public ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;

    // Parameters for world simulation
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    public final Activity activity; // just for loading bitmaps in game objects

    public final RenderSystem rsys;
    public final WorldBoundSystem wbsys;
    public final AiSystem aisys;

    public final SpawnSystem spawnsys;

    public List<Entity> entities = new ArrayList<>();

    public GameWorld(Box physicalSize, Box screenSize, Activity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 0);  // gravity vector

        this.currentView = new Box(physicalSize);

        // stored to prevent GC
        touchConsumer = new TouchConsumer(this);
        entityContactListener = new EntityContactListener();

        this.world.setContactListener(entityContactListener);

        this.objects = new ArrayList<>();
        this.canvas = new Canvas(buffer);

        rsys = new RenderSystem(this);
        wbsys = new WorldBoundSystem(this);
        aisys = new AiSystem(this);
        spawnsys = new SpawnSystem(this);

        var nest = NestFactory.makeNest(this);
        entities.add(nest);

        // spawn ants
        for (int i = 0; i < 100; i++) {
            float angle = rng.nextFloat(360.0f);
            float x = (float) Math.cos(angle) * SPAWN_DIST;
            float y = (float) Math.sin(angle) * SPAWN_DIST;

            var ant = AntFactory.makeAnt(this, x, y, angle);
            entities.add(ant);
        }
    }

    private static final Random rng = new Random();
    private final float SPAWN_DIST = 1.0f;

    public synchronized void update(float elapsedTime)  {
        // advance the physics simulation
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

        // Handle collisions
        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents())
            touchConsumer.consumeTouchEvent(event);

        wbsys.update(entities, elapsedTime);
        spawnsys.update(entities, elapsedTime);
        aisys.update(entities, elapsedTime);
    }

    public synchronized void render()
    {
        // clear the screen (with black)
        canvas.drawARGB(255, 0, 0, 0);
        rsys.update(entities, 0.0f);
    }

    // Conversions between screen coordinates and physical coordinates

    public float toMetersX(float x) { return currentView.xmin + x * (currentView.width/screenSize.width); }
    public float toMetersY(float y) { return currentView.ymin + y * (currentView.height/screenSize.height); }

    public float toPixelsX(float x) { return (x-currentView.xmin)/currentView.width*bufferWidth; }
    public float toPixelsY(float y) { return (y-currentView.ymin)/currentView.height*bufferHeight; }

    public float toPixelsXLength(float x)
    {
        return x/currentView.width*bufferWidth;
    }
    public float toPixelsYLength(float y)
    {
        return y/currentView.height*bufferHeight;
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

    public void setTouchHandler(TouchHandler touchHandler) {
        this.touchHandler = touchHandler;
    }
}
