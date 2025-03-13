package com.example.firerrun;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.List;

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
    private List<Block> blocks; // Список блоков для проверки столкновений

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
        paint.setColor(0xFFFF0000); // Красный цвет
        paint.setStyle(Paint.Style.FILL);
    }

    // Метод для установки списка блоков
    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
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

            float newX = x + moveX;
            float newY = y + moveY;

            if (blocks != null) {
                for (Block block : blocks) {
                    if (checkCollisionWithBlock(newX, newY, block)) {
                        if (newY < y) {
                            moveY = 0;
                            newY = y;
                        }

                        if (moveX > 0 && newX + width > block.getX() && x + width <= block.getX()) {
                            moveX = 0;
                            newX = block.getX() - width;
                        } else if (moveX < 0 && newX < block.getX() + block.getWidth() && x >= block.getX() + block.getWidth()) {
                            moveX = 0;
                            newX = block.getX() + block.getWidth();
                        }
                    }
                }
            }

            x = newX;
            y = newY;
        }
    }

    private boolean checkCollisionWithBlock(float newX, float newY, Block block) {
        float left = newX - width / 2;
        float right = newX + width / 2;
        float top = newY - height / 2;
        float bottom = newY + height / 2;

        float blockLeft = block.getX();
        float blockRight = block.getX() + block.getWidth();
        float blockTop = block.getY();
        float blockBottom = block.getY() + block.getHeight();

        return right > blockLeft && left < blockRight && bottom > blockTop && top < blockBottom;
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