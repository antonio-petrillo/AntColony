package com.gdd.game.ui;

import android.graphics.Canvas;
import android.util.SparseArray;

import com.badlogic.androidgames.framework.Input;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*
 * UIController gestisce l'intero ciclo di vita dell'UI nel gioco.
 */
public class UIController {

    private static final class PopupEntry {
        final WidgetGroup popup;
        final boolean modal;

        PopupEntry(WidgetGroup popup, boolean modal) {
            this.popup = popup;
            this.modal = modal;
        }
    }

    private WidgetGroup root;
    private final List<PopupEntry> popups = new ArrayList<>();

    private final SparseArray<Widget> pointerOwners = new SparseArray<>(); // pointer e widget posseduti
    private final Set<Integer> modalBlockedPointers = new HashSet<>(); // pointer bloccati da popup modale senza widget

    // ***************************************
    //  Misc
    // ***************************************

    public void setRoot(WidgetGroup root) {
        this.root = root;
    }

    public WidgetGroup getRoot() {
        return root;
    }

    public void showPopup(WidgetGroup popup) {
        showPopup(popup, true);
    }

    public void showPopup(WidgetGroup popup, boolean modal) {
        if (modal) cancelAllPointers();
        popups.add(new PopupEntry(popup, modal));
        this.updateLayout();
    }

    public void hideTopPopup() {
        if (!popups.isEmpty()) {
            popups.remove(popups.size() - 1);
        }
    }

    public void clearPopups() {
        popups.clear();
    }

    public boolean hasPopup() {
        return !popups.isEmpty();
    }

    public void reset() {
        cancelAllPointers();
        clearPopups();
        root = null;
    }

    // ***************************************
    //  Layout
    // ***************************************

    /*
     * Aggiorna layout (dimensioni/posizioni locali) e
     * posizioni assolute di tutti i widget, root e popup inclusi.
     * Va richiamato prima di draw() e processInput() se ci sono state modifiche all'UI.
     */
    public void updateLayout() {
        if (root != null) {
            root.validate(); // 1. sistema x,y,w,h locali
            root.validateTransform();  // 2. calcola absX/absY
        }

        int n = popups.size();
        for (int i = 0; i < n; i++) {
            WidgetGroup popup = popups.get(i).popup;
            popup.validate();
            popup.validateTransform();
        }
    }

    // ***************************************
    //  Rendering
    // ***************************************

    /*
     * Disegna soltanto. Presuppone che updateLayout() sia già
     * stato chiamato in questo frame: qui non si ricalcola nulla.
     */
    public void draw(Canvas canvas) {
        if (root != null) {
            root.draw(canvas);
        }

        int n = popups.size();
        for (int i = 0; i < n; i++) {
            PopupEntry entry = popups.get(i);
            if (entry.modal) {
                canvas.drawColor(0x99000000);
            }
            entry.popup.draw(canvas);
        }
    }

    // ***************************************
    //  Input
    // ***************************************

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
        WidgetGroup topLayer = getTopLayer();
        Widget hitWidget = topLayer != null ? topLayer.hit(event.x, event.y) : null;

        if (hitWidget != null && hitWidget.touchDown(event.x, event.y, event.pointer)) {
            pointerOwners.put(event.pointer, hitWidget);
            return true;
        }

        if (isTopPopupModal()) {
            modalBlockedPointers.add(event.pointer);
            return true;
        }

        return false;
    }

    private boolean handleTouchDragged(Input.TouchEvent event) {
        Widget w = pointerOwners.get(event.pointer);
        if (w != null) {
            w.touchDragged(event.x, event.y, event.pointer);
            return true;
        }
        return modalBlockedPointers.contains(event.pointer);
    }

    private boolean handleTouchUp(Input.TouchEvent event) {
        Widget w = pointerOwners.get(event.pointer);
        if (w != null) {
            w.touchUp(event.x, event.y, event.pointer);
            pointerOwners.remove(event.pointer);
            return true;
        }
        return modalBlockedPointers.remove(event.pointer);
    }

    public void cancelPointer(int pointer) {
        Widget w = pointerOwners.get(pointer);
        if (w != null) w.touchCancelled(pointer);
        pointerOwners.remove(pointer);
        modalBlockedPointers.remove(pointer);
    }

    public void cancelAllPointers() {
        for (int i = 0; i < pointerOwners.size(); i++) {
            Widget w = pointerOwners.valueAt(i);
            if (w != null) w.touchCancelled(pointerOwners.keyAt(i));
        }
        pointerOwners.clear();
        modalBlockedPointers.clear();
    }

    public void cancelPointerFor(Widget widget) {
        for (int i = pointerOwners.size() - 1; i >= 0; i--) {
            if (pointerOwners.valueAt(i) == widget) {
                widget.touchCancelled(pointerOwners.keyAt(i));
                pointerOwners.removeAt(i);
            }
        }
    }

    // ***************************************
    //  Misc
    // ***************************************

    private WidgetGroup getTopLayer() {
        if (popups.isEmpty()) return root;
        return popups.get(popups.size() - 1).popup;
    }

    private boolean isTopPopupModal() {
        return !popups.isEmpty() && popups.get(popups.size() - 1).modal;
    }
}
