package com.gdd.game;

public class CameraHandler {

    //TODO: migliorare la gestione della classe, insieme ai nomi

    public final Box worldCameraView; // camera
    private final Box maxView; // 100% view

    // Default: 100% (vedi tutta la scena)
    // ZoomIn max: 200%
    private float currentZoom = 1; // range [1=100%, 2=200%]
    private float maxZoom = 2;


    public CameraHandler(Box currentView) {
        this.maxView = new Box(currentView);
        this.worldCameraView = currentView;
    }


    /*
     * if value =  0.1 => zoom in  +10%
     * if value = -0.1 => zoom out -10%
     */
    public void zoom(float z) {
        currentZoom += z;

        // check min-max values
        if (currentZoom < 1) currentZoom = 1;
        else if (currentZoom > maxZoom) currentZoom = maxZoom;

        // calculate the new size
        worldCameraView.width = maxView.width / currentZoom;
        worldCameraView.height = maxView.height / currentZoom;

        // fix xmax-ymax
        worldCameraView.xmax = worldCameraView.xmin + worldCameraView.width;
        worldCameraView.ymax = worldCameraView.ymin + worldCameraView.height;

        //TODO: fix the zoom bug

        // siccome xmin-ymin non si modificano con gli zoom
        // si crea un bug se fai zoom al max, vai a dx e poi zoom out
        // esci fuori dallo schermo
    }

    public void scroll(float x, float y) {
        scrollx(x);
        scrolly(y);
    }

    public void scrollx(float x) {

        // case 1: scroll to left
        if(x < 0) {
            // esco dal bordo sinistro?
            if( (worldCameraView.xmin + x) < maxView.xmin) {
                // da gestire
            }
            else { // non esco dal bordo
                worldCameraView.xmin += x;
                worldCameraView.xmax += x;
            }
        }
        // case 2: scroll to right
        else if(x > 0) {
            // esco dal bordo destro?
            if( (worldCameraView.xmax + x) > maxView.xmax) {
                // da gestire
            }
            else { // non esco dal bordo
                worldCameraView.xmin += x;
                worldCameraView.xmax += x;
            }
        }
    }

    public void scrolly(float y) {

        // case 1: scroll to up
        if(y < 0) {
            // esco dal bordo superiore?
            if( (worldCameraView.ymin + y) < maxView.ymin) {
                // da gestire
            }
            else { // non esco dal bordo
                worldCameraView.ymin += y;
                worldCameraView.ymax += y;
            }
        }
        // case 2: scroll to down
        else if(y > 0) {
            // esco dal bordo inferiore?
            if( (worldCameraView.ymax + y) > maxView.ymax) {
                // da gestire
            }
            else { // non esco dal bordo
                worldCameraView.ymin += y;
                worldCameraView.ymax += y;
            }
        }
    }
}
