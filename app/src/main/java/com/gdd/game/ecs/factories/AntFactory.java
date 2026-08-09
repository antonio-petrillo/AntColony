package com.gdd.game.ecs.factories;

import android.graphics.Color;
import android.graphics.Paint;

import com.gdd.game.Assets;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.BitmapRenderComp;
import com.gdd.game.ecs.components.CircleRenderComp;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.BoxRenderComp;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.EntityTag;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.Vec2;

import java.util.Random;

public class AntFactory {

    private static final float DENSITY     = 1.0f;
    private static final float FRICTION    = 0.3f;
    private static final float RESTITUTION = 0.2f;
    private static final float RADIUS = 0.1f;

    public static final float ATTACK_COOLDOWN = 1.0f;

    private static final Random rng = new Random();

    private  AntFactory() {}

    public static Entity makeAnt(GameWorld gw, float x, float y, float direction) {

        var ant = new Entity(EntityTag.ANT);
        ant.transform.halfWidth = RADIUS/2;
        ant.transform.halfHeight = RADIUS/2;
        ant.addComponent(new CircleRenderComp(Color.RED, true));
        ant.addComponent(new HealthComponent(20));

        float timeBetweenActions = rng.nextFloat(1.5f, 5.0f);
        ant.addComponent(new AiComponent(AiComponent.State.WANDER, timeBetweenActions, 10));

        // ***** PHYSICS

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.dynamicBody);
        bdef.setPosition(x, y); // spawned on the Nest
        bdef.setAngle(direction);
        bdef.setAngularDamping(0);
        bdef.setLinearDamping(0);
        bdef.setFixedRotation(true);

        var body = gw.world.createBody(bdef);
        body.setSleepingAllowed(false);

        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setDensity(DENSITY);
        fdef.setFriction(FRICTION);
        fdef.setRestitution(RESTITUTION);
        body.createFixture(fdef);
        var vec = new Vec2(
                Entity.ANT_SPEED * (float) Math.cos(direction),
                Entity.ANT_SPEED * (float) Math.sin(direction)
        );
        body.setLinearVelocity(vec);
        vec.delete();

        fdef.delete();
        bdef.delete();
        shape.delete();

        body.setUserData(ant);
        ant.addComponent(new PhysicComponent(body));

        return  ant;
    }

}
