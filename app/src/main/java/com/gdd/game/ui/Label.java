package com.gdd.game.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Label extends Widget {

    public enum BackgroundMode { NONE, COLOR, BITMAP }
    public enum HAlign { LEFT, CENTER, RIGHT }
    public enum VAlign { TOP, CENTER, BOTTOM }

    protected String text;
    protected final Paint textPaint;
    protected Bitmap backgroundBitmap;
    protected final Paint backgroundPaint;
    protected final RectF dst = new RectF();

    protected BackgroundMode backgroundMode = BackgroundMode.NONE;

    // Default: centrato, nessun margine
    protected HAlign hAlign = HAlign.CENTER;
    protected VAlign vAlign = VAlign.CENTER;
    protected float marginH = 0f; // usato solo se hAlign != CENTER
    protected float marginV = 0f; // usato solo se vAlign != CENTER


    public Label(float x, float y, float width, float height)
    {
        super(x, y, width, height);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(0xFFFFFFFF);
        textPaint.setTextAlign(Paint.Align.CENTER);

        backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backgroundPaint.setColor(0xFF3E7BFA);

        autoFitTextSize(0.6f);
    }

    public Label(float x, float y, float width, float height, String text)
    {
        this(x, y, width, height);
        this.text = text;
    }

    // ***************************************
    //  Rendering
    // ***************************************

    @Override
    public void draw(Canvas canvas) {
        switch (backgroundMode) {
            case COLOR:
                canvas.drawRoundRect(absX, absY, absX + width, absY + height,
                        12f, 12f, backgroundPaint);
                break;
            case BITMAP:
                if (backgroundBitmap != null) {
                    dst.set(absX, absY, absX + width, absY + height);
                    canvas.drawBitmap(backgroundBitmap, null, dst, null);
                }
                break;
            case NONE:
                break;
        }

        if (text != null) {
            drawText(canvas);
        }
    }

    private void drawText(Canvas canvas) {
        float cx, cy;

        // --- orizzontale ---
        switch (hAlign) {
            case LEFT:
                textPaint.setTextAlign(Paint.Align.LEFT);
                cx = absX + marginH;
                break;
            case RIGHT:
                textPaint.setTextAlign(Paint.Align.RIGHT);
                cx = absX + width - marginH;
                break;
            case CENTER:
            default:
                textPaint.setTextAlign(Paint.Align.CENTER);
                cx = absX + width / 2f;
                break;
        }

        // --- verticale ---
        switch (vAlign) {
            case TOP:
                cy = absY + marginV - textPaint.ascent();
                break;
            case BOTTOM:
                cy = absY + height - marginV - textPaint.descent();
                break;
            case CENTER:
            default:
                cy = absY + height / 2f - (textPaint.ascent() + textPaint.descent()) / 2f;
                break;
        }

        canvas.drawText(text, cx, cy, textPaint);
    }

    // ********************************
    //  Getter / Setter
    // ********************************

    public void setTextAlignment(HAlign h, VAlign v, float marginH, float marginV) {
        this.hAlign = h;
        this.vAlign = v;
        this.marginH = marginH;
        this.marginV = marginV;
    }

    public void setBackgroundMode(BackgroundMode mode) { this.backgroundMode = mode; }

    public void setBackgroundBitmap(Bitmap bmp) {
        this.backgroundBitmap = bmp;
    }

    public void setBackgroundColor(int color) {
        backgroundPaint.setColor(color);
    }

    public void setText(String text) { this.text = text; }
    public String getText() { return text; }
    public void setTextSize(float size) { textPaint.setTextSize(size); }
    public void setTextColor(int color) { textPaint.setColor(color); }

    // ********************************
    //  Misc
    // ********************************

    /*
     * Modifica la dimensione del testo rispetto alla sua dimensione.
     * Richiede un valore nel range [0,1]
     *      Esempi: 0.4 = testo piccolo con margine,
     *              0.6 = default,
     *              0.8 = testo che riempie quasi tutto.
     */
    public void autoFitTextSize(float paddingRatio) {

        float limitingSize = Math.min(width, height);

        // applica un fattore per lasciare margine (es. 0.4-0.6)
        float targetSize = limitingSize * paddingRatio;

        textPaint.setTextSize(targetSize);

        // se il testo è lungo, verifica che stia anche in orizzontale
        if (text != null) {
            float textWidth = textPaint.measureText(text);
            float maxWidth = width * 0.9f; // 90% della larghezza disponibile

            if (textWidth > maxWidth) {
                float scale = maxWidth / textWidth;
                textPaint.setTextSize(targetSize * scale);
            }
        }
    }
}