package com.gdd.game;

public class CameraHandler {

    //TODO: migliorare la gestione della classe, insieme ai nomi

    // IN METRI
    //   XMIN = -10, XMAX = 10, YMIN = -15, YMAX = 15;

    // 100% è di default => vedo tutto
    // 200% vedo le cose grandi il doppio

    public final Box currentView; // camera
    private final Box maxBox; // 100% view

    private float currentZoom = 1; // range [1=100%, 2=200%]
    private float maxZoom = 2;


    public CameraHandler(Box currentView) {
        this.maxBox = new Box(currentView);
        this.currentView = currentView;
    }


    /*
     * // cerca di passare sempre multipli di 0.1 per ora
     * if value =  0.1 => zoom in  +10%
     * if value = -0.1 => zoom out -10%
     */
    public void zoom(float z) {
        currentZoom += z;

        // check min-max values
        if (currentZoom < 1) currentZoom = 1;
        else if (currentZoom > maxZoom) currentZoom = maxZoom;

        // calculate the new size
        currentView.width = maxBox.width / currentZoom;
        currentView.height = maxBox.height / currentZoom;

        // fix xmax-ymax
        currentView.xmax = currentView.xmin + currentView.width;
        currentView.ymax = currentView.ymin + currentView.height;

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
            if( (currentView.xmin + x) < maxBox.xmin) {
                // da gestire
            }
            else { // non esco dal bordo
                currentView.xmin += x;
                currentView.xmax += x;
            }
        }
        // case 2: scroll to right
        else if(x > 0) {
            // esco dal bordo destro?
            if( (currentView.xmax + x) > maxBox.xmax) {
                // da gestire
            }
            else { // non esco dal bordo
                currentView.xmin += x;
                currentView.xmax += x;
            }
        }
    }

    public void scrolly(float y) {

        // case 1: scroll to up
        if(y < 0) {
            // esco dal bordo superiore?
            if( (currentView.ymin + y) < maxBox.ymin) {
                // da gestire
            }
            else { // non esco dal bordo
                currentView.ymin += y;
                currentView.ymax += y;
            }
        }
        // case 2: scroll to down
        else if(y > 0) {
            // esco dal bordo inferiore?
            if( (currentView.ymax + y) > maxBox.ymax) {
                // da gestire
            }
            else { // non esco dal bordo
                currentView.ymin += y;
                currentView.ymax += y;
            }
        }
    }
}
