package com.gdd.game.engine.components;

import com.gdd.game.engine.Actor;

public abstract class Component {

    public Actor owner;

    public void setOwner ( Actor owner ) { this . owner = owner ; }
    public Actor getOwner () { return owner ; }

    public abstract ComponentType type();
}
