package com.example.firerrun;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private GameView gameView;
    private PlayerController playerController;

    public boolean TouchRedBlockTOplayerLEFT = false;
    public boolean TouchRedBlockTOplayerRIGHT = false;

    public static float x;
    public static float y;
    public float speed;

    private boolean movingLeft;
    private boolean movingRight;
    boolean jumping;
    private boolean isIdle;
    public float jumpSpeed = 10;
    public Bitmap bodyImage;
    public Bitmap headImage;
    public Bitmap gunImage;
    public static Bitmap bulletImage;
    private Context context;

    public static int width = 150;
    public static int height = 150;
    public int headWidth = 150;
    public int headHeight = 150;
    public int gunWidth = 150;
    public int gunHeight = 90;

    private List<Block> blocks;
    private List<BlockMoveScript> blockMoveScripts;
    private List<Switch> switches;
    public float LandRestriction = 500;
    public static List<Bullet> bullets;

    public static boolean isFacingLeft;

    private float initialJumpSpeed = -9.5f;
    float gravity = 0.38f;
    private float maxJumpHeight = 4.75f;

    private float currentJumpHeight = 0f;
    private Animation animation;
    private float moveSpeed;

    private int headStill = R.drawable.person_head_2;

    public Player(Context context, GameView gameView) {
        this.context = context;
        this.gameView = gameView;
        this.x = 100;
        this.y = 100;

        bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_stop1);
        bodyImage = Bitmap.createScaledBitmap(bodyImage, width, height, false);

        updateHeadImage();

        gunImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_gun);
        gunImage = Bitmap.createScaledBitmap(gunImage, gunWidth, gunHeight, false);

        bulletImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
        bullets = new ArrayList<>();
        isIdle = true;
        blocks = new ArrayList<>();
        blockMoveScripts = new ArrayList<>();
        switches = new ArrayList<>();

        animation = new Animation(this);

        this.playerController = new PlayerController(this, gameView);
    }

    public void updateHeadImage() {
        float scaleFactor = 1.0f;

        if (GameView.headState == 0) {
            headStill = R.drawable.person_head;
            headImage = BitmapFactory.decodeResource(context.getResources(), headStill);
            headImage = Bitmap.createScaledBitmap(headImage, headWidth, headHeight, false);
            Log.i("Player", "Head updated to person_head");
        } else if (GameView.headState == 1) {
            headStill = R.drawable.person_head_2;
            Bitmap original = BitmapFactory.decodeResource(context.getResources(), headStill);
            float originalWidth = 381f;
            float originalHeight = 382f;
            float targetWidth = 425f * scaleFactor;
            float scale = targetWidth / originalWidth;
            float newHeight = originalHeight * scale;
            headImage = Bitmap.createScaledBitmap(original, (int) targetWidth, (int) newHeight, true);
            Log.i("Player", "Head updated to person_head_2");
        }
    }

    public void update() {
        float newX = x;
        float newY = y;

        if (movingLeft) {
            newX -= speed;
            isFacingLeft = true;
        }

        if (movingRight) {
            newX += speed;
            isFacingLeft = false;
        }

        boolean canMove = true;
        for (Block block : blocks) {
            if (isCollidingWithBlock(newX, y, block)) {
                canMove = false;
                break;
            }
        }

        if (canMove) {
            x = newX;
        }

        if (jumping) {
            newY += jumpSpeed;
            jumpSpeed += gravity;
            currentJumpHeight += Math.abs(jumpSpeed);

            if (currentJumpHeight >= maxJumpHeight || newY >= LandRestriction) {
                jumping = false;
                newY = LandRestriction;
            }
        }

        if (!isOnGround() && !jumping) {
            newY += jumpSpeed;
            jumpSpeed += gravity;
        }

        canMove = true;
        for (Block block : blocks) {
            if (isCollidingWithBlock(x, newY, block)) {
                canMove = false;
                break;
            }
        }

        if (canMove) {
            y = newY;
        }

        boolean isOnBlock = checkBlockCollision(blocks, blockMoveScripts, switches);

        if (!isOnBlock && !jumping) {
            y += jumpSpeed;
            jumpSpeed += gravity;
        }
    }

    private boolean isCollidingWithBlock(float newX, float newY, Block block) {
        float playerLeft = newX;
        float playerRight = newX + width;
        float playerTop = newY;
        float playerBottom = newY + height;

        float blockLeft = block.getX();
        float blockRight = block.getX() + block.getWidth();
        float blockTop = block.getY();
        float blockBottom = block.getY() + block.getHeight();

        return playerRight > blockLeft && playerLeft < blockRight &&
                playerBottom > blockTop && playerTop < blockBottom;
    }

    private boolean isCollidingWithBlockMove(float newX, float newY, BlockMoveScript blockMove) {
        float playerLeft = newX;
        float playerRight = newX + width;
        float playerTop = newY;
        float playerBottom = newY + height;

        float blockLeft = blockMove.getX();
        float blockRight = blockMove.getX() + blockMove.getWidth();
        float blockTop = blockMove.getY();
        float blockBottom = blockMove.getY() + blockMove.getHeight();

        return playerRight > blockLeft && playerLeft < blockRight &&
                playerBottom > blockTop && playerTop < blockBottom;
    }

    private boolean isCollidingWithSwitchBlock(float newX, float newY, Switch switchObj) {
        float playerLeft = newX;
        float playerRight = newX + width;
        float playerTop = newY;
        float playerBottom = newY + height;

        float blockLeft = switchObj.getBlockX();
        float blockRight = switchObj.getBlockX() + switchObj.getBlockWidth();
        float blockTop = switchObj.getBlockY();
        float blockBottom = switchObj.getBlockY() + switchObj.getBlockHeight();

        return playerRight > blockLeft && playerLeft < blockRight &&
                playerBottom > blockTop && playerTop < blockBottom;
    }

    public void jump() {
        if (isOnGround() && !jumping) {
            jumping = true;
            jumpSpeed = initialJumpSpeed;
            currentJumpHeight = 0f;
        }
    }

    public boolean isOnGround() {
        boolean blockCondition = checkBlockCollision(blocks, blockMoveScripts, switches);
        return blockCondition;
    }

    public boolean checkBlockCollision(List<Block> blocks, List<BlockMoveScript> blockMoveScripts, List<Switch> switches) {
        boolean isColliding = false;
        LandRestriction = Player.y - Player.height;

        for (Block block : blocks) {
            if (Math.abs(block.getX() - x) < 200 && Math.abs(block.getY() - y) < 200) {
                boolean xOverlap = (x < block.getX() + block.getWidth()) &&
                        (x + width > block.getX());

                boolean yOverlap = (y + height >= block.getY()) &&
                        (y + height <= block.getY() + block.getHeight());

                if (xOverlap && yOverlap) {
                    isColliding = true;

                    if (y + height <= block.getY() + block.getHeight() && jumpSpeed >= 0) {
                        y = block.getY() - height;
                        LandRestriction = (int) block.getY();
                        jumpSpeed = 0;
                    }
                }
            }
        }

        for (BlockMoveScript blockMove : blockMoveScripts) {
            blockMove.update();
        }
        for (BlockMoveScript blockMove : blockMoveScripts) {
            if (Math.abs(blockMove.getX() - x) < 200 && Math.abs(blockMove.getY() - y) < 200) {
                boolean xOverlap = (x < blockMove.getX() + blockMove.getWidth()) &&
                        (x + width > blockMove.getX());

                boolean yOverlap = (y + height >= blockMove.getY()) &&
                        (y + height <= blockMove.getY() + blockMove.getHeight());

                if (xOverlap && yOverlap) {
                    isColliding = true;

                    if (y + height <= blockMove.getY() + blockMove.getHeight() && jumpSpeed >= 0) {
                        y = blockMove.getY() - height;
                        LandRestriction = (int) blockMove.getY();
                        jumpSpeed = 0;
                        x += blockMove.speed * (blockMove.movingRight ? 1 : -1);
                    }
                }
            }
        }

        for (Switch switchObj : switches) {
            if (Math.abs(switchObj.getBlockX() - x) < 200 && Math.abs(switchObj.getBlockY() - y) < 200) {
                boolean xOverlap = (x < switchObj.getBlockX() + switchObj.getBlockWidth()) &&
                        (x + width > switchObj.getBlockX());

                boolean yOverlap = (y + height >= switchObj.getBlockY()) &&
                        (y + height <= switchObj.getBlockY() + switchObj.getBlockHeight());

                if (xOverlap && yOverlap) {
                    isColliding = true;

                    if (y + height <= switchObj.getBlockY() + switchObj.getBlockHeight() && jumpSpeed >= 0) {
                        y = switchObj.getBlockY() - height;
                        LandRestriction = (int) switchObj.getBlockY();
                        jumpSpeed = 0;
                        if (switchObj.isAnimating) {
                            if (switchObj.getBlockX() < switchObj.targetX) {
                                x += moveSpeed;
                            } else if (switchObj.getBlockX() > switchObj.targetX) {
                                x -= moveSpeed;
                            }
                            if (switchObj.getBlockY() < switchObj.targetY) {
                                y += moveSpeed;
                            } else if (switchObj.getBlockY() > switchObj.targetY) {
                                y -= moveSpeed;
                            }
                        }
                    }
                }
            }
        }

        return isColliding;
    }

    public boolean checkFlagCollision(List<FinishScript> finishScripts) {
        for (FinishScript finishScript : finishScripts) {
            boolean xOverlap = (x < finishScript.getX() + finishScript.getWidth()) &&
                    (x + width > finishScript.getX());

            boolean yOverlap = (y + height >= finishScript.getY()) &&
                    (y <= finishScript.getY() + finishScript.getHeight());

            if (xOverlap && yOverlap) {
                return true;
            }
        }
        return false;
    }

    public void draw(Canvas canvas) {
        Bitmap currentBodyImage = bodyImage;
        Bitmap currentHeadImage = headImage;
        Bitmap currentGunImage = gunImage;

        Matrix matrix = new Matrix();
        if (isFacingLeft) {
            matrix.preScale(-1, 1);
            currentBodyImage = Bitmap.createBitmap(bodyImage, 0, 0, bodyImage.getWidth(), bodyImage.getHeight(), matrix, false);
            currentHeadImage = Bitmap.createBitmap(headImage, 0, 0, headImage.getWidth(), headImage.getHeight(), matrix, false);
            currentGunImage = Bitmap.createBitmap(gunImage, 0, 0, gunImage.getWidth(), gunImage.getHeight(), matrix, false);
        }

        canvas.drawBitmap(currentBodyImage, x, y, null);
        canvas.drawBitmap(currentHeadImage, x + (width - headWidth) / 2 - 15, y - headHeight + 20, null);
        canvas.drawBitmap(currentGunImage, x, y, null);

        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }

        Paint redPaint = new Paint();
        redPaint.setColor(Color.RED);
        redPaint.setStyle(Paint.Style.FILL);

        float rectWidth = 0;
        float rectHeight = 150;
        float offsetY = 50;
        float offsetX = 50;

        float leftRectCenterX = x - offsetX;
        float leftRectLeft = leftRectCenterX - rectWidth / 2;
        float leftRectTop = y + height / 2 - rectHeight / 2 - offsetY;
        float leftRectRight = leftRectCenterX + rectWidth / 2;
        float leftRectBottom = y + height / 2 + rectHeight / 2 - offsetY;

        canvas.drawRect(leftRectLeft, leftRectTop, leftRectRight, leftRectBottom, redPaint);

        float rightRectCenterY = y + height / 2;
        float rightRectCenterX = x + width + offsetX;
        float rightRectLeft = rightRectCenterX - rectWidth / 2;
        float rightRectTop = rightRectCenterY - rectHeight / 2 - offsetY;
        float rightRectRight = rightRectCenterX + rectWidth / 2;
        float rightRectBottom = rightRectCenterY + rectHeight / 2 - offsetY;

        canvas.drawRect(rightRectLeft, rightRectTop, rightRectRight, rightRectBottom, redPaint);

        for (Block block : blocks) {
            if (isColliding(rightRectLeft, rightRectTop, rightRectRight, rightRectBottom, block)) {
                TouchRedBlockTOplayerLEFT = true;
                speed = 0;
                Log.i("redPaint", "Left Block touch. Speed set to: " + speed);
            } else {
                TouchRedBlockTOplayerLEFT = false;
            }

            if (isColliding(leftRectLeft, leftRectTop, leftRectRight, leftRectBottom, block)) {
                TouchRedBlockTOplayerRIGHT = true;
                speed = 0;
                Log.i("redPaint", "Right Block touch. Speed set to: " + speed);
            } else {
                TouchRedBlockTOplayerRIGHT = false;
            }
        }

        if (!TouchRedBlockTOplayerLEFT && !TouchRedBlockTOplayerRIGHT) {
            speed = 15;
        }
    }

    private boolean isColliding(float rectLeft, float rectTop, float rectRight, float rectBottom, Block block) {
        float blockLeft = block.getX();
        float blockTop = block.getY();
        float blockRight = block.getX() + block.getWidth();
        float blockBottom = block.getY() + block.getHeight();

        return rectRight > blockLeft && rectLeft < blockRight &&
                rectBottom > blockTop && rectTop < blockBottom;
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
        if (movingLeft) {
            animation.startWalkingAnimation();
        } else if (!movingRight) {
            animation.stopWalkingAnimation();
        }
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
        if (movingRight) {
            animation.startWalkingAnimation();
        } else if (!movingLeft) {
            animation.stopWalkingAnimation();
        }
    }

    public void setBlocks(List<Block> blockList) {
        this.blocks = blockList;
    }

    public void setBlockMoveScripts(List<BlockMoveScript> blockMoveScripts) {
        this.blockMoveScripts = blockMoveScripts;
    }

    public void setSwitches(List<Switch> switches) {
        this.switches = switches;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public Context getContext() {
        return context;
    }

    public void setPlayerImage(Bitmap walkFrame, Bitmap headImage, Bitmap gunImage) {
        this.bodyImage = Bitmap.createScaledBitmap(walkFrame, width, height, false);
        this.headImage = Bitmap.createScaledBitmap(headImage, headWidth, headHeight, false);
        this.gunImage = Bitmap.createScaledBitmap(gunImage, gunWidth, gunHeight, false);
    }

    public void setX(float newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

    public int getVelocityX() {
        if (movingLeft) {
            return (int) -speed;
        } else if (movingRight) {
            return (int) speed;
        } else {
            return 0;
        }
    }

    public boolean checkSwitchCollision(List<Switch> switches) {
        for (Switch switchObj : switches) {
            if (switchObj.checkCollision(this)) {
                return true;
            }
        }
        return false;
    }

    public void PlayerFinishAnimation() {
        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageButton btnLeft = MainActivity.btnLeft;
                ImageButton btnRight = MainActivity.btnRight;
                ImageButton btnJump = MainActivity.btnJump;
                ImageButton btnShoot = MainActivity.btnShoot;

                btnLeft.setVisibility(View.GONE);
                btnRight.setVisibility(View.GONE);
                btnJump.setVisibility(View.GONE);
                btnShoot.setVisibility(View.GONE);
            }
        });
    }

    public void resetPosition() {
        x = 100;
        y = 100;
        jumpSpeed = 0;
        jumping = false;
        currentJumpHeight = 0f;
        LandRestriction = 500;
        movingLeft = false;
        movingRight = false;
        isFacingLeft = false;
        bullets.clear();
        updateHeadImage();
    }
}