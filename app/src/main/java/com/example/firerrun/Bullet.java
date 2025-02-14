package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

public class Bullet {
    public float x;
    private float y;
    public static final float DEFAULT_SPEED = 40f;
    private float speed;
    private Bitmap image;
    public static int width = 50;
    public static int height = 20;

    private boolean isFacingLeft;

    public Bullet(float x, float y, Bitmap image, boolean isFacingLeft) {
        this.x = x;
        this.y = y;
        this.image = Bitmap.createScaledBitmap(image, width, height, false);
        this.speed = DEFAULT_SPEED;
        this.isFacingLeft = isFacingLeft;
    }

    public void update() {
        if (isFacingLeft) {
            x -= speed;
        } else {
            x += speed;
        }
    }

    public void draw(Canvas canvas) {
        if (isFacingLeft) {
            Matrix matrix = new Matrix();
            matrix.preScale(-1, 1);
            Bitmap mirroredImage = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, false);
            canvas.drawBitmap(mirroredImage, x, y, null);
        } else {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isFacingLeft() {
        return isFacingLeft;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}