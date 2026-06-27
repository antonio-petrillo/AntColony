package com.gdd.game;

public class CameraHandler {

    //TODO: modificare completamente il funzionamento tenendo conto della classe Box

    public final Box currentView;
    public final Box maxView;

    public CameraHandler(Box currentView) {
        this.currentView = currentView;
        this.maxView = new Box(currentView);
    }


    public void scrollx(float x) {

    }

    public void scrolly(float y) {

    }


    public void zoomIn() {
        currentView.width -= 2;
        currentView.height -= 2;

        // Lower limit
        if(currentView.width < 5)
            currentView.width = 5;
        if(currentView.height < 5)
            currentView.height = 5;
    }

    public void zoomOut() {
        currentView.width += 2;
        currentView.height += 2;

        // Upper limit
        if(currentView.width > maxView.width)
            currentView.width = maxView.width;
        if(currentView.height > maxView.height)
            currentView.height = maxView.height;
    }
}
