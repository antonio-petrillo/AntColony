package com.gdd.game.screen;

import com.gdd.game.Game;

public abstract class Screen {

    public final Game game;

    public Screen(Game game) { this.game = game; }

    public abstract void update(float deltaTime);

    public abstract void render();

    public abstract void pause();

    public abstract void resume();

    public abstract void dispose();
}
