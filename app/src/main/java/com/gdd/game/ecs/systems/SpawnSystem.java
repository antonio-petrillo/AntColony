package com.gdd.game.ecs.systems;

import com.gdd.game.Box;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.entities.FoodFactory;

import java.util.List;
import java.util.Random;

public class SpawnSystem  implements System {
    public static final float SPAWN_INTERVAL = 3.0f;
    public static final float MIN_SPAWN_DIST = 2.0f;   // metres from origin (nest)
    private static final float MIN_SPAWN_DIST_SQUARE = MIN_SPAWN_DIST * MIN_SPAWN_DIST;   // metres from origin (nest)
    public static final float BORDER_MARGIN = 0.5f;   // keep food away from world edges

    private final GameWorld gw;
    private final Random rng = new Random();
    private float timer = 0f;

    public SpawnSystem(GameWorld gw) {
        this.gw = gw;
    }

    @Override
    public void update(List<Entity> entities, float dt) {
        timer += dt;
        if (timer < SPAWN_INTERVAL) return;
        timer = 0f;

        float x, y;
        Box world = gw.physicalSize;

        // Retry until we find a position far enough from the nest
        do {
            x = rng.nextFloat(world.xmin + BORDER_MARGIN, world.xmax - BORDER_MARGIN);
            y = rng.nextFloat(world.ymin + BORDER_MARGIN, world.ymax - BORDER_MARGIN);
        } while (x * x + y * y < MIN_SPAWN_DIST_SQUARE);

        entities.add(FoodFactory.makeFood(gw, x, y));
    }
}
