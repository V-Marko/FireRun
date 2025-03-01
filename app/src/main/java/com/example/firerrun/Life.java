package com.example.firerrun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

public class Life {
    private int maxLives;
    private int currentLives;
    private Paint textPaint;
    private Context context;
    private Handler handler;

    public Life(Context context) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.maxLives = 100;
        this.currentLives = maxLives;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void draw(Canvas canvas) {
        canvas.drawText("Lives: " + currentLives, 50, 50, textPaint);

        if (currentLives <= 0) {
            currentLives = 0;
            playerLose(canvas);
        }
    }
    public void playerLose(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setDither(true);

        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);

        float x = canvas.getWidth() / 2;
        float y = canvas.getHeight() / 3;

        canvas.drawText("Game Over", x, y, paint);
    }

    public void decreaseLife(int amount) {
        currentLives -= amount;
//        if (currentLives <= 0) {
//            currentLives = 0;
//        }
    }
}