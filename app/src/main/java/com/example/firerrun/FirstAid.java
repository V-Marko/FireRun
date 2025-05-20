package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;

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



        return collision;
    }

    public void collect() {
        isActive = false;
    }

    public boolean isActive() {
        return isActive;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public float getX() {
        return x;
    }
}