package com.gdd.game.ecs.entities;

import android.graphics.Paint;

import com.gdd.game.Game;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;

public class NestFactory {
    private NestFactory() {}

    public static Entity makeNest(Game gw, Vec2 nestPosition) {

        var nest = new Entity(Entity.Kind.NEST);
        nest.addComponent(new HealthComponent(1000));

        Paint paint = new Paint();
        paint.setARGB(255, 0, 255, 0);
        paint.setStyle(Paint.Style.STROKE);
        nest.addComponent(new RenderComponent(paint));

        final float SIDE = 0.25f;

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.staticBody);
        bdef.setPosition(nestPosition.getX(), nestPosition.getY());

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

        body.setUserData(nest);
        nest.addComponent(new PhysicComponent(body));

        return nest;
    }

}
