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
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private Bitmap background;
    private GameThread gameThread;
    private Player player;
    private Life life;
    private Animation animation;
    private Bitmap playerImage;
    private Paint textPaint;
    private BadBox badBox;
    private long lastCollisionTime = 0;
    private final long collisionCooldown = 300;
    private PlayerController playerController;
    private SwitchCader switchCader;
    private int BlockID = 0; // block id

    private List<Block> blockList = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<BadBox> badBoxList = new ArrayList<>();


    List<FinishScript> finishScripts = new ArrayList<>();


    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        for (int[] blockData : BlocksList.Blocks) {
            Block block;
            switch (blockData[4]) {
                case 0:
                    BlockID = R.drawable.block;
                    break;
                case 1:
                    BlockID = R.drawable.block2;
                    break;
                case 2:
                    BlockID = R.drawable.oak_tree;
                    break;
                case 3:
                    BlockID = R.drawable.oak2;
                    break;
                case 4:
                    BlockID = R.drawable.barrel;
                    break;
                case 5:
                    BlockID = R.drawable.finish;
            }
            block = new Block(context, blockData[0], blockData[1], blockData[2], blockData[3], BlockID);
            blockList.add(block);
        }

        for (int[] badBoxData : BadBoxList.BadBoxs) {
            BadBox badBox = new BadBox(badBoxData[0], badBoxData[1], badBoxData[2], badBoxData[3],
                    BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_box));
            badBoxList.add(badBox);
        }

        player = new Player(context);
        player.setBlocks(blockList);

        gameThread = new GameThread(getHolder(), this);
        life = new Life(context);
        playerImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.stand_1);
        playerImage = Bitmap.createScaledBitmap(playerImage, 100, 100, false);
        animation = new Animation(player);
        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);

        playerController = new PlayerController(player, this);
        switchCader = new SwitchCader(player, this);

        finishScripts.add(new FinishScript(4500, 500, 200, 200, getContext()));


    }

    public static int getScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }

    public void shoot() {
        boolean isFacingLeft = Player.isFacingLeft;
        float bulletX = isFacingLeft ? (player.getX() - Bullet.width) : (player.getX() + player.getWidth());
        float bulletY = player.getY();

        Bullet newBullet = new Bullet(bulletX, bulletY, BitmapFactory.decodeResource(getResources(), R.drawable.bullet), isFacingLeft);
        bullets.add(newBullet);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int backgroundWidth = getWidth();
        int backgroundHeight = getHeight();

        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(originalBackground, backgroundWidth, backgroundHeight, true);

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
        switchCader.updateCader();

        for (FinishScript finishScript : finishScripts) {
            finishScript.x += 0;
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (bullet.getX() > getWidth() || bullet.getX() < 0) {
                bullets.remove(i);
                continue;
            }

            for (BadBox badBox : badBoxList) {
                if (badBox.checkCollisionBullet(bullet)) {
                    bullets.remove(i);
                    badBox.die();
                    break;
                }
            }

            for (BadBox badBox : badBoxList) {
                badBox.update();
            }

            for (Block block : blockList) {
                if (bullet.getX() < block.getX() + block.getWidth() &&
                        bullet.getX() + Bullet.width > block.getX() &&
                        bullet.getY() < block.getY() + block.getHeight() &&
                        bullet.getY() + Bullet.height > block.getY()) {
                    bullets.remove(i);
                    break;
                }
            }
        }

        if(player.getY() >=1000){
            life.decreaseLife(999_999_999);
        }
        for (BadBox badBox : badBoxList) {
            if (badBox.checkCollisionPlayer(player)) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastCollisionTime >= collisionCooldown) {
                    lastCollisionTime = currentTime;
                    life.decreaseLife(20);
                    Log.i("info", "Player collided with badBox and lost life");
                }
            }
        }

        boolean isOnBlock = false;
        for (Block block : blockList) {
            if (player.checkBlockCollision(blockList)) {
                isOnBlock = true;
                break;
            }
        }
        if(player.checkFlagCollision(finishScripts)){
            Log.i("Finishh", "finish");
            player.PlayerFinishAnimation();
        }

        if (!isOnBlock) {
            player.LandRestriction = 500;
        }

        player.update();
        for (BadBox badBox : badBoxList) {
            badBox.update();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }

        // Рисуем finishScripts
        for (FinishScript finishScript : finishScripts) {
            finishScript.draw(canvas);
        }

        for (Bullet bullet : bullets) {
            try {
                bullet.draw(canvas);
            } catch (Exception ignored) {}
        }

        player.draw(canvas);
        for (BadBox badBox : badBoxList) {
            badBox.draw(canvas);
        }
        life.draw(canvas);

        for (Block block : blockList) {
            block.draw(canvas);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Block> getBlockList() {
        return blockList;
    }

    public List<BadBox> getBadBoxList(){
        return badBoxList;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public BadBox getBadBox() {
        return badBox;
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
            long lastTime = System.nanoTime();
            double nsPerUpdate = 1_000_000_000.0 / 120.0; // 120 FPS
            double delta = 0;

            while (running) {
                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerUpdate;
                lastTime = now;

                while (delta >= 1) {
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
                    delta--;
                }
            }
        }
    }
}
