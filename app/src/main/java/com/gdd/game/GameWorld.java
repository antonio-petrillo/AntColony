package com.gdd.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.ecs.entities.AntFactory;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.NestFactory;
import com.gdd.game.ecs.entities.WaspFactory;
import com.gdd.game.ecs.misc.EntityContactListener;
import com.gdd.game.ecs.systems.AiSystem;
import com.gdd.game.ecs.systems.RenderSystem;
import com.gdd.game.ecs.systems.SpawnSystem;
import com.gdd.game.ecs.systems.GarbageCollectSystem;
import com.gdd.game.ui.UIButton;
import com.gdd.game.ui.UIManager;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {

    public final Activity activity; // just for loading bitmaps in game objects

    // Rendering
    public final static int bufferWidth = 400, bufferHeight = 600; // actual pixels
    public Bitmap buffer;
    private final Canvas canvas;

    // UI
    private final Paint uiPaint;
    private final UIManager uiManager;

    // Physics Simulation
    public List<GameObject> objects;
    public World world;
    public final Box physicalSize, // physics world: x[-10,+10] y[-15,+15] (meters)
            screenSize, // smartphone screen size (pixel)
            currentView; // camera in the physics world (meters)
    private final TouchConsumer touchConsumer;
    private final EntityContactListener entityContactListener;
    private TouchHandler touchHandler;
    private CameraHandler cameraHandler;

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


    public GameWorld(Box physicalSize, Box screenSize, Activity theActivity) {

        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 0);  // gravity vector

        this.currentView = new Box(physicalSize);
        cameraHandler = new CameraHandler(currentView);

        // UI
        uiPaint = new Paint();
        uiPaint.setColor(Color.YELLOW);
        uiPaint.setStyle(Paint.Style.FILL);
        uiManager = new UIManager();
        initUI();

        // stored to prevent GC
        touchConsumer = new TouchConsumer(this);
        entityContactListener = new EntityContactListener();

        this.world.setContactListener(entityContactListener);

        this.objects = new ArrayList<>();
        this.canvas = new Canvas(buffer);

        var nestPosition = new Vec2(0, 0);
        var nest = NestFactory.makeNest(this, nestPosition);
        entities.add(nest);

        rsys = new RenderSystem(this);
        wbsys = new GarbageCollectSystem(this);
        aisys = new AiSystem(this, nestPosition, 1.0f);
        spawnsys = new SpawnSystem(this);

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

    public void initUI() {

        float scrollx = 1f, scrolly = 1f;
        float zoom = 0.2f;

        UIButton button;

        button = new UIButton(50, 420, 50, 50);
        button.setBitmap(Assets.BUTTON_UP);
        button.setOnClickListener(btn -> {
            cameraHandler.scroll(0, -scrolly);
        });
        uiManager.add(button);

        button = new UIButton(50, 530, 50, 50);
        button.setBitmap(Assets.BUTTON_DOWN);
        button.setOnClickListener(btn -> {
            cameraHandler.scroll(0, scrolly);
        });
        uiManager.add(button);

        button = new UIButton(10, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_LEFT);
        button.setOnClickListener(btn -> {
            cameraHandler.scroll(-scrollx, 0);
        });
        uiManager.add(button);

        button = new UIButton(90, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_RIGHT);
        button.setOnClickListener(btn -> {
            cameraHandler.scroll(scrollx, 0);
        });
        uiManager.add(button);

        button = new UIButton(270, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_PLUS);
        button.setOnClickListener(btn -> {
            cameraHandler.zoom(zoom);
        });
        uiManager.add(button);

        button = new UIButton(345, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_MINUS);
        button.setOnClickListener(btn -> {
            cameraHandler.zoom(-zoom);
        });
        uiManager.add(button);
    }

    public synchronized void update(float elapsedTime)  {

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            consumed = uiManager.handleInput(event);

            // if(!consumed)
            // eventualmente si può passare il controllo alla scena fisica
            // touchConsumer.consumeTouchEvent(event);
        }

        // Handle collisions: advance the physics simulation
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

        // Update Systems
        wbsys.update(entities, elapsedTime);
        spawnsys.update(entities, elapsedTime);
        aisys.update(entities, elapsedTime);
    }

    public synchronized void render()
    {
        // background (clear the screen with black)
        canvas.drawARGB(255, 0, 0, 0);

        // scene
        rsys.update(entities, 0.0f);

        // ui
        uiManager.draw(canvas, uiPaint);
    }

    // Conversions between screen coordinates and physical coordinates

    /*
    // Old version: convert screen coordinates to physics world
    public float toMetersX(float x) { return currentView.xmin + x * (currentView.width/screenSize.width); }
    public float toMetersY(float y) { return currentView.ymin + y * (currentView.height/screenSize.height); }
    */

    // New version: convert framebuffer coordinates to physics world
    public float toMetersX(float x) { return currentView.xmin + x * (currentView.width/bufferWidth); }
    public float toMetersY(float y) { return currentView.ymin + y * (currentView.height/bufferHeight); }

    public float toPixelsX(float x) { return (x-currentView.xmin)/currentView.width*bufferWidth; }
    public float toPixelsY(float y) { return (y-currentView.ymin)/currentView.height*bufferHeight; }

    public float toPixelsXLength(float x) { return x/currentView.width*bufferWidth; }
    public float toPixelsYLength(float y) { return y/currentView.height*bufferHeight; }

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
