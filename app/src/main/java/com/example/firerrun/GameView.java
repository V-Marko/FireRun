package com.example.firerrun;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaPlayer;
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

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    public static int headState = 0;

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
    private Rect personButton;
    private boolean isPersonScreenVisible = false;
    private Rect backButton;

    private List<SpeedGreenScript> speedGreenScripts = new ArrayList<>();

    private Rect leftButton;
    private Rect rightButton;
    private Bitmap currentHeadBitmap;
    private Bitmap bodyBitmap;
    private Bitmap gunBitmap;

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
    public List<Coolest> coolestList = new ArrayList<>();
    public List<BlowingStone> blowingStones = new ArrayList<>();
    private List<BadBoxBotScript> badBoxBots = new ArrayList<>();
    List<ilusoryblocks> illusoryBlocks = new ArrayList<>();
    List<Switch> switches = new ArrayList<>();

    public List<WallUpDownScript> wallUpDownScripts = new ArrayList<>();
    private Bitmap wallImage;
    public List<BlockMoveScript> blockMoveScripts = new ArrayList<>();
    private Bitmap blockImage;

    private long lastDropTime = 0;
    private final long dropInterval = 2000;

    private int loadingDotsCount = 0;

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
    public List<SmallRunBoom> smallRunBooms = new ArrayList<>();

    List<TurentScript> turrets = new ArrayList<>();

    public List<TurretBullet> turretBullets = new ArrayList<>();
    private boolean isNearSwitch;

    // Переменные для отслеживания двойного касания
    private long lastTouchTime = 0;
    private final long doubleTapThreshold = 300; // Максимальный интервал между касаниями в миллисекундах
    private boolean isDoubleTapProcessed = false; // Флаг для предотвращения повторной обработки

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
        coolestList.clear();
        smallRunBooms.clear();
        speedGreenScripts.clear();
        badBoxBots.clear();
        wallUpDownScripts.clear();
        blockMoveScripts.clear();
        illusoryBlocks.clear();
        turrets.clear();
        turretBullets.clear();
        switches.clear();

        if (level - 1 >= 0 && level - 1 < BlockMoveList.BlockMove.length) {
            for (int[] blockData : BlockMoveList.BlockMove[level - 1]) {
                BlockMoveScript blockMove = new BlockMoveScript(
                        blockData[0], blockData[1], blockData[2], blockData[3], blockData[4],
                        blockData[5], blockData[6], blockData[7], blockData[8],
                        blockData[9], blockData[10], blockData[11], blockData[12],
                        blockImage
                );
                blockMoveScripts.add(blockMove);
            }
        }

        if (level - 1 >= 0 && level - 1 < SwitchList.switches.length) {
            for (int[] switchData : SwitchList.switches[level - 1]) {
                Switch switchObj = new Switch(
                        switchData[0], switchData[1], switchData[2], switchData[3],
                        switchData[4], switchData[5], switchData[6], switchData[7],
                        switchData[8], switchData[9],
                        getContext()
                );
                switches.add(switchObj);
            }
        }

        if (level - 1 >= 0 && level - 1 < BlocksList.Blocks.length) {
            for (int[] blockData : BlocksList.Blocks[level - 1]) {
                int blockId;
                switch (blockData[4]) {
                    case 0: blockId = R.drawable.block; break;
                    case 1: blockId = R.drawable.block2; break;
                    case 2: blockId = R.drawable.oak_tree; break;
                    case 3: blockId = R.drawable.oak2; break;
                    case 4: blockId = R.drawable.barrel; break;
                    default: blockId = R.drawable.block; break;
                }
                Block block = new Block(getContext(), blockData[0], blockData[1], blockData[2], blockData[3], blockId);
                blockList.add(block);
            }
        }

        if (level - 1 >= 0 && level - 1 < wallUpDownList.wallUDList.length) {
            for (int[] wallData : wallUpDownList.wallUDList[level - 1]) {
                WallUpDownScript wall = new WallUpDownScript(
                        wallData[0], wallData[1], wallData[2], wallData[3],
                        wallData[4], wallData[5], wallData[6]
                );
                wallUpDownScripts.add(wall);
            }
        }

        if (level - 1 >= 0 && level - 1 < BoomList.BoomList.length) {
            for (int[] boomData : BoomList.BoomList[level - 1]) {
                BoomScript boom = new BoomScript(
                        boomData[0], boomData[1], boomData[2], boomData[3],
                        boomData[2] * 1.5f, boomData[3] * 1.5f, boomData[4], getContext()
                );
                boomScripts.add(boom);
            }
        }

        if (level - 1 < BadBoxBotList.BadBoxs.length) {
            for (int[] botData : BadBoxBotList.BadBoxs[level - 1]) {
                BadBoxBotScript bot = new BadBoxBotScript(botData[0], botData[1], botData[2], botData[3], botData[4], getContext());
                bot.setBlocks(blockList);
                badBoxBots.add(bot);
            }
        }

        if (level - 1 < SmallRunBoomList.SmallRunBoomList.length) {
            for (int[] boomData : SmallRunBoomList.SmallRunBoomList[level - 1]) {
                SmallRunBoom smallBoom = new SmallRunBoom(
                        boomData[0], boomData[1], boomData[2], boomData[3],
                        boomData[4], boomData[5], boomData[6]
                );
                smallRunBooms.add(smallBoom);
            }
        }

        if (level - 1 >= 0 && level - 1 < ilusoryblocksList.ilusoryblocks.length) {
            for (int[] illusoryData : ilusoryblocksList.ilusoryblocks[level - 1]) {
                Bitmap illusoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block);
                switch (illusoryData[4]) {
                    case 0: illusoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block); break;
                    case 1: illusoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.block2); break;
                    case 2: illusoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oak_tree); break;
                    case 3: illusoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.oak2); break;
                    case 4: illusoryBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.barrel); break;
                }
                illusoryBitmap = Bitmap.createScaledBitmap(illusoryBitmap, illusoryData[2], illusoryData[3], false);
                ilusoryblocks illusoryBlock = new ilusoryblocks(
                        illusoryData[0], illusoryData[1], illusoryData[2], illusoryData[3],
                        illusoryBitmap, getContext()
                );
                illusoryBlocks.add(illusoryBlock);
            }
        }

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

        if (level - 1 < SmallRunBoomList.SmallRunBoomList.length) {
            for (int[] boomData : SmallRunBoomList.SmallRunBoomList[level - 1]) {
                SmallRunBoom smallBoom = new SmallRunBoom(
                        boomData[0], boomData[1], boomData[2], boomData[3],
                        boomData[4], boomData[5], boomData[6]
                );
                smallRunBooms.add(smallBoom);
            }
        }

        if (level - 1 >= 0 && level - 1 < CoolestList.CoolestList.length) {
            for (int[] coolestData : CoolestList.CoolestList[level - 1]) {
                Coolest coolest = new Coolest(
                        coolestData[0], coolestData[1], coolestData[2], coolestData[3], coolestData[4]
                );
                coolestList.add(coolest);
            }
        }

        if (level - 1 >= 0 && level - 1 < TurretList.Turrets.length) {
            for (int[] turretData : TurretList.Turrets[level - 1]) {
                TurentScript turret = new TurentScript(
                        turretData[0], turretData[1], turretData[2], turretData[3], turretData[4], turretData[5], turretData[6], 3, this
                );
                turrets.add(turret);
            }
        }

        player.setBlocks(blockList);
        player.setBlockMoveScripts(blockMoveScripts);
        player.setSwitches(switches);

        player.resetPosition();
        this.level = level;
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);

        isMenuVisible = true;
        isInMenu = true;

        player = new Player(context, this);
        player.setSwitches(switches);

        loadLevel(level);

        player.setBlocks(blockList);
        player.setBlockMoveScripts(blockMoveScripts);

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

        wallImage = BitmapFactory.decodeResource(getResources(), R.drawable.wall);
        blockImage = BitmapFactory.decodeResource(getResources(), R.drawable.block);

        float scaleFactor = 1.0f;
        bodyBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.person_stop1);
        if (bodyBitmap == null) {
            Log.e("GameView", "Failed to load person_stop1");
        } else {
            bodyBitmap = Bitmap.createScaledBitmap(bodyBitmap, (int) (394 * scaleFactor), (int) (437 * scaleFactor), true);
        }

        updatePlayerHead();

        gunBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.person_gun);
        if (gunBitmap == null) {
            Log.e("GameView", "Failed to load person_gun");
        } else {
            gunBitmap = Bitmap.createScaledBitmap(gunBitmap, (int) (523 * scaleFactor), (int) (191 * scaleFactor), true);
        }
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

        MainActivity.shoot_voice.start();




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
        if (wallImage != null && !wallImage.isRecycled()) {
            wallImage.recycle();
            wallImage = null;
        }
    }

    public void update() {
        if (isGamePaused) return;

        long currentTime = System.currentTimeMillis();

        float originalSpeed = player.speed;

        switchCader.updateCader();

        for (WallUpDownScript wall : wallUpDownScripts) {
            wall.update();
        }
        for (BlockMoveScript blockMove : blockMoveScripts) {
            blockMove.update();
        }

        for (WallUpDownScript wall : wallUpDownScripts) {
            if (checkCollisionWithWall(wall, player)) {
                if (currentTime - lastCollisionTime >= collisionCooldown) {
                    lastCollisionTime = currentTime;
                    life.decreaseLife(30);
                }
            }
        }

        for (int i = badBoxBots.size() - 1; i >= 0; i--) {
            BadBoxBotScript bot = badBoxBots.get(i);
            bot.update(player, blockList);

            if (bot.checkCollisionWithPlayer(player)) {
                if (currentTime - lastCollisionTime >= collisionCooldown) {
                    lastCollisionTime = currentTime;
                    life.decreaseLife(20);
                    Log.i("Collision", "Player hit by BadBoxBot");
                }
            }
        }

        boolean isNearSwitch = false;
        for (Switch switchObj : switches) {
            switchObj.update();
            if (switchObj.checkCollision(player)) {
                isNearSwitch = true;
                break;
            }
        }

        final boolean shouldShowButton = isNearSwitch;
        ((Activity) getContext()).runOnUiThread(() -> {
            MainActivity.btnUse.setVisibility(shouldShowButton ? View.VISIBLE : View.GONE);
        });

        boolean isOnBlock = player.checkBlockCollision(blockList, blockMoveScripts, switches);

        if (!isOnBlock && !player.jumping) {
            player.y += player.jumpSpeed;
            player.jumpSpeed += player.gravity;
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (bullet.getX() > getWidth() || bullet.getX() < 0) {
                bullets.remove(i);
                continue;
            }

            boolean bulletRemoved = false;
            for (int j = turrets.size() - 1; j >= 0; j--) {
                TurentScript turret = turrets.get(j);
                if (turret.checkCollisionBullet(bullet)) {
                    turret.takeDamage(1);
                    bullets.remove(i);
                    bulletRemoved = true;
                    if (!turret.isAlive()) {
                        turrets.remove(j);
                    }
                    break;
                }
            }

            if (bulletRemoved) {
                continue;
            }

            for (int j = badBoxBots.size() - 1; j >= 0; j--) {
                BadBoxBotScript bot = badBoxBots.get(j);
                if (checkCollisionWithBullet(bot, bullet)) {
                    bullets.remove(i);
                    badBoxBots.remove(j);
                    break;
                }
            }
        }

        for (Block block : blockList) {
            block.update();
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

        for (int i = smallRunBooms.size() - 1; i >= 0; i--) {
            SmallRunBoom smallBoom = smallRunBooms.get(i);
            smallBoom.update(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2);

            float boomLeft = smallBoom.x - smallBoom.width / 2;
            float boomRight = smallBoom.x + smallBoom.width / 2;
            float boomTop = smallBoom.y - smallBoom.height / 2;
            float boomBottom = smallBoom.y + smallBoom.height / 2;

            if (player.getX() + player.getWidth() > boomLeft &&
                    player.getX() < boomRight &&
                    player.getY() + player.getHeight() > boomTop &&
                    player.getY() < boomBottom) {
                if (currentTime - lastCollisionTime >= collisionCooldown) {
                    lastCollisionTime = currentTime;
                    life.decreaseLife(15);
                }
            }
        }

        for (int i = bullets.size() - 1; i >= 0; i--) {
            Bullet bullet = bullets.get(i);
            bullet.update();

            if (bullet.getX() > getWidth() || bullet.getX() < 0) {
                bullets.remove(i);
                continue;
            }

            boolean bulletRemoved = false;
            for (int j = smallRunBooms.size() - 1; j >= 0; j--) {
                SmallRunBoom smallBoom = smallRunBooms.get(j);
                RectF diagonal1Bounds = smallBoom.getDiagonal1Bounds();

                float bulletLeft = bullet.getX();
                float bulletRight = bullet.getX() + Bullet.width;
                float bulletTop = bullet.getY();
                float bulletBottom = bullet.getY() + Bullet.height;

                if (bulletRight > diagonal1Bounds.left && bulletLeft < diagonal1Bounds.right &&
                        bulletBottom > diagonal1Bounds.top && bulletTop < diagonal1Bounds.bottom) {
                    bullets.remove(i);
                    smallRunBooms.remove(j);
                    bulletRemoved = true;
                    break;
                }
            }

            if (bulletRemoved) {
                continue;
            }

            for (int j = smallRunBooms.size() - 1; j >= 0; j--) {
                SmallRunBoom smallBoom = smallRunBooms.get(j);
                float boomLeft = smallBoom.x - smallBoom.width / 2;
                float boomRight = smallBoom.x + smallBoom.width / 2;
                float boomTop = smallBoom.y - smallBoom.height / 2;
                float boomBottom = smallBoom.y + smallBoom.height / 2;

                float bulletLeft = bullet.getX();
                float bulletRight = bullet.getX() + Bullet.width;
                float bulletTop = bullet.getY();
                float bulletBottom = bullet.getY() + Bullet.height;

                if (bulletRight > boomLeft && bulletLeft < boomRight &&
                        bulletBottom > boomTop && bulletTop < boomBottom) {
                    smallRunBooms.remove(j);
                }
            }

            Bullet bulletStillAlive = bullets.get(i);
            for (BadBox badBox : badBoxList) {
                if (badBox.checkCollisionBullet(bulletStillAlive)) {
                    bullets.remove(i);
                    badBox.die();
                    break;
                }
            }

            for (Block block : blockList) {
                if (bulletStillAlive.getX() < block.getX() + block.getWidth() &&
                        bulletStillAlive.getX() + Bullet.width > block.getX() &&
                        bulletStillAlive.getY() < block.getY() + block.getHeight() &&
                        bulletStillAlive.getY() + Bullet.height > block.getY()) {
                    bullets.remove(i);
                    break;
                }
            }

            for (Coolest coolest : coolestList) {
                if (coolest.checkCollisionWithBullet(bulletStillAlive)) {
                    bullets.remove(i);
                    break;
                }
            }
        }

        for (TurentScript turret : turrets) {
            turret.update(1.0f / 60.0f, player);
        }

        for (int i = turretBullets.size() - 1; i >= 0; i--) {
            TurretBullet turretBullet = turretBullets.get(i);
            turretBullet.update(1.0f / 60.0f);

            if (turretBullet.getX() > getWidth() || turretBullet.getX() < 0) {
                turretBullets.remove(i);
                continue;
            }

            if (turretBullet.getX() + TurretBullet.width > player.getX() &&
                    turretBullet.getX() < player.getX() + player.getWidth() &&
                    turretBullet.getY() + TurretBullet.height > player.getY() &&
                    turretBullet.getY() < player.getY() + player.getHeight()) {
                if (currentTime - lastCollisionTime >= collisionCooldown) {
                    lastCollisionTime = currentTime;
                    life.decreaseLife(25);
                    Log.i("Collision", "Player hit by turret bullet");
                }
                turretBullets.remove(i);
                continue;
            }

            for (Block block : blockList) {
                if (turretBullet.getX() < block.getX() + block.getWidth() &&
                        turretBullet.getX() + TurretBullet.width > block.getX() &&
                        turretBullet.getY() < block.getY() + block.getHeight() &&
                        turretBullet.getY() + TurretBullet.height > block.getY()) {
                    turretBullets.remove(i);
                    break;
                }
            }
        }

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

        long lastCoolestCollisionTime = 0;
        final long coolestCollisionCooldown = 500;
        for (Coolest coolest : coolestList) {
            coolest.update();
            if (coolest.checkCollisionWithPlayer(player)) {
                if (currentTime - lastCoolestCollisionTime >= coolestCollisionCooldown) {
                    life.decreaseLife(10);
                    lastCoolestCollisionTime = currentTime;
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
        player.speed = isTouchingSpeedGreen ? 35f : originalSpeed;

        for (BadBox badBox : badBoxList) {
            badBox.update();
            if (badBox.checkCollisionPlayer(player)) {
                if (currentTime - lastCollisionTime >= collisionCooldown) {
                    lastCollisionTime = currentTime;
                    life.decreaseLife(20);
                    Log.i("info", "Player collided with BadBox and lost life");
                }
            }
        }

        if (player.getY() >= 1000) {
            life.decreaseLife(999_999_999);
        }

        if (!isOnBlock) {
            player.LandRestriction = 500;
        }

        if (player.checkFlagCollision(finishScripts)) {
            player.PlayerFinishAnimation();
            SwitchCader.index = 1;
            life.resetLife();
            goToMenu();
        }

        player.update();
    }

    private boolean checkCollisionWithWall(WallUpDownScript wall, Player player) {
        return player.getX() + player.getWidth() > wall.getX() &&
                player.getX() < wall.getX() + wall.getWidth() &&
                player.getY() + player.getHeight() > wall.getY() &&
                player.getY() < wall.getY() + wall.getHeight();
    }

    private boolean checkCollisionWithPlayer(BadBoxBotScript bot) {
        return player.getX() + player.getWidth() > bot.getX() &&
                player.getX() < bot.getX() + bot.getWidth() &&
                player.getY() + player.getHeight() > bot.getY() &&
                player.getY() < bot.getY() + bot.getHeight();
    }

    private boolean checkCollisionWithBullet(BadBoxBotScript bot, Bullet bullet) {
        return bullet.getX() + Bullet.width > bot.getX() &&
                bullet.getX() < bot.getX() + bot.getWidth() &&
                bullet.getY() + Bullet.height > bot.getY() &&
                bullet.getY() < bot.getY() + bot.getHeight();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (isMenuVisible) {
            MenuLevelsFunction(canvas);
            return;
        }

        if (isLoad) {
            LoadFunction(canvas);
            return;
        }

        if (isPersonScreenVisible) {
            drawPersonScreen(canvas);
            return;
        }

        if (background != null) {
            canvas.drawBitmap(background, 0, 0, null);
        }

        for (BlockMoveScript blockMove : blockMoveScripts) {
            blockMove.draw(canvas);
        }

        for (WallUpDownScript wall : wallUpDownScripts) {
            float wallX = wall.getX();
            float wallY = wall.getY();
            float wallWidth = wall.getWidth();
            float wallHeight = wall.getHeight();
            float imageWidth = wallImage.getWidth();
            float imageHeight = wallImage.getHeight();

            float currentX = wallX;
            while (currentX < wallX + wallWidth) {
                float tileWidth = Math.min(imageWidth, wallX + wallWidth - currentX);

                float currentY = wallY;
                while (currentY < wallY + wallHeight) {
                    float tileHeight = Math.min(imageHeight, wallY + wallHeight - currentY);

                    RectF tileRect = new RectF(
                            currentX,
                            currentY,
                            currentX + tileWidth,
                            currentY + tileHeight
                    );
                    canvas.drawBitmap(wallImage, null, tileRect, null);

                    currentY += imageHeight;
                }

                currentX += imageWidth;
            }
        }

        for (Switch switchObj : switches) {
            switchObj.draw(canvas);
        }

        for (ilusoryblocks illusory : illusoryBlocks) {
            illusory.draw(canvas);
        }

        for (SmallRunBoom smallBoom : smallRunBooms) {
            smallBoom.draw(canvas);
        }
        for (BadBoxBotScript bot : badBoxBots) {
            bot.draw(canvas);
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

        for (TurretBullet turretBullet : turretBullets) {
            try {
                turretBullet.draw(canvas);
            } catch (Exception ignored) {}
        }

        for (TurentScript turret : turrets) {
            turret.draw(canvas);
        }

        player.draw(canvas);

        for (BadBox badBox : badBoxList) {
            badBox.draw(canvas);
        }

        for (Coolest coolest : coolestList) {
            coolest.draw(canvas);
        }

        life.draw(canvas);

        for (Block block : blockList) {
            block.draw(canvas);
        }

        if (isGamePaused && !isPersonScreenVisible) {
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

    public List<BadBoxBotScript> getBadBoxBot() {
        return badBoxBots;
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
            double nsPerUpdate = 1_000_000_000.0 / 60.0;
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

        personButton = new Rect(centerX - buttonWidth / 2, startY + buttonHeight + 50,
                centerX + buttonWidth / 2, startY + buttonHeight * 2 + 50);
        canvas.drawRect(personButton, buttonPaint);
        canvas.drawText("Person", centerX, startY + buttonHeight + 150, buttonTextPaint);
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

    private void drawPersonScreen(Canvas canvas) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, getWidth(), getHeight(), backgroundPaint);

        int screenWidth = getWidth();
        int screenHeight = getHeight();

        if (bodyBitmap != null) {
            int bodyWidth = bodyBitmap.getWidth();
            int bodyHeight = bodyBitmap.getHeight();
            float bodyX = (screenWidth - bodyWidth) / 2f;
            float bodyY = screenHeight - bodyHeight - 50f;
            canvas.drawBitmap(bodyBitmap, bodyX, bodyY, null);

            if (currentHeadBitmap != null) {
                int headWidth = currentHeadBitmap.getWidth();
                int headHeight = currentHeadBitmap.getHeight();
                float headX = (screenWidth - headWidth) / 2f;
                float headY = bodyY - headHeight + 50f;
                canvas.drawBitmap(currentHeadBitmap, headX, headY, null);
            }

            if (gunBitmap != null) {
                int gunWidth = gunBitmap.getWidth();
                int gunHeight = gunBitmap.getHeight();
                float gunX = (screenWidth - gunWidth) / 2f + 90;
                float gunY = bodyY + (bodyHeight / 2f) - (gunHeight / 2f) - 170f;
                canvas.drawBitmap(gunBitmap, gunX, gunY, null);
            }

            Paint buttonPaint = new Paint();
            buttonPaint.setColor(Color.DKGRAY);

            Paint buttonTextPaint = new Paint();
            buttonTextPaint.setColor(Color.WHITE);
            buttonTextPaint.setTextSize(80);
            buttonTextPaint.setTextAlign(Paint.Align.CENTER);

            int buttonWidth = getWidth() / 6;
            int buttonHeight = 100;

            backButton = new Rect(50, 50, 50 + buttonWidth, 50 + buttonHeight);
            canvas.drawRect(backButton, buttonPaint);
            canvas.drawText("Back", 50 + buttonWidth / 2, 50 + buttonHeight / 2 + 25, buttonTextPaint);

            leftButton = new Rect((int) (bodyX - buttonWidth - 20), (int) (bodyY + bodyHeight / 2 - buttonHeight / 2),
                    (int) (bodyX - 20), (int) (bodyY + bodyHeight / 2 + buttonHeight / 2));
            canvas.drawRect(leftButton, buttonPaint);
            canvas.drawText("Left", leftButton.centerX(), leftButton.centerY() + 25, buttonTextPaint);

            rightButton = new Rect((int) (bodyX + bodyWidth + 20), (int) (bodyY + bodyHeight / 2 - buttonHeight / 2),
                    (int) (bodyX + bodyWidth + buttonWidth + 20), (int) (bodyY + bodyHeight / 2 + buttonHeight / 2));
            canvas.drawRect(rightButton, buttonPaint);
            canvas.drawText("Right", rightButton.centerX(), rightButton.centerY() + 25, buttonTextPaint);
        }
    }

    public void startLoadingAnimation() {
        loadingHandler.post(loadingRunnable);
    }

    public void stopLoadingAnimation() {
        loadingHandler.removeCallbacks(loadingRunnable);
    }

    public void LoadFunction(Canvas canvas) {
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
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTouchTime < doubleTapThreshold && !isInMenu && !isGamePaused && !isLoad && !isPersonScreenVisible && !isDoubleTapProcessed) {
                // Обнаружено двойное касание во время игры
                headState = (headState == 0) ? 1 : 0;
                updatePlayerHead();
                Log.i("GameView", "Head changed during gameplay to state: " + headState);
                isDoubleTapProcessed = true;
                lastTouchTime = 0;
                invalidate();
                return true;
            }
            lastTouchTime = currentTime;
            isDoubleTapProcessed = false;

            if (isPersonScreenVisible) {
                if (backButton != null && backButton.contains((int) touchX, (int) touchY)) {
                    isPersonScreenVisible = false;
                    // Обновляем голову игрока перед возвратом в игру
                    updatePlayerHead();
                    player.updateHeadImage(); // Убедимся, что Player тоже обновляет свою голову
                    invalidate();
                    return true;
                }
                if (leftButton != null && leftButton.contains((int) touchX, (int) touchY)) {
                    headState = (headState == 0) ? 1 : 0;
                    updatePlayerHead();
                    if (currentHeadBitmap != null) {
                        SurfaceHolder holder = getHolder();
                        Canvas canvas = null;
                        try {
                            canvas = holder.lockCanvas();
                            if (canvas != null) {
                                drawPersonScreen(canvas);
                            }
                        } finally {
                            if (canvas != null) {
                                holder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    return true;
                }
                if (rightButton != null && rightButton.contains((int) touchX, (int) touchY)) {
                    Log.i("PersonSwitch", "Right");
                    headState = (headState == 0) ? 1 : 0;
                    updatePlayerHead();
                    if (currentHeadBitmap != null) {
                        SurfaceHolder holder = getHolder();
                        Canvas canvas = null;
                        try {
                            canvas = holder.lockCanvas();
                            if (canvas != null) {
                                drawPersonScreen(canvas);
                            }
                        } finally {
                            if (canvas != null) {
                                holder.unlockCanvasAndPost(canvas);
                            }
                        }
                    }
                    return true;
                }
            }

            if (isGamePaused && !isPersonScreenVisible) {
                if (menuButton != null && menuButton.contains((int) touchX, (int) touchY)) {
                    Log.i("Menu", "MENUUUU");
                    goToMenu();
                    return true;
                }
                if (personButton != null && personButton.contains((int) touchX, (int) touchY)) {
                    isPersonScreenVisible = true;
                    SurfaceHolder holder = getHolder();
                    Canvas canvas = null;
                    try {
                        canvas = holder.lockCanvas();
                        if (canvas != null) {
                            drawPersonScreen(canvas);
                        }
                    } finally {
                        if (canvas != null) {
                            holder.unlockCanvasAndPost(canvas);
                        }
                    }
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

    private void updatePlayerHead() {
        player.updateHeadImage(); // Убедимся, что Player обновляет свою голову
        float scaleFactor = 1.0f;
        currentHeadBitmap = player.headImage; // Используем голову из Player
        if (currentHeadBitmap == null) {
            Log.e("GameView", "Failed to update player head");
        } else {
            currentHeadBitmap = Bitmap.createScaledBitmap(currentHeadBitmap, (int) (425 * scaleFactor), (int) (421 * scaleFactor), true);
        }
        invalidate(); // Перерисовываем экран
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
        turretBullets.clear();
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

    public List<SpeedGreenScript> getSpeedGreenScripts() {
        return speedGreenScripts;
    }

    public List<SmallRunBoom> getSmallRunBooms() {
        return smallRunBooms;
    }

    public void addTurretBullet(TurretBullet bullet) {
        turretBullets.add(bullet);
    }

    public List<Switch> getSwitches() {
        return switches;
    }
}