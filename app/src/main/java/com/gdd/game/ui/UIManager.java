package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.badlogic.androidgames.framework.Input;

import java.util.ArrayList;
import java.util.List;

public class UIManager {

    private final List<UIElement> elements = new ArrayList<>();


    public void add(UIElement e)    { elements.add(e); }
    public void remove(UIElement e) { elements.remove(e); }
    public void clear()             { elements.clear(); }


    public void draw(Canvas canvas, Paint paint) {
        for (UIElement e : elements) e.draw(canvas,paint);
    }

    public boolean handleInput(Input.TouchEvent event) {

        // Iterazione inversa: l'elemento più "sopra" ha priorità
        for (int i = elements.size() - 1; i >= 0; i--) {
            if (elements.get(i).handleInput(event)) {
                return true;
            }
        }

        return false;
    }

}
