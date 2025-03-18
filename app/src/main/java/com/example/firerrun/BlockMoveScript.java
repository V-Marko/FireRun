package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class BlockMoveScript {
    public float x;         // Позиция движущегося блока (блок 2)
    public float y;
    public float width;
    public float height;
    public float speed;

    public float position1X; // Позиция блока 1
    public float position1Y;
    public float width1;
    public float height1;

    public float position2X; // Позиция блока 3
    public float position2Y;
    public float width2;
    public float height2;

    boolean movingRight = true; // Флаг направления движения
    private Bitmap blockImage;  // Текстура блока

    public BlockMoveScript(float x, float y, float width, float height, float speed,
                           float position1X, float position1Y, float width1, float height1,
                           float position2X, float position2Y, float width2, float height2,
                           Bitmap blockImage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.position1X = position1X;
        this.position1Y = position1Y;
        this.width1 = width1;
        this.height1 = height1;
        this.position2X = position2X;
        this.position2Y = position2Y;
        this.width2 = width2;
        this.height2 = height2;
        this.blockImage = Bitmap.createScaledBitmap(blockImage, (int)width, (int)height, false);
    }

    public void update() {
        if (movingRight) {
            x += speed;
        } else {
            x -= speed;
        }

        if (checkCollisionWithBlock1()) {
            movingRight = true;
            x = position1X + width1;
        }

        if (checkCollisionWithBlock2()) {
            movingRight = false;
            x = position2X - width;
        }
    }

    private boolean checkCollisionWithBlock1() {
        return x < position1X + width1 &&
                x + width > position1X &&
                y < position1Y + height1 &&
                y + height > position1Y;
    }

    private boolean checkCollisionWithBlock2() {
        return x < position2X + width2 &&
                x + width > position2X &&
                y < position2Y + height2 &&
                y + height > position2Y;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(blockImage, x, y, null);
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
}