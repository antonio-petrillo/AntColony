package com.gdd.game.ecs.entities;

import android.graphics.Paint;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;

public class NestFactory {
    private NestFactory() {}

    private static int nestCount = 0;
    private static Entity theNest = new Entity();

    public static Entity makeNest(GameWorld gw) {
        if (nestCount > 0) {
            throw new IllegalStateException("Only one Nest at a time is allowed!");
        }

        theNest.addComponent(new HealthComponent(1000));

        Paint paint = new Paint();
        paint.setARGB(255, 0, 255, 0);
        paint.setStyle(Paint.Style.STROKE);
        theNest.addComponent(new RenderComponent(paint, RenderComponent.Kind.NEST));

        final float SIDE = 0.25f;

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.staticBody);
        bdef.setPosition(0, 0);

        var body = gw.world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(SIDE, SIDE);

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setFriction(0.3f);
        body.createFixture(fdef);

        bdef.delete();
        fdef.delete();
        shape.delete();

        body.setUserData(theNest);
        theNest.addComponent(new PhysicComponent(body));

        nestCount++;
        return theNest;
    }

    public static void deleteNest() {
        nestCount--;
        theNest.components.clear();
    }
}
