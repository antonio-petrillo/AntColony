package com.gdd.game.engine;

import com.google.fpl.liquidfun.ParticleSystem;
import com.google.fpl.liquidfun.World;

public class ScenePhysics {

    // Parameters for world simulation
    private static final int VELOCITY_ITERATIONS = 8;
    private static final int POSITION_ITERATIONS = 3;
    private static final int PARTICLE_ITERATIONS = 3;

    // Particles
    private static final int MAXPARTICLECOUNT = 1000;
    private static final float PARTICLE_RADIUS = 0.3f;


    // Physics Simulation
    public World world;
    public ParticleSystem particleSystem;


    /*
     * Constructor.
     */
    public ScenePhysics() {
        this.world = new World(0, 0);  // gravity vector
    }


    // ------------------------------------------------------------------
    // Getter / Setter
    // ------------------------------------------------------------------

    public synchronized void setGravity(float x, float y)
    {
        world.setGravity(x, y);
    }


    // ------------------------------------------------------------------
    // Game Loop
    // ------------------------------------------------------------------

    public synchronized void step(float dt) {
        // Handle collisions: advance the physics simulation
        world.step(dt, VELOCITY_ITERATIONS, POSITION_ITERATIONS, PARTICLE_ITERATIONS);
    }

}
