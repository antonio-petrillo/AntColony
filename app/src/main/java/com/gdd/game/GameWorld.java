package com.gdd.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.ParticleSystemDef;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;

/**
 * The game objects and the viewport.
 *
 * Created by mfaella on 27/02/16.
 */
public class GameWorld {
    // Rendering
    final static int bufferWidth = 400, bufferHeight = 600;    // actual pixels
    Bitmap buffer;
    private final Canvas canvas;

    // Simulation
    List<GameObject> objects;
    World world;
    final Box physicalSize, screenSize, currentView;
    private final TouchConsumer touchConsumer;
    private TouchHandler touchHandler;

    // Particles
    ParticleSystem particleSystem;
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;

    // Parameters for world simulation
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    final Activity activity; // just for loading bitmaps in game objects

    // Arguments are in physical simulation units.
    public GameWorld(Box physicalSize, Box screenSize, Activity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 0);  // gravity vector

        this.currentView = physicalSize;
        // Start with half the world
        // new Box(physicalSize.xmin, physicalSize.ymin, physicalSize.xmax, physicalSize.ymin + physicalSize.height/2);

        // The particle system
        ParticleSystemDef psysdef = new ParticleSystemDef();
        this.particleSystem = world.createParticleSystem(psysdef);
        particleSystem.setRadius(PARTICLE_RADIUS);
        particleSystem.setMaxParticleCount(MAXPARTICLECOUNT);
        psysdef.delete();

        // stored to prevent GC
        touchConsumer = new TouchConsumer(this);

        this.objects = new ArrayList<>();
        this.canvas = new Canvas(buffer);
    }


    public synchronized GameObject addGameObject(GameObject obj)
    {
        objects.add(obj);
        return obj;
    }

    public synchronized void addParticleGroup(GameObject obj)
    {
        objects.add(obj);
    }
    private float spawnAccum = 0.0f;
    private static final float SPAWN_INTERVAL = 1.0f;
    public synchronized void update(float elapsedTime)  {
        // advance the physics simulation
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

        // Handle collisions
        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents())
            touchConsumer.consumeTouchEvent(event);

        spawnAccum += elapsedTime;

        var iter = objects.iterator();
        Nest nest = null;
        while (iter.hasNext()) {
            var obj = iter.next();
            float x = obj.body.getPositionX();
            float y = obj.body.getPositionY();
            if (x < physicalSize.xmin || x > physicalSize.xmax || y < physicalSize.ymin || y > physicalSize.ymax) {
                iter.remove();
            }

            if (spawnAccum >= SPAWN_INTERVAL && obj instanceof Nest theNest) {
                nest = theNest;
            }
        }

        if (spawnAccum >= SPAWN_INTERVAL) {
            spawnAccum = 0.0f;
            if (nest != null)
                for (int i = 0; i < 5; i++)
                    nest.spawn();
        }
    }

    public synchronized void render()
    {
        // clear the screen (with black)
        canvas.drawARGB(255, 0, 0, 0);
        for (GameObject obj: objects)
            obj.draw(buffer);
        // drawParticles();
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
