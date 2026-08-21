package com.gdd.game.screen;

import com.gdd.game.ecs.misc.Box;
import com.gdd.game.Game;
import com.gdd.game.GameWorld;
import com.gdd.game.Settings;

public class GameScreen extends Screen {

    private GameWorld gw;

    public GameScreen(Game game) {
        super(game);

        // World: physical simulation
        float halfWorldWidth = Settings.worldWidth / 2;
        float halfWorldHeight = Settings.worldHeight / 2;
        Box worldSize = new Box(-halfWorldWidth, -halfWorldHeight,
                halfWorldWidth, halfWorldHeight);

        gw = new GameWorld(game.getFramebuffer(), game.getScreensize(), worldSize);
        gw.setTouchHandler(game.getTouchHandler());
    }

    // ***************************************
    //  Game loop
    // ***************************************

    @Override
    public void update(float deltaTime) {
        gw.update(deltaTime);
    }

    @Override
    public void render() {
        gw.render();
    }

    // ***************************************
    //  Android callbacks
    // ***************************************

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
