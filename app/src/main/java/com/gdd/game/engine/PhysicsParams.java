package com.gdd.game.engine;

import com.google.fpl.liquidfun.BodyType;

public class PhysicsParams {

    public enum ShapeType { CIRCLE, BOX }

    public BodyType bodyType = BodyType.dynamicBody;
    public float x, y;
    public float direction;

    public ShapeType shapeType = ShapeType.BOX;
    public float radius;       // usato se shapeType == CIRCLE
    public float width, height; // usati se shapeType == BOX
}