package com.gdd.game.ecs.factories;

import android.graphics.Color;
import android.graphics.Paint;

import com.gdd.game.Assets;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.components.AiComponent;
import com.gdd.game.ecs.components.BitmapRenderComp;
import com.gdd.game.ecs.components.CircleRenderComp;
import com.gdd.game.ecs.components.PhysicComponent;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.EntityTag;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;

import org.json.JSONException;
import org.json.JSONObject;

public class FoodFactory {

    private static float RADIUS, DENSITY, FRICTION, RESTITUTION;

    private FoodFactory() {}

    public static void init(JSONObject o) throws JSONException {

        JSONObject p = o.getJSONObject("physics");
        RADIUS = (float) p.getDouble("size");
        DENSITY = (float) p.getDouble("density");
        FRICTION = (float) p.getDouble("friction");
        RESTITUTION = (float) p.getDouble("restitution");
    }

    public static Entity makeFood(GameWorld gw, float x, float y) {

        var food = new Entity(EntityTag.FOOD);
        food.transform.halfWidth = RADIUS;
        food.transform.halfHeight = RADIUS;
        food.transform.x = x;
        food.transform.y = y;
        //food.addComponent(new CircleRenderComp(Color.WHITE, true));
        food.addComponent(new BitmapRenderComp(Assets.FOOD_BITMAP));
        food.addComponent(new AiComponent(AiComponent.State.NONE));

        // ***** PHYSICS

        BodyDef bdef = new BodyDef();
        bdef.setType(BodyType.dynamicBody);
        bdef.setPosition(x, y);
        bdef.setLinearDamping(4);
        bdef.setAngularDamping(4);

        var body = gw.world.createBody(bdef);

        CircleShape shape = new CircleShape();
        shape.setRadius(RADIUS);

        FixtureDef fdef = new FixtureDef();

        fdef.setShape(shape);
        fdef.setDensity(DENSITY);
        fdef.setFriction(FRICTION);
        fdef.setRestitution(RESTITUTION);

        body.createFixture(fdef);

        fdef.delete();
        shape.delete();;
        bdef.delete();

        body.setUserData(food);
        food.addComponent(new PhysicComponent(body));

        return food;
    }
}
