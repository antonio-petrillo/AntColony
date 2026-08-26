package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

public class HorizontalGroup extends WidgetGroup {

    private float spacing = 8f;
    private float padding = 4f;


    public HorizontalGroup(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    // ***************************************
    //  Layout
    // ***************************************

    @Override
    protected void layout() {
        float cursorX = padding;
        for (Widget child : children) {
            float w = child.getPrefWidth();
            float h = child.getPrefHeight();

            // riempi verticalmente il gruppo
            child.setSize(w, height - padding * 2);
            child.setPosition(cursorX, padding);

            cursorX += w + spacing;
        }
    }

    // ***************************************
    //  Render
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    // ***************************************
    //  Getter / Setter
    // ***************************************

    @Override
    public float getPrefWidth() {
        float total = padding * 2;
        for (Widget c : children) total += c.getPrefWidth();
        if (children.size() > 1) total += spacing * (children.size() - 1);
        return total;
    }

    @Override
    public float getPrefHeight() {
        float max = 0;
        for (Widget c : children) max = Math.max(max, c.getPrefHeight());
        return max + padding * 2;
    }
}
