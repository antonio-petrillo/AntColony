package com.gdd.game;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

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
import com.gdd.game.ui.UIElement;
import com.gdd.game.ui.UIManager;
import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameWorld {

    // Rendering
    public final static int bufferWidth = 400, bufferHeight = 600; // actual pixels
    public Bitmap buffer;
    private final Canvas canvas;
    private final Paint paint;
    private final UIManager uimanager = new UIManager();;

    // Simulation
    public List<GameObject> objects;
    public World world;
    public final Box physicalSize, screenSize, currentView, maxView;
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
    public final GarbageCollectSystem wbsys;
    public final AiSystem aisys;

    public final SpawnSystem spawnsys;

    public List<Entity> entities = new ArrayList<>();

    public GameWorld(Box physicalSize, Box screenSize, Activity theActivity) {
        this.physicalSize = physicalSize;
        this.screenSize = screenSize;
        this.activity = theActivity;
        this.buffer = Bitmap.createBitmap(bufferWidth, bufferHeight, Bitmap.Config.ARGB_8888);
        this.world = new World(0, 0);  // gravity vector

        this.maxView = new Box(physicalSize);
        this.currentView = new Box(physicalSize);

        // ***** UI *****
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.FILL);
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

    private static final Random rng = new Random();
    private final float SPAWN_DIST = 1.0f;
    boolean consumed;

    public synchronized void update(float elapsedTime)  {

        // advance the physics simulation
        world.step(elapsedTime, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);

        // Handle collisions
        //for (Input.TouchEvent event: touchHandler.getTouchEvents())
        //    touchConsumer.consumeTouchEvent(event);

        // Handle touch events
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            consumed = uimanager.handleInput(event);
        }

        wbsys.update(entities, elapsedTime);
        spawnsys.update(entities, elapsedTime);
        aisys.update(entities, elapsedTime);
    }

    public synchronized void render()
    {
        // clear the screen (with black)
        canvas.drawARGB(255, 0, 0, 0);
        rsys.update(entities, 0.0f);
        uimanager.draw(canvas, paint);
    }

    public void initUI() {
        UIButton button;

        button = new UIButton(50, 420, 50, 50);
        button.setBitmap(Assets.BUTTON_UP);
        button.setOnClickListener(btn -> {

        });
        uimanager.add(button);

        button = new UIButton(50, 530, 50, 50);
        button.setBitmap(Assets.BUTTON_DOWN);
        button.setOnClickListener(btn -> {

        });
        uimanager.add(button);

        button = new UIButton(10, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_LEFT);
        button.setOnClickListener(btn -> {

        });
        uimanager.add(button);

        button = new UIButton(90, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_RIGHT);
        button.setOnClickListener(btn -> {

        });
        uimanager.add(button);

        button = new UIButton(270, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_PLUS);
        button.setOnClickListener(btn -> {
            zoomIn();
        });
        uimanager.add(button);

        button = new UIButton(345, 475, 50, 50);
        button.setBitmap(Assets.BUTTON_MINUS);
        button.setOnClickListener(btn -> {
            zoomOut();
        });
        uimanager.add(button);
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




    public void zoomIn() {
        currentView.width -= 2;
        currentView.height -= 2;

        // Lower limit
        if(currentView.width < 5)
            currentView.width = 5;
        if(currentView.height < 5)
            currentView.height = 5;
    }

    public void zoomOut() {
        currentView.width += 2;
        currentView.height += 2;

        // Upper limit
        if(currentView.width > maxView.width)
            currentView.width = maxView.width;
        if(currentView.height > maxView.height)
            currentView.height = maxView.height;
    }
}
