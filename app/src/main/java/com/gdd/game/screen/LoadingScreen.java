package com.gdd.game.screen;

import com.gdd.game.Assets;
import com.gdd.game.Game;

public class LoadingScreen extends Screen {

    public LoadingScreen(Game game) {
        super(game);
    }

    // ***************************************
    //  Game loop
    // ***************************************

    public void update(float deltaTime) {

        Assets.click = game.getAudio().newSound("click.ogg");

        game.setScreen(new MainMenuScreen(game));
    }

    public void render() {

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
