package com.gdd.game.ecs.factories;

import android.graphics.Color;

import com.gdd.game.Assets;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.BitmapRenderComp;
import com.gdd.game.ecs.components.BoxRenderComp;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.EntityTag;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Vec2;

public class NestFactory {

    private static final float SIDE = 0.25f;

    private NestFactory() {}

    public static Entity makeNest(GameWorld gw, Vec2 nestPosition) {

        var nest = new Entity(EntityTag.NEST);
        nest.transform.halfWidth = SIDE*2.5f;
        nest.transform.halfHeight = SIDE*2.5f;
        // nest.addComponent(new BoxRenderComp(Color.BLUE, true));
        nest.addComponent(new BitmapRenderComp(Assets.NEST_BITMAP));
        nest.addComponent(new HealthComponent(1000));

        // ***** PHYSICS

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
