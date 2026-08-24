package com.gdd.game;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.gdd.game.ecs.misc.Box;
import com.gdd.game.ecs.misc.Camera;

/*
 * Si occupa di disegnare il background della scena in modo compatibile con la telecamera.
 */
public class TiledBackgroundRenderer {

    private final Bitmap tileBitmap;
    private final float tileSize; // dimensione tile in metri (es. 1f)
    private final Paint paint;

    private final Rect dstPixel = new Rect();

    public TiledBackgroundRenderer(Bitmap tileBitmap, float tileSize) {
        this.tileBitmap = tileBitmap;
        this.tileSize = tileSize;
        this.paint = new Paint();
        paint.setFilterBitmap(false); // evita seams da bilinear filtering
        paint.setDither(false);
    }

    public void draw(Canvas canvas, Camera camera) {
        Box view = camera.cameraView;

        int minCol = (int) Math.floor(view.xmin / tileSize) - 1;
        int maxCol = (int) Math.ceil(view.xmax / tileSize) + 1;
        int minRow = (int) Math.floor(view.ymin / tileSize) - 1;
        int maxRow = (int) Math.ceil(view.ymax / tileSize) + 1;

        int cols = maxCol - minCol;
        int rows = maxRow - minRow;

        int[] xPix = new int[cols + 1];
        for (int i = 0; i <= cols; i++) {
            xPix[i] = Math.round(camera.toPixelsX((minCol + i) * tileSize));
        }
        int[] yPix = new int[rows + 1];
        for (int j = 0; j <= rows; j++) {
            yPix[j] = Math.round(camera.toPixelsY((minRow + j) * tileSize));
        }

        for (int j = 0; j < rows; j++) {
            int top = yPix[j];
            int bottom = yPix[j + 1];
            for (int i = 0; i < cols; i++) {
                dstPixel.set(xPix[i], top, xPix[i + 1], bottom);
                canvas.drawBitmap(tileBitmap, null, dstPixel, paint);
            }
        }
    }
}
