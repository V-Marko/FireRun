package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

public class BlowingStone {
    public float x;
    public float y;
    public float speed;
    public float width;
    public float height;
    private Bitmap bitmap;
    private float rotation;
    private float rotationSpeed;

    // Конструктор
    public BlowingStone(float x, float y, float speed, float width, float height, Context context) {
        this.x = x;
        this.y = y; // Начинаем выше экрана
        this.speed = speed; // Скорость падения
        this.width = width;
        this.height = height;
        this.bitmap = Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.getResources(), R.drawable.stone),
                (int) width, (int) height, false
        );
        this.rotation = 0f;
        this.rotationSpeed = 10f; // Скорость вращения
    }

    // Обновление состояния камня
    public void update() {
        y += speed; // Камень падает вниз с заданной скоростью
        rotation += rotationSpeed; // Вращение
        if (rotation >= 360f) {
            rotation -= 360f; // Сбрасываем вращение после полного круга
        }
    }

    // Отрисовка камня
    public void draw(Canvas canvas) {
        if (bitmap != null) {
            canvas.save();
            canvas.rotate(rotation, x + width / 2, y + height / 2); // Вращаем камень
            canvas.drawBitmap(bitmap, x, y, null); // Рисуем камень
            canvas.restore();
        }
    }

    // Проверка столкновения с игроком
    public boolean checkCollisionWithPlayer(Player player) {
        return (x < player.getX() + player.getWidth() &&
                x + width > player.getX() &&
                y < player.getY() + player.getHeight() &&
                y + height > player.getY());
    }

    // Геттеры
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