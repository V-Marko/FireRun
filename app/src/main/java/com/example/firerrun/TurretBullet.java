package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class TurretBullet {
    public float x;
    private float y;
    private float vx;
    private float vy;
    private Bitmap bitmap;
    public static final int width = 50;
    public static final int height = 20;
    private float angle;

    public TurretBullet(float x, float y, Bitmap bitmap, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

        this.angle = (float) Math.toDegrees(Math.atan2(vy, vx));
    }

    public void update(float deltaTime) {
        x += vx * deltaTime;
        y += vy * deltaTime;
    }

    public void draw(Canvas canvas) {
        canvas.save();

        float pivotX = x + width / 2;
        float pivotY = y + height / 2;
        canvas.rotate(angle, pivotX, pivotY);

        canvas.drawBitmap(bitmap, x, y, null);

        canvas.restore();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getVx() {
        return vx;
    }

    public float getVy() {
        return vy;
    }

    public void reset(float x, float y, float vx, float vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
    }
}