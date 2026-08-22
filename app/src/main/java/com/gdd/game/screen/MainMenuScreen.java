package com.gdd.game.screen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.Assets;
import com.gdd.game.Game;
import com.gdd.game.Settings;
import com.gdd.game.ui.Panel;
import com.gdd.game.ui.TextButton;
import com.gdd.game.ui.UIController;
import com.gdd.game.ui.WidgetGroup;

public class MainMenuScreen extends Screen {

    private UIController uiController;
    private Canvas canvas;
    private TouchHandler touchHandler;

    private final Paint paint;
    private RectF rectBG = new RectF();
    private RectF rectT = new RectF();
    private float fbWidth, fbHeight;

    public MainMenuScreen(Game game) {
        super(game);

        canvas = new Canvas(game.getFramebuffer());
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        fbWidth = game.getFramebuffer().getWidth();
        fbHeight = game.getFramebuffer().getHeight();
        rectBG.set(0, 0, fbWidth, fbHeight);
        float offsetY = 50;
        rectT.set( (fbWidth/2)-195, 0+offsetY, (fbWidth/2)+195, 78+offsetY);

        touchHandler = game.getTouchHandler();
        uiController = game.getUiController();

        initUI();
    }

    // ***************************************
    //  Game loop
    // ***************************************

    public void update(float deltaTime) {
        for (Input.TouchEvent event: touchHandler.getTouchEvents()) {
            uiController.processInput(event);
        }
    }

    public void render() {
        canvas.drawARGB(255, 200, 200, 200);

        canvas.drawBitmap(Assets.MAINBG_BITMAP, null, rectBG, paint);
        canvas.drawBitmap(Assets.MAINTITLE_BITMAP, null, rectT, paint);

        uiController.draw(canvas);
    }

    // ***************************************
    //  Android callbacks
    // ***************************************

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    // ***************************************
    //  UI
    // ***************************************

    private void initUI() {

        float screenW = game.getScreensize().width;
        float screenH = game.getScreensize().height;
        float fbufferW = Settings.fbufferWidth;
        float fbufferH = Settings.fbufferHeight;

        float buttonW = 250f;
        float buttonH = 100f;

        uiController.reset();

        WidgetGroup root = new Panel(0, 0, screenW, screenH);

        TextButton startButton = new TextButton(
                (fbufferW/2)-(buttonW/2), (fbufferH/2)-(buttonH/2),
                buttonW, buttonH);
        startButton.setText("START GAME");
        root.addChild(startButton);

        startButton.setOnClickListener(b -> {
            game.setScreen(new GameScreen(game));
        });

        uiController.setRoot(root);
        uiController.updateLayout();
    }
}
