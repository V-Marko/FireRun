package com.example.firerrun;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;

public class Life {
    private int maxLives;
    private int currentLives;
    private Paint textPaint;
    private Context context;
    private Handler handler;

    // Поля для полосы здоровья
    private static final int HEALTH_BAR_WIDTH = 300; // Ширина полосы здоровья
    private static final int HEALTH_BAR_HEIGHT = 20; // Высота полосы здоровья
    private static final int HEALTH_BAR_OFFSET_Y = 10; // Отступ полосы под текстом
    private Paint healthBarBackgroundPaint; // Краска для фона полосы
    private Paint healthBarFillPaint; // Краска для заполненной части полосы (красная)

    private int textX = 360;

    public Life(Context context) {
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
                playerLose(canvas);
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
    }

    public void resetLife() {
        currentLives = 100;
    }
}