package com.example.firerrun;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Life {
    private int maxLives;
    private int currentLives;

    private Paint textPaint;

    public Life() {
        this.maxLives = 100;
        this.currentLives = maxLives;


        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setTextSize(50);
        textPaint.setTextAlign(Paint.Align.LEFT);
    }

    public void draw(Canvas canvas) {
        canvas.drawText("Lives: " + currentLives, 50, 50, textPaint);
    }

    public void decreaseLife(int amount) {
        currentLives -= amount;
        if (currentLives < 0) {
            currentLives = 0;
        }
    }

    public void increaseLife(int amount) {
        currentLives += amount;
        if (currentLives > maxLives) {
            currentLives = maxLives;
        }
    }

    public int getCurrentLives() {
        return currentLives;
    }

    public int getMaxLives() {
        return maxLives;
    }

    public void resetLives() {
        currentLives = maxLives;
    }
}