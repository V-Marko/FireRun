package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BoomScript {
    public float x;
    public float y;
    private float width;
    private float height;
    private float explosionWidth;
    private float explosionHeight;
    private float speed = 15f;
    private Bitmap bitmap;
    private Bitmap animationBitmap;
    private boolean isAnimating = false;
    private long animationStartTime;
    private final long animationDuration = 500;
    private long delayMs;
    private long startTime;
    private boolean hasStartedFalling = false;

    public BoomScript(float x, float y, float width, float height, float explosionWidth, float explosionHeight, long delayMs, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.explosionWidth = explosionWidth;
        this.explosionHeight = explosionHeight;
        this.delayMs = delayMs;
        this.startTime = System.currentTimeMillis();
        this.bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.boom),
                (int) width, (int) height, false
        );
        this.animationBitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.boom_animation),
                (int) explosionWidth, (int) explosionHeight, false
        );
    }

    public void update() {
        if (isAnimating) {
            if (System.currentTimeMillis() - animationStartTime >= animationDuration) {
                isAnimating = false;
                y = -height;
            }
        } else if (!hasStartedFalling) {
            // Check if delay has passed
            if (System.currentTimeMillis() - startTime >= delayMs) {
                hasStartedFalling = true;
            }
        } else {
            y += speed;
        }
    }

    public void draw(Canvas canvas) {
        if (isAnimating) {
            float explosionX = x + (width - explosionWidth) / 2;
            float explosionY = y + (height - explosionHeight) / 2;
            canvas.drawBitmap(animationBitmap, explosionX, explosionY, null);
        } else {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    public boolean checkCollisionWithPlayer(Player player) {
        return player.getX() < x + width &&
                player.getX() + player.getWidth() > x &&
                player.getY() < y + height &&
                player.getY() + player.getHeight() > y;
    }

    public boolean checkCollisionWithBullet(Bullet bullet) {
        return bullet.getX() < x + width &&
                bullet.getX() + bullet.getWidth() > x &&
                bullet.getY() < y + height &&
                bullet.getY() + bullet.getHeight() > y;
    }

    public boolean checkCollisionWithBlock(Block block) {
        return x < block.getX() + block.getWidth() &&
                x + width > block.getX() &&
                y < block.getY() + block.getHeight() &&
                y + height > block.getY();
    }

    public void startAnimation() {
        isAnimating = true;
        animationStartTime = System.currentTimeMillis();
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

    public float getExplosionWidth() {
        return explosionWidth;
    }

    public float getExplosionHeight() {
        return explosionHeight;
    }
}