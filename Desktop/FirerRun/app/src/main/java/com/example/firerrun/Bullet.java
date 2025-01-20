package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import java.util.List;

public class Bullet {
    private float x, y;
    public static final float DEFAULT_SPEED = 20f;
    private float speed;
    private Bitmap image;
    private float PlayerWidth = Player.width;
    public static int width = 50;
    public static int height = 20;
    private boolean destroyed;

    public Bullet(float x, float y, Bitmap image) {
        this.x = x;
        this.y = y;
        this.image = Bitmap.createScaledBitmap(image, width, height, false);
        this.speed = DEFAULT_SPEED;
        this.destroyed = false;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void update() {
        if (PlayerWidth > 0) {
            x += speed;
        } else if (PlayerWidth < 0) {
            x -= speed;
        }

        if (x < 0 || x > 2000) {
            destroyed = true;
        }
    }

    public void draw(Canvas canvas) {
        if (!destroyed) {
            canvas.drawBitmap(image, x, y - 50, null);
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getImage() {
        return image;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
