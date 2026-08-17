package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

import java.util.ArrayList;
import java.util.List;

public class UIController {

    private static final int NO_POINTER = -1;

    private static final class PopupEntry {
        final WidgetGroup layout;
        final boolean modal;

        PopupEntry(WidgetGroup layout, boolean modal) {
            this.layout = layout;
            this.modal = modal;
        }
    }

    private final List<PopupEntry> popups = new ArrayList<>();
    private WidgetGroup mainLayout;

    private int activePointer = NO_POINTER;
    private Widget activeWidget = null;

    // ------------------------------------------------------------------
    // Configurazione layout
    // ------------------------------------------------------------------

    public void setMainLayout(WidgetGroup layout) {
        this.mainLayout = layout;
    }

    public WidgetGroup getMainLayout() {
        return mainLayout;
    }

    public void showPopup(WidgetGroup popup) {
        showPopup(popup, true);
    }

    public void showPopup(WidgetGroup popup, boolean modal) {
        popups.add(new PopupEntry(popup, modal));
    }

    public void hideTopPopup() {
        if (!popups.isEmpty()) {
            popups.remove(popups.size() - 1);
        }
    }

    public void hidePopup(WidgetGroup popup) {
        for (int i = popups.size() - 1; i >= 0; i--) {
            if (popups.get(i).layout == popup) {
                popups.remove(i);
                return;
            }
        }
    }

    public void clearPopups() {
        popups.clear();
    }

    public boolean hasPopup() {
        return !popups.isEmpty();
    }


    // ------------------------------------------------------------------
    // Ciclo di vita: update / draw
    // ------------------------------------------------------------------

    public void draw(Canvas canvas) {
        if (mainLayout != null) {
            mainLayout.draw(canvas);
        }

        int n = popups.size();
        for (int i = 0; i < n; i++) {
            PopupEntry entry = popups.get(i);
            if (entry.modal) {
                canvas.drawColor(0x99000000);
            }
            entry.layout.draw(canvas);
        }
    }

    // ------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------

    public boolean processInput(Input.TouchEvent event) {
        if(event == null) return false;

        boolean consumed = false;

        switch (event.type) {
            case Input.TouchEvent.TOUCH_DOWN:
                consumed = handleTouchDown(event);
                break;
            case Input.TouchEvent.TOUCH_DRAGGED:
                consumed = handleTouchDragged(event);
                break;
            case Input.TouchEvent.TOUCH_UP:
                consumed = handleTouchUp(event);
                break;
        }

        return consumed;
    }

    private boolean handleTouchDown(Input.TouchEvent event) {

        if (activePointer != NO_POINTER) {
            return false;
        }

        WidgetGroup topLayer = topLayer();
        Widget hitWidget = topLayer != null ? topLayer.hit(event.x, event.y) : null;

        if (hitWidget != null && hitWidget.touchDown(event.x, event.y, event.pointer)) {
            activePointer = event.pointer;
            activeWidget = hitWidget;
            return true;
        }

        if (isTopPopupModal()) {
            activePointer = event.pointer;
            activeWidget = null;
            return true;
        }

        return false;
    }

    private boolean handleTouchDragged(Input.TouchEvent event) {
        if (event.pointer != activePointer)
            return false;
        if (activeWidget != null) {
            activeWidget.touchDragged(event.x, event.y, event.pointer);
        }
        return true;
    }

    private boolean handleTouchUp(Input.TouchEvent event) {
        if (event.pointer != activePointer)
            return false;
        if (activeWidget != null) {
            activeWidget.touchUp(event.x, event.y, event.pointer);
        }
        activePointer = NO_POINTER;
        activeWidget = null;
        return true;
    }

    private WidgetGroup topLayer() {
        if (popups.isEmpty()) return mainLayout;
        return popups.get(popups.size() - 1).layout;
    }

    private boolean isTopPopupModal() {
        return !popups.isEmpty() && popups.get(popups.size() - 1).modal;
    }
}
