package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;
import android.graphics.Rect;

public class Block {
    public float x, y;
    private int width, height;
    private Bitmap image;
    private Context context;

    public Block(Context context, float x, float y, int width, int height, int imageResourceId) {
        this.context = context;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.image = BitmapFactory.decodeResource(context.getResources(), imageResourceId);
        this.image = Bitmap.createScaledBitmap(image, width, height, false);
    }

    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, x, y, null);
        } else {
            canvas.drawRect(x, y, x + width, y + height, null);
        }
    }
    public void update() {

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