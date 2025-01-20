package com.example.firerrun;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread gameThread;
    private Player player;
    private Bullet bullet;
    private Life Life;
    private Animation animation;
    private Bitmap playerImage;
    private Paint textPaint;
    private BadBox badBox;
    private long lastCollisionTime = 0;
    private final long collisionCooldown = 300;

    private Block block;
    private int[][] blocks = BlocksList.Blocks;
    private List<Bullet> bullets = new ArrayList<>();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        gameThread = new GameThread(getHolder(), this);
        player = new Player(context);
        Life = new Life();

        playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.stand_1);
        playerImage = Bitmap.createScaledBitmap(playerImage, 100, 100, false);

        animation = new Animation(player);

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);

        badBox = new BadBox(500, 500, BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_box));
        block = new Block(Block.width, Block.height, R.drawable.block, context);
        animation = new Animation(player);
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
        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (bullet.getX() > getWidth() || bullet.getX() < 0) {
                bullets.remove(i);
                continue;
            }

            if (badBox.checkCollisionBullet(bullet)) {
                bullets.remove(i);
                badBox.die();
                Log.i("info", "Bullet hit bad_box and bad_box is destroyed");
            }
        }

        player.update();
        badBox.update();

        if (badBox.checkCollisionPlayer(player)) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCollisionTime >= collisionCooldown) {
                lastCollisionTime = currentTime;
                Life.decreaseLife(20);
            }
        }
    }




    public void draw(Canvas canvas) {// TODO: GameView draw
        super.draw(canvas);

        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }
        player.draw(canvas);
        badBox.draw(canvas);
        Life.draw(canvas);
        block.draw(canvas);
    }


    public void moveLeft() {
        if(player.width>=0){
            player.width = -player.width;
            player.headWidth = -player.headWidth;
            player.gunWidth = -player.gunWidth;
            Bullet.width = -Bullet.width;

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
            Bullet.width = -Bullet.width;


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
//        player.shoot();
        Bullet newBullet = new Bullet((player.getX()+ player.getWidth()),
                                    (player.getY() + getHeight())/2-125,
                BitmapFactory.decodeResource(getResources(), R.drawable.bullet));
        bullets.add(newBullet);
    }

}