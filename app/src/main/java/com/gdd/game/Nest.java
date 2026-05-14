package com.gdd.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// TODO: by default in the center of the screen
public class Nest extends GameObject {

    private static final float THICKNESS = 1;
    private final Canvas canvas;
    private final Paint paint = new Paint();
    private final float HALF_WIDTH = 0.5f, HALF_HEIGHT = 0.5f;
    private final float SPAWN_DIST = 1.0f;
    private final Random rng = new Random();

    private final float xmin, ymin, xmax, ymax;

    public List<Ant> ants = new ArrayList<>(1024);
    public Nest(GameWorld gw) {
        super(gw);
        name = "NEST";

        canvas = new Canvas(gw.buffer);
        paint.setARGB(255, 0, 0, 255);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);

        xmin = gw.toPixelsX(-HALF_WIDTH);
        xmax = gw.toPixelsX(HALF_WIDTH);
        ymin = gw.toPixelsY(-HALF_HEIGHT);
        ymax = gw.toPixelsY(HALF_HEIGHT);

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.staticBody);
        bdef.setPosition(0, 0);


        body = gw.world.createBody(bdef);

        PolygonShape box = new PolygonShape();
        box.setAsBox(HALF_WIDTH, HALF_HEIGHT);

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(box);
        fdef.setFriction(0.3f);
        body.createFixture(fdef);

        bdef.delete();
        fdef.delete();
        box.delete();
    }

    public void spawn() {
       float angle = rng.nextFloat(360.0f);
       float x = (float) Math.cos(angle) * SPAWN_DIST;
        float y = (float) Math.sin(angle) * SPAWN_DIST;

        var ant = new Ant(gw, x, y, angle);
        ants.add(ant);
        gw.addGameObject(ant);
    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {

        canvas.save();
        canvas.drawRect(xmin, ymin, xmax, ymax, paint);
        canvas.restore();
    }
}
