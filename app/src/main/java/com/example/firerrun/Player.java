package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Paint;
import android.graphics.Color;

public class Player {
    public boolean TouchRedBlockTOplayerLEFT = false;
    public boolean TouchRedBlockTOplayerRIGHT = false;

    public static float x;
    private static float y;
    private float speed;


    private boolean movingLeft, movingRight, jumping;
    private boolean isIdle;
    private float jumpSpeed = 10;
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

    //    private static final long ANIMATION_DELAY = 300;
    private List<Block> blocks;
    public float LandRestriction = 500;
    public static List<Bullet> bullets;

    public static boolean isFacingLeft;

    private float initialJumpSpeed = -9.5f;
    private float gravity = 0.38f;
    private float maxJumpHeight = 4.75f;//max Jump height

    private float currentJumpHeight = 0f;
    private Animation animation;
    public Player(Context context) {
        this.context = context;
        this.x = 100;
        this.y = 100;

        bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_stop1);
        bodyImage = Bitmap.createScaledBitmap(bodyImage, width, height, false);

        headImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_head);
        headImage = Bitmap.createScaledBitmap(headImage, headWidth, headHeight, false);

        gunImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_gun);
        gunImage = Bitmap.createScaledBitmap(gunImage, gunWidth, gunHeight, false);

        bulletImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);
        bullets = new ArrayList<>();
        isIdle = true;
        blocks = new ArrayList<>();

        animation = new Animation(this);
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

        // Проверка столкновений по горизонтали
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
                Log.i("Jump", "JUMP ended");
            }
        }

        if (!isOnGround() && !jumping) {
            newY += jumpSpeed;
            jumpSpeed += gravity;
        }

        // Проверка столкновений по вертикали
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

        // Проверка, находится ли игрок на блоке
        boolean isOnBlock = checkBlockCollision(blocks);

        if (!isOnBlock && !jumping) {
            y += jumpSpeed;
            jumpSpeed += gravity;
        }


        Log.i("X", "x = " + getX());
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


    public void jump() {
        if (isOnGround() && !jumping) {
            jumping = true;
            jumpSpeed = initialJumpSpeed;
            currentJumpHeight = 0f;
            Log.i("Jump", "JUMP initiated");
        }
    }

    public boolean isOnGround() {
        boolean groundCondition = (y >= LandRestriction);
        boolean blockCondition = checkBlockCollision(blocks);

        return groundCondition || blockCondition;
    }

    public boolean checkBlockCollision(List<Block> blocks) {
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

        return isColliding;
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

        canvas.drawBitmap(currentBodyImage, x, y, null); // body
        canvas.drawBitmap(currentHeadImage, x + (width - headWidth) / 2 - 15, y - headHeight + 20, null); // head
        canvas.drawBitmap(currentGunImage, x, y, null); // gun

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

    public int getVelocityX() {
        if (movingLeft) {
            return (int) -speed;
        } else if (movingRight) {
            return (int) speed;
        } else {
            return 0;
        }
    }
}