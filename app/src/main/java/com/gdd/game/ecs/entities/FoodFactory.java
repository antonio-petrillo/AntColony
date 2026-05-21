package com.gdd.game.ecs.entities;

import android.graphics.Paint;

import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.components.RenderComponent;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;

public class FoodFactory {

    public static final float RADIUS = 0.2f;
    private FoodFactory() {}

    public static Entity makeFood(GameWorld gw, float x, float y) {

        var paint = new Paint();
        paint.setARGB(100, 80, 80, 80);
        paint.setStyle(Paint.Style.STROKE);

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.kinematicBody);
        bdef.setPosition(x, y);

        var body = gw.world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);

        FixtureDef fdef = new FixtureDef();

        fdef.setShape(shape);
        fdef.setRestitution(0);
        fdef.setFriction(0);
        fdef.setDensity(0); // TODO: spqwn food with differents weights

        body.createFixture(fdef);

        fdef.delete();
        shape.delete();;
        bdef.delete();

        var food = new Entity(Entity.Kind.FOOD);
        food.addComponent(new PhysicComponent(body));
        food.addComponent(new RenderComponent(paint));

        body.setUserData(food);

        return food;
    }
}
