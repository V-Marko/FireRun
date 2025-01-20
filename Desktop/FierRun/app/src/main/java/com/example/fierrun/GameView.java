package com.example.fierrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private GameThread gameThread;
    private Player player;
    private Animation animation;
    private Bitmap playerImage;
    private Paint textPaint;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        player = new Player(context);

        playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.stand_1);
        playerImage = Bitmap.createScaledBitmap(playerImage, 100, 100, false);

        animation = new Animation(player);

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                gameThread.setRunning(false);
                gameThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {
        player.update();
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        player.draw(canvas);

    }

    public void moveLeft() {
        if(player.width>=0){
            player.width = -player.width;
            player.headWidth = -player.headWidth;
            player.gunWidth = -player.gunWidth;


        }


        player.setMovingLeft(true);
        animation.startWalkingAnimation();
    }

    public void stopLeft() {
        player.setMovingLeft(false);
        if (!player.isMoving()) {
            animation.stopWalkingAnimation();
        }
    }

    public void moveRight() {
        if(player.width<=0){
            player.width = -player.width;
            player.headWidth = -player.headWidth;
            player.gunWidth = -player.gunWidth;

        }
        player.setMovingRight(true);
        animation.startWalkingAnimation();
    }

    public void stopRight() {

        player.setMovingRight(false);
        if (!player.isMoving()) {
            animation.stopWalkingAnimation();
        }
    }

    public void jump() {
        player.jump();
    }

    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private GameView gameView;
        private boolean running;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameView.update();
                        gameView.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }
    public void shoot() {
        player.shoot();
    }

}
