package com.gdd.game.ecs.systems;

import android.util.Log;

import com.gdd.game.Box;
import com.gdd.game.GameWorld;
import com.gdd.game.ecs.entities.Entity;
import com.gdd.game.ecs.factories.AntFactory;
import com.gdd.game.ecs.factories.FoodFactory;
import com.gdd.game.ecs.factories.WaspFactory;

import java.util.List;
import java.util.Random;

public class SpawnSystem  implements System {
    public static final float BORDER_MARGIN = 0.5f;   // keep food away from world edges
    private static final float TWO_PI = (float)(Math.PI * 2);

    private final GameWorld gw;
    private final Box worldSize;
    private final Random rng = new Random();
    private float timerAnt = 0f;
    public int antCount = 0;
    private float timerFood = 0f;
    public int foodCount = 0;
    private float timerWasp = 0f;
    public int waspCount = 0;

    private final Entity nest;

    private static enum WaspSpawnSide {
        NORTH, EAST, SOUTH, OVEST;
    }
    private final int PRECOMPUTED_SPAWN_SIZE_CACHE = 32;
    private final WaspSpawnSide[] precomputedSpawnSides;
    private int waspSpawnSideIndex = 0;

    public SpawnSystem(GameWorld gw, Entity nest) {
        this.gw = gw;
        this.nest = nest;
        this.worldSize = gw.worldSize;
        precomputedSpawnSides = new WaspSpawnSide[32];

        /* Based on the random choice
         * - 0: spawn north
         * - 1: spawn east
         * - 2: spawn south
         * - 3: spawn ovest
         */

        var sides = WaspSpawnSide.values();
        for (int i = 0; i < PRECOMPUTED_SPAWN_SIZE_CACHE; i++) {
            var randomChoice = rng.nextInt(4);
            precomputedSpawnSides[i] = sides[randomChoice];
        }
   }

    private void spawnAnt(List<Entity> entities, float dt) {
        timerAnt += dt;
        if (antCount >= Entity.ANT_MAX_COUNT) {
            return;
        }
        if (timerAnt >= Entity.ANT_SPAWN_INTERVAL) {
            float direction = rng.nextFloat(TWO_PI);
            entities.add(AntFactory.makeAnt(gw, nest.transform.x, nest.transform.y, direction));
            antCount++;
            timerAnt = 0f;
        }
    }

    private void spawnWasp(List<Entity> entities, float dt) {
        timerWasp += dt;
        if (waspCount >= Entity.WASP_MAX_COUNT) {
            return;
        }
        if (timerWasp >= Entity.WASP_SPAWN_INTERVAL) {
            float x = 0f, y = 0f;
            var nextSpawnSide = precomputedSpawnSides[waspSpawnSideIndex];
            waspSpawnSideIndex = (waspSpawnSideIndex + 1) % PRECOMPUTED_SPAWN_SIZE_CACHE;
            var percentage = rng.nextFloat(0.1f, 0.9f);
            switch (nextSpawnSide) {
                case NORTH -> {
                    x = worldSize.xmin + worldSize.width * percentage;
                    y = worldSize.ymin;
                }
                case EAST -> {
                    x = worldSize.xmax;
                    y = worldSize.ymax - worldSize.height * percentage;
                }
                case SOUTH -> {
                    x = worldSize.xmax - worldSize.width * percentage;
                    y = worldSize.ymax;
                }
                case OVEST -> {
                    x = worldSize.xmin;
                    y = worldSize.ymin + worldSize.height * percentage;
                }
            }
            var dx = nest.transform.x - x;
            var dy = nest.transform.y - y;
            float direction = (float) Math.atan2(dy, dx) + rng.nextFloat(-Entity.WASP_MAX_DEGREE_INWARD, Entity.WASP_MAX_DEGREE_INWARD);
            entities.add(WaspFactory.makeWasp(gw, x, y, direction));
            waspCount++;
            timerWasp = 0f;
        }
    }

    private void spawnFood(List<Entity> entities, float dt) {
        timerFood += dt;
        if (foodCount >= Entity.FOOD_MAX_COUNT) {
            return;
        }
        if (timerFood >= Entity.FOOD_SPAWN_INTERVAL) {
            float x, y;
            x = rng.nextFloat(worldSize.xmin + BORDER_MARGIN, worldSize.xmax - BORDER_MARGIN);
            y = rng.nextFloat(worldSize.ymin + BORDER_MARGIN, worldSize.ymax - BORDER_MARGIN);
            foodCount++;
            entities.add(FoodFactory.makeFood(gw, x, y));
            timerFood = 0f;
        }
    }

    @Override
    public void update(List<Entity> entities, float dt) {
        spawnFood(entities, dt);
        spawnAnt(entities, dt);
        spawnWasp(entities, dt);
    }
}
