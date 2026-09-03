package com.gdd.game.screen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.badlogic.androidgames.framework.Input;
import com.badlogic.androidgames.framework.impl.TouchHandler;
import com.gdd.game.Assets;
import com.gdd.game.Game;
import com.gdd.game.Settings;
import com.gdd.game.ui.ImageButton;
import com.gdd.game.ui.Panel;
import com.gdd.game.ui.TextButton;
import com.gdd.game.ui.UIController;
import com.gdd.game.ui.WidgetGroup;

public class MainMenuScreen extends Screen {

    private UIController uiController;
    private TouchHandler touchHandler;

    private float fbWidth, fbHeight;
    private Canvas canvas;
    private final Paint paint;
    private RectF rectBG = new RectF();

    public MainMenuScreen(Game game) {
        super(game);

        canvas = new Canvas(game.getFramebuffer());
        paint = new Paint(Paint.FILTER_BITMAP_FLAG);

        fbWidth = game.getFramebuffer().getWidth();
        fbHeight = game.getFramebuffer().getHeight();
        rectBG.set(0, 0, fbWidth, fbHeight);

        touchHandler = game.getTouchHandler();
        uiController = game.getUiController();

        uiController.reset();
        initUI();
        uiController.updateLayout();
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
        canvas.drawBitmap(Assets.MAIN_MENU_BG, null, rectBG, paint);
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

        WidgetGroup root = new Panel(0, 0, fbWidth, fbHeight);

        float buttonW = 200f;
        float buttonH = 100f;

        ImageButton startButton = new ImageButton(
                (fbWidth/2)-(buttonW/2), (fbHeight/2)-(buttonH/2),
                buttonW, buttonH);
        startButton.setIdleBitmap(Assets.STARTGAME_BUTTON_IDLE);
        startButton.setPressedBitmap(Assets.STARTGAME_BUTTON_PRESSED);
        root.addChild(startButton);

        startButton.setOnClickListener(b -> {
            Assets.click.play(1);
            game.setScreen(new GameScreen(game));
        });

        uiController.setRoot(root);
    }
}
