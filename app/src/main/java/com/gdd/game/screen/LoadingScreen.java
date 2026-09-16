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

        Assets.song = game.getAudio().newMusic("mario64_theme.mp3");

        // ***** SOUNDS *****

        Assets.click = game.getAudio().newSound("click.ogg");
        Assets.card_draw1 = game.getAudio().newSound("card_draw1.mp3");
        Assets.card_draw2 = game.getAudio().newSound("card_draw2.mp3");
        Assets.card_disappear = game.getAudio().newSound("card_disappear.mp3");

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
