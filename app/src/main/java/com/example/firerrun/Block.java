package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Block {
    public float x, y;
    private int width, height;
    private Bitmap image;
    private Context context;

    public Block(Context context, float x, float y, int width, int height, Bitmap image) {
        this.context = context;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image != null && width > 0 && height > 0
                ? Bitmap.createScaledBitmap(image, width, height, false)
                : image;
    }

    public void draw(Canvas canvas) {
        if (image != null && !image.isRecycled()) {
            canvas.drawBitmap(image, x, y, null);
        } else {
            Paint paint = new Paint();
            paint.setColor(android.graphics.Color.RED); // Fallback color for debugging
            canvas.drawRect(x, y, x + width, y + height, paint);
        }
    }

    public void update() {}

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

    public void recycle() {
        if (image != null && !image.isRecycled()) {
            image.recycle();
            image = null;
        }
    }
}