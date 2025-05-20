package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.Log;

public class FirstAid {
    float x;
    private float y;
    private int width, height;
    private Bitmap bitmap;
    private boolean isActive;
    private RectF bounds;

    public FirstAid(float x, float y, int width, int height, Context context, Bitmap cachedBitmap) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.isActive = true;
        this.bitmap = Bitmap.createScaledBitmap(cachedBitmap, width, height, false);
        this.bounds = new RectF(x, y, x + width, y + height);
        Log.d("FirstAid", "Initialized at x=" + x + ", y=" + y + ", width=" + width + ", height=" + height);
    }

    public void draw(Canvas canvas) {
        if (isActive) {
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }

    public boolean checkCollisionWithPlayer(Player player) {
        if (!isActive) return false;

        RectF playerBounds = new RectF(
                player.getX(), player.getY(),
                player.getX() + player.getWidth(),
                player.getY() + player.getHeight()
        );

        RectF firstAidBounds = new RectF(x, y, x + width, y + height);

        boolean collision = RectF.intersects(playerBounds, firstAidBounds);

        if (collision) {
            Log.i("FirstAid", "Collision detected with player!");
        }

        return collision;
    }

    public void collect() {
        isActive = false;
        Log.i("FirstAid", "FirstAid collected at (" + x + "," + y + ")");
    }

    public boolean isActive() {
        Log.d("FirstAid", "Checking if active: " + isActive);
        return isActive;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }
}