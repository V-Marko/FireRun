package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class FinishScript {
    public float x, y;
    private int width, height;
    private Bitmap image;

    public FinishScript(float x, float y, int width, int height, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.finish),
                width, height, false
        );
    }

    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, x, y, null);
        }
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
}