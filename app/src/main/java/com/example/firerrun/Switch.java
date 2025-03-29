package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.Log;

public class Switch {
    public float x;
    public float y;
    public float width;
    public float height;
    public float BlockX;
    public float BlockY;
    public float BlockWidth;
    public float BlockHeight;
    private Bitmap switchImage;
    private Bitmap blockImage;
    private boolean isActivated = false;
    public int moveX;
    public int moveY;
    boolean isAnimating = false;
    public float targetX;
    public float targetY;
    private float moveSpeed = 5.0f;

    public Switch(float x, float y, float width, float height, float blockX, float blockY, float blockWidth, float blockHeight, int moveX, int moveY, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.BlockX = blockX;
        this.BlockY = blockY;
        this.BlockWidth = blockWidth;
        this.BlockHeight = blockHeight;
        this.moveX = moveX;
        this.moveY = moveY;

        switchImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.switch_button);
        switchImage = Bitmap.createScaledBitmap(switchImage, (int) width, (int) height, false);

        blockImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.block);
        blockImage = Bitmap.createScaledBitmap(blockImage, (int) BlockWidth, (int) BlockHeight, false);
    }

    public void draw(Canvas canvas) {
        if (switchImage != null) {
            if (isActivated) {
                Matrix matrix = new Matrix();
                matrix.setScale(-1, 1);
                matrix.postTranslate(x + width, y);
                canvas.drawBitmap(switchImage, matrix, null);
            } else {
                // Обычная отрисовка
                canvas.drawBitmap(switchImage, x, y, null);
            }
        }
        if (blockImage != null) {
            canvas.drawBitmap(blockImage, BlockX, BlockY, null);
        }
    }

    public boolean checkCollision(Player player) {
        return player.getX() + player.getWidth() > x &&
                player.getX() < x + width &&
                player.getY() + player.getHeight() > y &&
                player.getY() < y + height;
    }

    public void activate() {
        if (!isActivated && !isAnimating) {
            isActivated = true;
            isAnimating = true;
            targetX = BlockX + moveX;
            targetY = BlockY + moveY;
        }
    }

    public void update() {
        if (isAnimating) {
            if (BlockX < targetX) {
                BlockX += moveSpeed;
                if (BlockX >= targetX) BlockX = targetX;
            } else if (BlockX > targetX) {
                BlockX -= moveSpeed;
                if (BlockX <= targetX) BlockX = targetX;
            }

            if (BlockY < targetY) {
                BlockY += moveSpeed;
                if (BlockY >= targetY) BlockY = targetY;
            } else if (BlockY > targetY) {
                BlockY -= moveSpeed;
                if (BlockY <= targetY) BlockY = targetY;
            }

            if (BlockX == targetX && BlockY == targetY) {
                isAnimating = false;
            }
        }
    }

    public boolean isActivated() {
        return isActivated;
    }

    public float getBlockX() {
        return BlockX;
    }

    public float getBlockY() {
        return BlockY;
    }

    public float getBlockWidth() {
        return BlockWidth;
    }

    public float getBlockHeight() {
        return BlockHeight;
    }
}