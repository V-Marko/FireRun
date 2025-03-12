package com.example.firerrun;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class SmallRunBoom {
    public float x;
    public float y;
    public float width;
    public float height;
    public float speedRotate;
    public float angle;
    public float speed;
    public Paint paint;
    public float SRBdistance;

    public SmallRunBoom(float x, float y, float width, float height, float speedRotate, float speed, float SRBdistance) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speedRotate = speedRotate;
        this.speed = speed;
        this.angle = 0;
        this.SRBdistance = SRBdistance;
        paint = new Paint();
        paint.setColor(0xFFFF0000);
        paint.setStyle(Paint.Style.FILL);
    }

    public void update(float playerX, float playerY) {
        angle += speedRotate;
        if (angle >= 360) {
            angle -= 360;
        }

        float offsetY = 50f;
        float targetY = playerY - offsetY;

        float dx = playerX - x;
        float dy = targetY - y;

        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > 0 && distance <= SRBdistance) {
            float moveX = (dx / distance) * speed;
            float moveY = (dy / distance) * speed * 2f;

            x += moveX;
            y += moveY;
        }
    }

    public void draw(Canvas canvas) {
        canvas.save();
        canvas.translate(x, y);
        canvas.rotate(angle);

        canvas.save();
        canvas.rotate(45);
        RectF diagonal1 = new RectF(-width / 2, -height / 2, width / 2, height / 2);
        canvas.drawRect(diagonal1, paint);
        canvas.restore();

        canvas.restore();
    }

    public RectF getDiagonal1Bounds() {
        RectF diagonal1 = new RectF(-width / 2, -height / 2, width / 2, height / 2);

        float rotatedX1 = (float) (diagonal1.left * Math.cos(Math.toRadians(angle + 45)) - diagonal1.top * Math.sin(Math.toRadians(angle + 45)));
        float rotatedY1 = (float) (diagonal1.left * Math.sin(Math.toRadians(angle + 45)) + diagonal1.top * Math.cos(Math.toRadians(angle + 45)));
        float rotatedX2 = (float) (diagonal1.right * Math.cos(Math.toRadians(angle + 45)) - diagonal1.bottom * Math.sin(Math.toRadians(angle + 45)));
        float rotatedY2 = (float) (diagonal1.right * Math.sin(Math.toRadians(angle + 45)) + diagonal1.bottom * Math.cos(Math.toRadians(angle + 45)));

        float left = Math.min(rotatedX1, rotatedX2) + x;
        float top = Math.min(rotatedY1, rotatedY2) + y;
        float right = Math.max(rotatedX1, rotatedX2) + x;
        float bottom = Math.max(rotatedY1, rotatedY2) + y;

        return new RectF(left, top, right, bottom);
    }
}