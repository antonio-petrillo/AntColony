package com.gdd.game.ui;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.badlogic.androidgames.framework.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * UIManager coordina l'intero ciclo di vita della UI:
 *  - un "main layout" sempre presente durante la partita
 *  - uno stack di "popup layout" (pausa, conferma uscita, risultato...)
 *  - draw/update di tutto quanto
 *  - routing dell'input grezzo (Input.TouchEvent) verso i widget,
 *    restituendo al chiamante gli eventi NON consumati, da passare
 *    alla scena sottostante (scroll/zoom/gameplay).
 *
 * Policy di possesso del touch: la UI gestisce AL MASSIMO UN pointer alla
 * volta. Se un TOUCH_DOWN colpisce un widget mentre la UI non sta già
 * gestendo un altro dito, quel pointer id viene "posseduto" fino al
 * relativo TOUCH_UP: tutti i TOUCH_DRAGGED/TOUCH_UP con lo stesso id
 * vanno dritti al widget posseduto (niente hit-test ripetuto). Se la UI
 * possiede già un pointer, i tocchi di altre dita passano direttamente
 * alla scena: così un pinch-to-zoom a due dita convive con un bottone
 * premuto senza bisogno di logica aggiuntiva.
 *
 * Un popup "modale" blocca comunque il touch anche quando non colpisce
 * nessun widget preciso (es. si tocca uno spazio vuoto del pannello
 * pausa): l'evento viene consumato lo stesso, per non far scivolare il
 * dito sulla scena sotto mentre il gioco è in pausa.
 */
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

    // Stato del pointer attualmente posseduto dalla UI (al massimo uno).
    private int activePointer = NO_POINTER;
    private Widget activeWidget = null; // null = pointer posseduto solo per bloccare un popup modale


    // [NON SERVE PER ORA] potrebbe essere utile per fare da filtro preliminare in futuro.
    // Lista riusata ogni frame per gli eventi da restituire alla scena:
    // evita una nuova allocazione ad ogni chiamata di processInput().
    //private final List<Input.TouchEvent> remainingEvents = new ArrayList<>();

    /**
     * Processa la lista grezza di eventi ricevuta dal MultiTouchHandler.
     * Restituisce gli eventi NON consumati dalla UI, da inoltrare alla
     * scena di gioco sottostante (es. per pan/zoom della camera).
     *
     * La lista restituita è riusata internamente: non conservarne un
     * riferimento oltre il frame corrente.
     */
    /*
    public List<Input.TouchEvent> processInput(List<Input.TouchEvent> events) {
        remainingEvents.clear();

        int n = events.size();
        for (int i = 0; i < n; i++) {
            Input.TouchEvent event = events.get(i);
            boolean consumed;
            switch (event.type) { ... }  // come il metodo già usato sotto

            if (!consumed) {
                remainingEvents.add(event);
            }
        }
        return remainingEvents;
     }
     */

    // ------------------------------------------------------------------
    // Configurazione layout
    // ------------------------------------------------------------------

    public void setMainLayout(WidgetGroup layout) {
        this.mainLayout = layout;
    }

    public WidgetGroup getMainLayout() {
        return mainLayout;
    }

    /** Mostra un popup modale (blocca input a main layout e scena). */
    public void showPopup(WidgetGroup popup) {
        showPopup(popup, true);
    }

    public void showPopup(WidgetGroup popup, boolean modal) {
        popups.add(new PopupEntry(popup, modal));
    }

    /** Rimuove il popup più in cima allo stack (es. bottone "resume"). */
    public void hideTopPopup() {
        if (!popups.isEmpty()) {
            popups.remove(popups.size() - 1);
        }
    }

    /** Rimuove uno specifico popup ovunque si trovi nello stack. */
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

    // per ora non fa nulla, non so se serve. forse per i widgetgroup che fanno "layout"
    public void update(float deltaTime) {
        if (mainLayout != null) {
            mainLayout.update(deltaTime);
        }
        int n = popups.size();
        for (int i = 0; i < n; i++) {
            popups.get(i).layout.update(deltaTime);
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        if (mainLayout != null) {
            mainLayout.draw(canvas, paint);
        }

        int n = popups.size();
        for (int i = 0; i < n; i++) {
            PopupEntry entry = popups.get(i);
            if (entry.modal) {
                // Oscura ciò che sta sotto per dare risalto al popup.
                canvas.drawColor(0x99000000);
            }
            entry.layout.draw(canvas, paint);
        }
    }


    // ------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------

    /**
     * Processa la lista grezza di eventi ricevuta dal MultiTouchHandler.
     * Restituisce gli eventi NON consumati dalla UI, da inoltrare alla
     * scena di gioco sottostante (es. per pan/zoom della camera).
     *
     * La lista restituita è riusata internamente: non conservarne un
     * riferimento oltre il frame corrente.
     */
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
            // La UI sta già gestendo un altro dito: questo passa alla scena.
            return false;
        }

        WidgetGroup topLayer = topLayer();
        Widget hitWidget = topLayer != null ? topLayer.hit(event.x, event.y) : null;

        if (hitWidget != null && hitWidget.touchDown(event.x, event.y, event.pointer)) {
            activePointer = event.pointer;
            activeWidget = hitWidget;
            return true;
        }

        // arriva qua se l'if precedente non trova un widget
        if (isTopPopupModal()) {
            // Nessun widget colpito, ma il popup in cima è modale:
            // blocchiamo comunque il tocco per non farlo passare alla scena.
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

    /*
     * Ritorna il layer più alto attuale.
     *  (devo controllare se è attivo?)
     */
    private WidgetGroup topLayer() {
        if (popups.isEmpty()) return mainLayout;
        return popups.get(popups.size() - 1).layout;
    }

    private boolean isTopPopupModal() {
        return !popups.isEmpty() && popups.get(popups.size() - 1).modal;
    }

}
