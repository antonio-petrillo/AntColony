package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * WidgetGroup è un contenitore di Widget.
 *
 * Nota: i widget children dovrebbero essere sempre contenuti nel rettangolo di widgetgroup
 * altrimenti "hit" non li trova. Serve il metodo di update del layout per garantirlo.
 */
public abstract class WidgetGroup extends Widget {

    protected List<Widget> children = new ArrayList<>();

    public WidgetGroup(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public void addWidget(Widget w) {
        w.setParent(this);
        children.add(w);
    }

    public void removeWidget(Widget w) {
        if (children.remove(w)) {
            w.setParent(null);
        }
    }

    public void clear() {
        for (int i = 0; i < children.size(); i++) {
            children.get(i).setParent(null);
        }
        children.clear();
    }

    public List<Widget> getChildren() {
        return children;
    }


    // ***************************************
    //            Ciclo di vita
    // ***************************************

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!visible) return;

        int n = children.size();
        for (int i = 0; i < n; i++) {
            Widget child = children.get(i);
            if (child.isVisible()) {
                child.draw(canvas, paint);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        int n = children.size();
        for (int i = 0; i < n; i++) {
            children.get(i).update(deltaTime);
        }
    }

    /*
     * Se il punto (px,py) "colpisce" un widget nell'albero, allora verrà ritornato dal metodo.
     * Notare che se un widget o un widgetgroup ha visible = false o enabled = false allora
     * verrà ignorato.
     */
    public Widget hit(float px, float py) {
        if (!visible || !enabled || !contains(px, py))
            return null;

        for (int i = children.size() - 1; i >= 0; i--) {
            Widget child = children.get(i);
            if (!child.isVisible() || !child.isEnabled())
                continue;

            if (child instanceof WidgetGroup) {
                Widget found = ((WidgetGroup) child).hit(px, py);
                if (found != null)
                    return found;
            } else if (child.contains(px, py)) {
                return child;
            }
        }

        return null;
    }

}
