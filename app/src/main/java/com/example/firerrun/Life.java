package com.example.firerrun;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

public class Life {
    private int maxLives;
    public int currentLives;
    private Paint textPaint;
    private Context context;
    private Handler handler;

    private static final int HEALTH_BAR_WIDTH = 300;
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_OFFSET_Y = 10;
    private Paint healthBarBackgroundPaint;
    private Paint healthBarFillPaint;

    private int textX = 360;

    public Life(Context context, GameView gameView) {
        this.context = context;
        this.handler = new Handler(Looper.getMainLooper());
        this.maxLives = 100;
        this.currentLives = maxLives;

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        healthBarBackgroundPaint = new Paint();
        healthBarBackgroundPaint.setColor(Color.GRAY);
        healthBarBackgroundPaint.setStyle(Paint.Style.FILL);

        healthBarFillPaint = new Paint();
        healthBarFillPaint.setColor(Color.RED);
        healthBarFillPaint.setStyle(Paint.Style.FILL);
    }

    public void draw(Canvas canvas) {
        if (currentLives <= 100) {
            canvas.drawText("" + currentLives, textX, 70, textPaint);

            float healthBarX = 50;
            float healthBarY = 50;

            RectF backgroundRect = new RectF(
                    healthBarX,
                    healthBarY,
                    healthBarX + HEALTH_BAR_WIDTH,
                    healthBarY + HEALTH_BAR_HEIGHT
            );
            canvas.drawRect(backgroundRect, healthBarBackgroundPaint);

            float healthRatio = (float) currentLives / maxLives;
            float filledWidth = HEALTH_BAR_WIDTH * healthRatio;
            RectF fillRect = new RectF(
                    healthBarX,
                    healthBarY,
                    healthBarX + filledWidth,
                    healthBarY + HEALTH_BAR_HEIGHT
            );
            canvas.drawRect(fillRect, healthBarFillPaint);

            if (currentLives <= 0) {
                currentLives = 0;
            }
        } else {
            currentLives = 100;
            canvas.drawText("" + currentLives, textX, 70, textPaint);

            float healthBarX = 500;
            float healthBarY = 50 + textPaint.getTextSize() + HEALTH_BAR_OFFSET_Y;

            RectF backgroundRect = new RectF(
                    healthBarX,
                    healthBarY,
                    healthBarX + HEALTH_BAR_WIDTH,
                    healthBarY + HEALTH_BAR_HEIGHT
            );
            canvas.drawRect(backgroundRect, healthBarBackgroundPaint);

            float healthRatio = 1.0f;
            float filledWidth = HEALTH_BAR_WIDTH * healthRatio;
            RectF fillRect = new RectF(
                    healthBarX,
                    healthBarY,
                    healthBarX + filledWidth,
                    healthBarY + HEALTH_BAR_HEIGHT
            );
            canvas.drawRect(fillRect, healthBarFillPaint);
        }
    }
    public void decreaseLife(int amount) {
        currentLives = Math.max(0, currentLives - amount);
        if (currentLives <= 0) {
            ((GameView) ((Activity) context).findViewById(R.id.gameView)).pauseGame();
            ((GameView) ((Activity) context).findViewById(R.id.gameView)).isGameOver = true;
        }
    }

    public void resetLife() {
        currentLives = 100;
    }

    public void setCurrentLives(int lives) {
        this.currentLives = Math.min(lives, 100);
    }
}