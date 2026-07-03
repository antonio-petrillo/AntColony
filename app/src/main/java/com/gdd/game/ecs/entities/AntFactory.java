package com.gdd.game.ecs.entities;

import android.graphics.Paint;

import com.gdd.game.Game;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.HealthComponent;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
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
    private static final Random rng = new Random();
    private  AntFactory() {}

    public static Entity makeAnt(Game gw, float x, float y, float direction) {

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
        var ant = new Entity(Entity.Kind.ANT);

        ant.addComponent(new PhysicComponent(body));
        var paint = new Paint();
        paint.setARGB(255, 100, 0, 100);
        ant.addComponent(new RenderComponent(paint));

        float timeBetweenActions = rng.nextFloat(1.5f, 5.0f);

        ant.addComponent(new HealthComponent(20));
        ant.addComponent(new AiComponent(AiComponent.State.WANDER, timeBetweenActions, 10));

        body.setUserData(ant);

        return  ant;
    }

    public static final float ATTACK_COOLDOWN = 1.0f;
}
