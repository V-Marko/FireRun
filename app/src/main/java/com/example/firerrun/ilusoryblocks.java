package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class ilusoryblocks {
    public float x, y;
    private int width, height;
    private Bitmap image;
    private Context context;
    private Paint paint;

    public ilusoryblocks(float x, float y, int width, int height, Bitmap image, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.context = context;
        this.paint = new Paint();
        this.paint.setAlpha(200);
    }

    public void draw(Canvas canvas) {
        if (image != null) {
            canvas.drawBitmap(image, x, y, paint);
        } else {
            Paint fallbackPaint = new Paint();
            fallbackPaint.setColor(android.graphics.Color.GRAY);
            fallbackPaint.setAlpha(200);
            canvas.drawRect(x, y, x + width, y + height, fallbackPaint);
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

    public void setAlpha(int alpha) {
        paint.setAlpha(alpha); // alpha - 0 (transparent) to 255 (opaque)
    }
}