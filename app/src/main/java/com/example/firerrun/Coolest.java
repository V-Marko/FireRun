package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class Coolest {
    public float x;
    public float y;
    private int width;
    private int height;
    private float rotation;
    private Paint paint;
    private float rotationSpeed;

    public Coolest(float x, float y, int width, int height, float rotationSpeed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.rotation = 0;
        this.rotationSpeed = rotationSpeed;

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
    }

    public void update() {
        rotation += rotationSpeed;
        if (rotation >= 360) {
            rotation = 0;
        }
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.rotate(rotation, x, y);
        RectF rect1 = new RectF(x - width / 2, y - height / 2,
                x + width / 2, y + height / 2);
        canvas.drawRect(rect1, paint);
        canvas.restore();

        canvas.save();
        canvas.rotate(rotation + 90, x, y);
        RectF rect2 = new RectF(x - width / 2, y - height / 2,
                x + width / 2, y + height / 2);
        canvas.drawRect(rect2, paint);
        canvas.restore();
    }

    public boolean checkCollisionWithPlayer(Player player) {
        RectF playerRect = new RectF(player.getX(), player.getY(),
                player.getX() + player.getWidth(), player.getY() + player.getHeight());

        RectF rect1 = new RectF(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        if (checkRotatedCollision(playerRect, rect1, rotation, x, y)) {
            Log.d("CoolestCollision", "Player hit rect1 at (" + x + ", " + y + ")");
            return true;
        }

        RectF rect2 = new RectF(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        if (checkRotatedCollision(playerRect, rect2, rotation + 90, x, y)) {
            Log.d("CoolestCollision", "Player hit rect2 at (" + x + ", " + y + ")");
            return true;
        }

        return false;
    }

    public boolean checkCollisionWithBullet(Bullet bullet) {
        RectF bulletRect = new RectF(bullet.getX(), bullet.getY(),
                bullet.getX() + bullet.getWidth(), bullet.getY() + bullet.getHeight());

        RectF rect1 = new RectF(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        if (checkRotatedCollision(bulletRect, rect1, rotation, x, y)) {
            return true;
        }

        RectF rect2 = new RectF(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        if (checkRotatedCollision(bulletRect, rect2, rotation + 90, x, y)) {
            return true;
        }

        return false;
    }

    private boolean checkRotatedCollision(RectF targetRect, RectF rect, float angle, float centerX, float centerY) {
        double rad = Math.toRadians(angle);

        float[] cornersX = new float[]{rect.left, rect.right, rect.right, rect.left};
        float[] cornersY = new float[]{rect.top, rect.top, rect.bottom, rect.bottom};

        for (int i = 0; i < 4; i++) {
            float rotatedX = centerX + (cornersX[i] - centerX) * (float) Math.cos(rad) -
                    (cornersY[i] - centerY) * (float) Math.sin(rad);
            float rotatedY = centerY + (cornersX[i] - centerX) * (float) Math.sin(rad) +
                    (cornersY[i] - centerY) * (float) Math.cos(rad);

            if (rotatedX >= targetRect.left && rotatedX <= targetRect.right &&
                    rotatedY >= targetRect.top && rotatedY <= targetRect.bottom) {
                return true;
            }
        }

        float[] targetCornersX = new float[]{targetRect.left, targetRect.right, targetRect.right, targetRect.left};
        float[] targetCornersY = new float[]{targetRect.top, targetRect.top, targetRect.bottom, targetRect.bottom};

        boolean allInside = true;
        for (int i = 0; i < 4; i++) {
            float rotatedX = centerX + (targetCornersX[i] - centerX) * (float) Math.cos(-rad) -
                    (targetCornersY[i] - centerY) * (float) Math.sin(-rad);
            float rotatedY = centerY + (targetCornersX[i] - centerX) * (float) Math.sin(-rad) +
                    (targetCornersY[i] - centerY) * (float) Math.cos(-rad);

            if (rotatedX < rect.left || rotatedX > rect.right ||
                    rotatedY < rect.top || rotatedY > rect.bottom) {
                allInside = false;
                break;
            }
        }

        return allInside;
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }
}