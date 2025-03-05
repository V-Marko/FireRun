package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class SpeedGreenScript {
    public float x;
    public float y;
    public int width, height;
    private Bitmap image;

    public SpeedGreenScript(float x, float y, int width, int height, Bitmap image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = Bitmap.createScaledBitmap(image, width, height, false);
    }

    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, x, y, null);
        }
    }

    public boolean checkCollisionPlayer(Player player) {
        Rect playerRect = new Rect(
                (int)player.getX(),
                (int)player.getY(),
                (int) ((int)player.getX() + player.getWidth()),
                (int) ((int)player.getY() + player.getHeight())
        );

        Rect speedGreenRect = new Rect(
                (int)x,
                (int)y,
                (int)x + width,
                (int)y + height
        );

        return playerRect.intersect(speedGreenRect);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
}