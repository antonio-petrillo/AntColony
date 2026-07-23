package com.gdd.game.engine.managers;

import com.gdd.game.engine.PhysicsParams;
import com.gdd.game.engine.components.PhysicsComponent;
import com.gdd.game.engine.core.Actor;
import com.google.fpl.liquidfun.BodyDef;
import com.google.fpl.liquidfun.BodyType;
import com.google.fpl.liquidfun.CircleShape;
import com.google.fpl.liquidfun.FixtureDef;
import com.google.fpl.liquidfun.PolygonShape;
import com.google.fpl.liquidfun.Shape;
import com.google.fpl.liquidfun.Vec2;
import com.google.fpl.liquidfun.World;

public class PhysicsManager {

    // Parameters for world simulation
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    // Parameters
    private static final float DENSITY     = 1.0f;
    private static final float FRICTION    = 0.3f;
    private static final float RESTITUTION = 0.2f;
    private static final float RADIUS = 0.1f;

    // Physics Simulation
    private final World world;

    private final float worldWidth, worldHeight; // boundaries


    /*
     * Constructor.
     */
    public PhysicsManager(float worldWidth, float worldHeight) {
        this.world = new World(0, 0);  // gravity vector
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        createBoundaries();
    }


    // ------------------------------------------------------------------
    // Getter / Setter
    // ------------------------------------------------------------------

    public synchronized void setGravity(float x, float y) {
        world.setGravity(x, y);
    }


    // ------------------------------------------------------------------
    // Game Loop
    // ------------------------------------------------------------------

    public synchronized void step(float dt) {
        // Handle collisions: advance the physics simulation
        world.step(dt, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
    }


    // ------------------------------------------------------------------
    // Component
    // ------------------------------------------------------------------

    /*
      Informazioni da impostare:

      tipo: static, dynamic, cinematic
      posizione (x,y)
      direzione
      shape = circle (+ radius) or polygon (+ size)
     */
    public PhysicsComponent createComponent(PhysicsParams params) {

        if(params == null)
            return null;

        // BODY DEF
        BodyDef bdef = new BodyDef();
        bdef.setType( params.bodyType );
        bdef.setPosition( params.x, params.y);
        bdef.setAngle( params.direction );
        bdef.setAngularDamping(0);
        bdef.setLinearDamping(0);
        bdef.setFixedRotation(true);

        // BODY
        var body = world.createBody(bdef);
        body.setSleepingAllowed(false);

        // SHAPE
        Shape shape;
        if(params.shapeType == PhysicsParams.ShapeType.CIRCLE) {
            CircleShape circle = new CircleShape();
            circle.setRadius( params.radius );
            shape = circle;
        } else {
            PolygonShape polygon = new PolygonShape();
            polygon.setAsBox( params.width/2, params.height/2 );
            shape = polygon;
        }

        // FIXTURE DEF
        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setDensity(DENSITY);
        fdef.setFriction(FRICTION);
        fdef.setRestitution(1f); // riemttere RESTITUTION
        body.createFixture(fdef);

        // **** TEST ****
        var vec = new Vec2(
                10 * (float) Math.cos(params.direction),
                10 * (float) Math.sin(params.direction)
        );
        body.setLinearVelocity(vec);
        vec.delete();

        shape.delete();
        fdef.delete();
        bdef.delete();

        return new PhysicsComponent(body);
    }

    // ------------------------------------------------------------------
    // Utils
    // ------------------------------------------------------------------

    private void createBoundaries() {

        float THICKNESS = 1f;
        float xmax = worldWidth / 2;
        float xmin = -xmax;
        float ymax = worldHeight / 2;
        float ymin = -ymax;

        // body definition: position and type
        BodyDef bdef = new BodyDef();

        var body = world.createBody(bdef);
        body.setSleepingAllowed(false);

        PolygonShape shape = new PolygonShape();

        FixtureDef fdef = new FixtureDef();
        fdef.setShape(shape);
        fdef.setDensity(0.f);
        fdef.setFriction(0.f);
        fdef.setRestitution(0.8f);

        // top
        shape.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymin, 0);
        body.createFixture(fdef);
        // bottom
        shape.setAsBox(xmax-xmin, THICKNESS, xmin+(xmax-xmin)/2, ymax, 0);
        body.createFixture(fdef);
        // left
        shape.setAsBox(THICKNESS, ymax-ymin, xmin, ymin+(ymax-ymin)/2, 0);
        body.createFixture(fdef);
        // right
        shape.setAsBox(THICKNESS, ymax-ymin, xmax, ymin+(ymax-ymin)/2, 0);
        body.createFixture(fdef);

        // clean up native objects
        bdef.delete();
        shape.delete();
        fdef.delete();
    }


}