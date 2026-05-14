package com.gdd.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.Vec2;

import java.util.Random;

public class Ant extends GameObject {
    private static final float DENSITY     = 1.0f;
    private static final float FRICTION    = 0.3f;
    private static final float RESTITUTION = 0.2f;

    private final Canvas canvas;
    private static final Random rng = new Random();

    private final Paint paint = new Paint();
    private final float radius = 0.1f;
    private final Path drawPath = new Path();

    public Ant(GameWorld gw) {
       super(gw);
       name = "ANT";

       canvas = new Canvas(gw.buffer);
       paint.setARGB(255, 170, 0, 200);
       paint.setStyle(Paint.Style.FILL);

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.dynamicBody);
        bdef.setPosition(0, 0); // spawned on the Nest
        float angle = rng.nextFloat(360.0f);

        bdef.setAngle(angle);

        body = gw.world.createBody(bdef);
        body.setUserData(this);
        body.setSleepingAllowed(false);

        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setDensity(DENSITY);
        fdef.setFriction(FRICTION);
        fdef.setRestitution(RESTITUTION);
        body.createFixture(fdef);
        body.setLinearVelocity(new Vec2(0.5f * (float) Math.cos(angle), 0.5f * (float) Math.sin(angle)));

        fdef.delete();
        bdef.delete();
        shape.delete();
    }

    public void update(float dt) {

    }

    @Override
    public void draw(Bitmap buf, float x, float y, float angle) {
        float r = gw.toPixelsXLength(radius);
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate((float) Math.toDegrees(angle));
        canvas.drawCircle(0, 0, r, paint);
        canvas.restore();
    }
}
