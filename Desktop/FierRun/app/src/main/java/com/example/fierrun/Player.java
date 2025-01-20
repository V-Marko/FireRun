package com.example.fierrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private float x, y;
    private float speed = 10f;
    private boolean movingLeft, movingRight, jumping;
    private boolean isIdle; // Новый флаг для проверки состояния "стояния"
    private float jumpSpeed = 15f;
    private float gravity = 1f;

    private Bitmap bodyImage;
    private Bitmap headImage;
    private Bitmap gunImage;
    private Bitmap bulletImage;

    private Context context;

    public static int width = 150; // Body Width
    public int height = 150; // Body Height

    public int headWidth = 150; // Head Width
    public int headHeight = 150; // Head Height

    public int gunWidth = 150; // Gun Width
    public int gunHeight = 90; // Gun Height

    private int person_stop_int = 0;

    // Переменные для задержки анимации
    private long lastIdleTime = 0;
    private static final long ANIMATION_DELAY = 300; // 300 миллисекунд для смены изображений

    // Список снарядов
    private List<Bullet> bullets;

    public Player(Context context) {
        this.context = context;
        x = 100;
        y = 500;

        bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_walk_1);
        bodyImage = Bitmap.createScaledBitmap(bodyImage, width, height, false);

        headImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_head);
        headImage = Bitmap.createScaledBitmap(headImage, headWidth, headHeight, false);

        gunImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_gun);
        gunImage = Bitmap.createScaledBitmap(gunImage, gunWidth, gunHeight, false);

        bulletImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.bullet);

        bullets = new ArrayList<>();
        isIdle = true;
    }

    public void update() {
        if (movingLeft) {
            x -= speed;
            isIdle = false;
        }
        if (movingRight) {
            x += speed;
            isIdle = false;
        }
        if (jumping) {
            y -= jumpSpeed;
            jumpSpeed -= gravity;
            if (y >= 500) {
                y = 500;
                jumping = false;
                jumpSpeed = 15f;
            }
        }

        if (!movingLeft && !movingRight && !jumping) {
            isIdle = true;
            onIdle();
        }

        // Обновление снарядов
        List<Bullet> toRemove = new ArrayList<>();
        for (Bullet bullet : bullets) {
            bullet.update();
            if (bullet.getX() > 2000) {
                toRemove.add(bullet);
            }
        }
        bullets.removeAll(toRemove);
    }

    private void onIdle() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastIdleTime >= ANIMATION_DELAY) {
            if (person_stop_int % 2 == 0) {
                bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_stop1);
            } else {
                bodyImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.person_stop2);
            }
            bodyImage = Bitmap.createScaledBitmap(bodyImage, width, height, false);
            person_stop_int++;

            lastIdleTime = currentTime;
        }
    }

    public void draw(Canvas canvas) {
        // Рисуем игрока
        canvas.drawBitmap(bodyImage, x, y, null);
        canvas.drawBitmap(headImage, x + (width - headWidth) / 2 - 15, y - headHeight + 20, null);
        canvas.drawBitmap(gunImage, x, y, null);

        // Рисуем снаряды
        for (Bullet bullet : bullets) {
            bullet.draw(canvas);
        }
    }

    public void shoot() {
        float bulletX = x + width; // Позиция снаряда впереди игрока
        float bulletY = y + height / 2;
        bullets.add(new Bullet(bulletX, bulletY, bulletImage));
    }

    public void setMovingLeft(boolean movingLeft) {
        this.movingLeft = movingLeft;
    }

    public void setMovingRight(boolean movingRight) {
        this.movingRight = movingRight;
    }

    public void jump() {
        if (!jumping) {
            jumping = true;
        }
    }

    public boolean isMoving() {
        return movingLeft || movingRight;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setPlayerImage(Bitmap newBodyImage, Bitmap newHeadImage, Bitmap newGunImage) {
        this.bodyImage = Bitmap.createScaledBitmap(newBodyImage, width, height, false);
        this.headImage = Bitmap.createScaledBitmap(newHeadImage, headWidth, headHeight, false);
        this.gunImage = Bitmap.createScaledBitmap(newGunImage, gunWidth, gunHeight, false);
    }

    public Context getContext() {
        return context;
    }
}
