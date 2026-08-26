package com.gdd.game.ui;

public class VerticalGroup extends WidgetGroup {

    private float spacing = 8f;
    private float padding = 4f;

    public VerticalGroup(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    @Override
    protected void layout() {
        float cursorY = padding;
        for (Widget child : children) {
            float w = child.getPrefWidth();
            float h = child.getPrefHeight();

            // riempi orizzontalmente il gruppo
            child.setSize(width - padding * 2, h);
            child.setPosition(padding, cursorY);

            cursorY += h + spacing;
        }
    }

    @Override
    public float getPrefWidth() {
        float max = 0;
        for (Widget c : children) max = Math.max(max, c.getPrefWidth());
        return max + padding * 2;
    }

    @Override
    public float getPrefHeight() {
        float total = padding * 2;
        for (Widget c : children) total += c.getPrefHeight();
        if (children.size() > 1) total += spacing * (children.size() - 1);
        return total;
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
        invalidate();
    }

    public void setPadding(float padding) {
        this.padding = padding;
        invalidate();
    }
}
