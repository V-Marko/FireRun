package com.example.firerrun;

import static kotlin.text.Typography.bullet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import androidx.collection.ArrayMap;

import java.util.ArrayList;
import java.util.List;
public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private boolean isInMenu = true;
    public int level = 1;
    private Bitmap background;
    private GameThread gameThread;
    private Player player;
    private Life life;
    private Animation animation;
    private Bitmap playerImage;
    private Paint textPaint;
    private BadBox badBox;
    private Bullet bullet;

    private List<SpeedGreenScript> speedGreenScripts = new ArrayList<>();


    private long lastCollisionTime = 0;
    private final long collisionCooldown = 300;
    private PlayerController playerController;
    private SwitchCader switchCader;
    private int BlockID = 0;

    public static boolean isMenuVisible = false;
    public static boolean isGamePaused = false;

    private Rect menuButton;

    private boolean isLoad = false;

    List<FinishScript> finishScripts = new ArrayList<>();
    private Rect[] levelButtons;

    private List<Block> blockList = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<BadBox> badBoxList = new ArrayList<>();


    public List<BlowingStone> blowingStones = new ArrayList<>();
    private long lastDropTime = 0;
    private final long dropInterval = 2000;

    private int loadingDotsCount = 0; // "Loading..." int
    private final Handler loadingHandler = new Handler(Looper.getMainLooper());
    private final Runnable loadingRunnable = new Runnable() {
        @Override
        public void run() {
            loadingDotsCount = (loadingDotsCount + 1) % 4;
            invalidate();
            loadingHandler.postDelayed(this, 500);
        }
    };
    public List<BoomScript> boomScripts = new ArrayList<>();
    public boolean isGamePaused() {
        return isGamePaused;
    }

    public void pauseGame() {
        isGamePaused = true;
        if (gameThread != null) {
            gameThread.setRunning(false);
        }
    }

    public void resumeGame() {
        isGamePaused = false;
        if (gameThread == null || !gameThread.isRunning()) {
            gameThread = new GameThread(getHolder(), this);
            gameThread.setRunning(true);
            gameThread.start();
        } else {
            gameThread.setRunning(true);
        }

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.btnLeft.setVisibility(View.VISIBLE);
                MainActivity.btnRight.setVisibility(View.VISIBLE);
                MainActivity.btnJump.setVisibility(View.VISIBLE);
                MainActivity.btnShoot.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadLevel(int level) {
        blockList.clear();
        badBoxList.clear();
        finishScripts.clear();
        blowingStones.clear();
        boomScripts.clear();

        if (level - 1 < BlocksList.Blocks.length) {
            for (int[] blockData : BlocksList.Blocks[level - 1]) {
                Block block;
                switch (blockData[4]) {
                    case 0: BlockID = R.drawable.block; break;
                    case 1: BlockID = R.drawable.block2; break;
                    case 2: BlockID = R.drawable.oak_tree; break;
                    case 3: BlockID = R.drawable.oak2; break;
                    case 4: BlockID = R.drawable.barrel; break;
                }
                block = new Block(getContext(), blockData[0], blockData[1], blockData[2], blockData[3], BlockID);
                blockList.add(block);
            }
        }

        if (level - 1 < SpeedGreenList.SpeedGreenList.length) {
            for (int[] speedGreenData : SpeedGreenList.SpeedGreenList[level - 1]) {
                SpeedGreenScript speedGreen = new SpeedGreenScript(
                        speedGreenData[0], speedGreenData[1], speedGreenData[2], speedGreenData[3],
                        BitmapFactory.decodeResource(getContext().getResources(), R.drawable.speed_green)
                );
                speedGreenScripts.add(speedGreen);
            }
        }

        if (level - 1 < BadBoxList.BadBoxs.length) {
            for (int[] badBoxData : BadBoxList.BadBoxs[level - 1]) {
                BadBox badBox = new BadBox(badBoxData[0], badBoxData[1], badBoxData[2], badBoxData[3],
                        BitmapFactory.decodeResource(getContext().getResources(), R.drawable.bad_box));
                badBoxList.add(badBox);
            }
        }

        if (level - 1 < FinishList.Finishes.length) {
            for (int[] finishData : FinishList.Finishes[level - 1]) {
                FinishScript finishScript = new FinishScript(finishData[0], finishData[1], finishData[2], finishData[3], getContext());
                finishScripts.add(finishScript);
            }
        }

        if (level - 1 < BlowingStoneList.BlowingStones.length) {
            for (int[] stoneData : BlowingStoneList.BlowingStones[level - 1]) {
                BlowingStone stone = new BlowingStone(
                        stoneData[0], -stoneData[3], 15f, stoneData[2], stoneData[3], getContext()
                );
                blowingStones.add(stone);
            }
        }

        if (level - 1 < BoomList.BoomList.length) {
            for (int[] boomData : BoomList.BoomList[level - 1]) {
                BoomScript boom = new BoomScript(
                        boomData[0], -boomData[3],
                        boomData[2], boomData[3], 250, 250, getContext()
                );
                boomScripts.add(boom);
            }
        }

        player.setBlocks(blockList);
        player.resetPosition();
        this.level = level;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        isMenuVisible = true;
        isInMenu = true;

        player = new Player(context);

        loadLevel(level);

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
        switchCader = new SwitchCader(player, this, getSpeedGreenScripts());
        Bitmap speedGreenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.speed_green);
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
        if (gameThread == null) {
            gameThread = new GameThread(getHolder(), this);
        }
        Bitmap originalBackground = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        background = Bitmap.createScaledBitmap(originalBackground, getWidth(), getHeight(), true);

        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        if (gameThread != null) {
            gameThread.setRunning(false);
            while (retry) {
                try {
                    gameThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void update() {
        if (isGamePaused) return;

        switchCader.updateCader();

        long currentTime = System.currentTimeMillis();

        for (BlowingStone stone : blowingStones) {
            stone.update();
            if (stone.getY() > getHeight()) {
                stone.y = -stone.getHeight();
            }
            if (stone.checkCollisionWithPlayer(player)) {
                life.decreaseLife(40);
                stone.y = -stone.getHeight();
            }
        }

        for (BoomScript boom : boomScripts) {
            boom.update();

            if (boom.getY() > getHeight()) {
                boom.y = -boom.getHeight();
            }

            if (boom.checkCollisionWithPlayer(player)) {
                life.decreaseLife(100);
                boom.y = -boom.getHeight();

                boom.startAnimation();
            }


            for (int i = bullets.size() - 1; i >= 0; i--) {
                Bullet bullet = bullets.get(i);
                if (boom.checkCollisionWithBullet(bullet)) {
                    Log.d("BoomCollision", "Bullet hit Boom at (" + boom.x + ", " + boom.y + ")");

                    boom.startAnimation();
                    boom.y = bullet.getY() - boom.getHeight();

                    bullets.remove(i);
                    break;
                }
            }




            for (Block block : blockList) {
                if (boom.checkCollisionWithBlock(block)) {
                    boom.startAnimation();
                    boom.y = block.getY() - boom.getHeight();
                    break;
                }
            }
        }

        for (FinishScript finishScript : finishScripts) {
            finishScript.x += 0;
        }
        if (currentTime - lastDropTime >= dropInterval) {
            lastDropTime = currentTime;
        }

        boolean isTouchingSpeedGreen = false;
        for (SpeedGreenScript speedGreen : speedGreenScripts) {
            if (speedGreen.checkCollisionPlayer(player)) {
                isTouchingSpeedGreen = true;
                break;
            }
        }

        if (isTouchingSpeedGreen) {
            player.speed = 35f;
        } else {
            player.speed = 15f;
        }

        for (BadBox badBox : badBoxList) {
            badBox.update();
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

        if (player.getY() >= 1000) {
            life.decreaseLife(999_999_999);
        }

        for (BadBox badBox : badBoxList) {
            if (badBox.checkCollisionPlayer(player)) {
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

        if (player.checkFlagCollision(finishScripts)) {
            Log.i("Finishh", "finish");
            player.PlayerFinishAnimation();
            SwitchCader.index = 1;
            goToMenu();
        }
        if (!isOnBlock) {
            player.LandRestriction = 500;
        }

        player.update();
    }
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (isMenuVisible) {
            MenuLevelsFunction(canvas);
            return;
        }

        if (isLoad) {
            LoadFunction(canvas, 20);
            return;
        }

        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }

        for (BlowingStone stone : blowingStones) {
            stone.draw(canvas);
        }

        for (BoomScript boom : boomScripts) {
            boom.draw(canvas);
        }

        for (SpeedGreenScript speedGreen : speedGreenScripts) {
            speedGreen.draw(canvas);
        }

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

        if (isGamePaused) {
            PauseFunction(canvas);
        }
    }

    public Player getPlayer() {
        return player;
    }

    public List<Block> getBlockList() {
        return blockList;
    }

    public List<BadBox> getBadBoxList() {
        return badBoxList;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public BadBox getBadBox() {
        return badBox;
    }

    public int getLevel() {
        return level;
    }

    class GameThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private GameView gameView;
        private volatile boolean running;

        public GameThread(SurfaceHolder surfaceHolder, GameView gameView) {
            this.surfaceHolder = surfaceHolder;
            this.gameView = gameView;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public boolean isRunning() {
            return running;
        }

        @Override
        public void run() {
            long lastTime = System.nanoTime();
            double nsPerUpdate = 1_000_000_000.0 / 60.0; // 60 FPS
            double delta = 0;

            while (running) {
                if (!surfaceHolder.getSurface().isValid()) {
                    continue;
                }

                long now = System.nanoTime();
                delta += (now - lastTime) / nsPerUpdate;
                lastTime = now;

                while (delta >= 1 && running) {
                    Canvas canvas = null;
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        if (canvas == null) continue;

                        synchronized (surfaceHolder) {
                            gameView.update();
                            gameView.draw(canvas);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                    delta--;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void PauseFunction(Canvas canvas) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Game Paused", getWidth() / 2, getHeight() / 4, textPaint);

        Paint buttonPaint = new Paint();
        buttonPaint.setColor(Color.DKGRAY);

        Paint buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(80);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        int buttonWidth = getWidth() / 2;
        int buttonHeight = 150;
        int centerX = getWidth() / 2;
        int startY = getHeight() / 2;

        menuButton = new Rect(centerX - buttonWidth / 2, startY, centerX + buttonWidth / 2, startY + buttonHeight);
        canvas.drawRect(menuButton, buttonPaint);
        canvas.drawText("Menu", centerX, startY + 100, buttonTextPaint);
    }

    public void MenuLevelsFunction(Canvas canvas) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.argb(180, 0, 0, 0));
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(100);
        textPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("Select Level", getWidth() / 2, getHeight() / 6, textPaint);

        Paint buttonPaint = new Paint();
        buttonPaint.setColor(Color.DKGRAY);

        Paint buttonTextPaint = new Paint();
        buttonTextPaint.setColor(Color.WHITE);
        buttonTextPaint.setTextSize(60);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        int buttonWidth = getWidth() / 4;
        int buttonHeight = 120;
        int padding = 20;

        int columns = 4;
        int rows = 3;

        int totalWidth = columns * (buttonWidth + padding * 2) - padding * 2;
        int totalHeight = rows * (buttonHeight + padding) - padding;

        int startX = (getWidth() - totalWidth) / 2;
        int startY = (getHeight() - totalHeight) / 2;

        if (levelButtons == null || levelButtons.length != 12) {
            levelButtons = new Rect[12];
        }

        for (int i = 0; i < 12; i++) {
            int row = i / columns;
            int col = i % columns;
            int x = startX + col * (buttonWidth + padding * 2);
            int y = startY + row * (buttonHeight + padding);

            levelButtons[i] = new Rect(x, y, x + buttonWidth, y + buttonHeight);
            canvas.drawRect(levelButtons[i], buttonPaint);
            canvas.drawText("Level " + (i + 1), x + buttonWidth / 2, y + buttonHeight / 2 + 20, buttonTextPaint);
        }
    }

    public void startLoadingAnimation() {
        loadingHandler.post(loadingRunnable);
    }

    public void stopLoadingAnimation() {
        loadingHandler.removeCallbacks(loadingRunnable);
    }

    public void LoadFunction(Canvas canvas, int time) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(80);
        textPaint.setTextAlign(Paint.Align.CENTER);

        String loadingText = "Loading" + getDots(loadingDotsCount);
        canvas.drawText(loadingText, getWidth() / 2, getHeight() / 2, textPaint);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 500);
    }

    private String getDots(int count) {
        StringBuilder dots = new StringBuilder();
        for (int i = 0; i < count; i++) {
            dots.append(".");
        }
        return dots.toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (isGamePaused) {
                if (menuButton != null && menuButton.contains((int) touchX, (int) touchY)) {
                    Log.i("Menu", "MENUUUU");
                    goToMenu();
                    return true;
                }
            }

            if (isInMenu && levelButtons != null) {
                for (int i = 0; i < levelButtons.length; i++) {
                    if (levelButtons[i].contains((int) touchX, (int) touchY)) {
                        int selectedLevel = i + 1;
                        Log.i("level", "LvL " + selectedLevel);
                        isMenuVisible = false;
                        isInMenu = false;
                        isLoad = true;
                        startLoadingAnimation();
                        new LoadLevelTask().execute(selectedLevel);
                        return true;
                    }
                }
            } else if (!isGamePaused && !isLoad) {
                if (touchX < getWidth() / 2) {
                    playerController.moveLeft();
                } else {
                    playerController.moveRight();
                    shoot();
                }
                return true;
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            if (!isInMenu && !isGamePaused && !isLoad) {
                playerController.stopLeft();
                playerController.stopRight();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private class LoadLevelTask extends AsyncTask<Integer, Void, Void> {
        @Override
        protected Void doInBackground(Integer... levels) {
            int level = levels[0];
            loadLevel(level);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            player.setBlocks(blockList);
            isLoad = false;
            isInMenu = false;
            stopLoadingAnimation();
            resumeGame();

            ((Activity) getContext()).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MainActivity.btnLeft.setVisibility(View.VISIBLE);
                    MainActivity.btnRight.setVisibility(View.VISIBLE);
                    MainActivity.btnJump.setVisibility(View.VISIBLE);
                    MainActivity.btnShoot.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void goToMenu() {
        isInMenu = true;
        isMenuVisible = true;
        isGamePaused = false;
        isLoad = false;
        bullets.clear();
        player.resetPosition();
        life.resetLife();
        resumeGame();

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MainActivity.btnLeft.setVisibility(View.VISIBLE);
                MainActivity.btnRight.setVisibility(View.VISIBLE);
                MainActivity.btnJump.setVisibility(View.VISIBLE);
                MainActivity.btnShoot.setVisibility(View.VISIBLE);
            }
        });
    }

    public List<SpeedGreenScript> getSpeedGreenScripts() {return speedGreenScripts;}
}