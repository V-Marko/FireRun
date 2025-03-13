package com.example.firerrun;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.util.List;

public class BadBoxBotScript {
    public float x;
    public float y;
    private float width;
    private float height;
    private float speed = 7f;
    private float jumpSpeed = 0f;
    private float gravity = 0.38f;
    private boolean isOnGround = false;
    private float landRestriction = 500;
    private Bitmap botImage;
    private Context context;
    private List<Block> blocks;
    private float aggroDistance;

    public BadBoxBotScript(float x, float y, float width, float height, float aggroDistance, Context context) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.aggroDistance = aggroDistance;
        this.context = context;

        botImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.bad_box);
        botImage = Bitmap.createScaledBitmap(botImage, (int)width, (int)height, false);
    }

    public void update(Player player, List<Block> blocks) {
        this.blocks = blocks;

        float playerX = player.getX();
        float playerY = player.getY();

        // Вычисляем расстояние до игрока
        float dx = playerX - x;
        float dy = playerY - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        // Движение к игроку только в пределах дистанции агрессии
        if (distance > 0 && distance <= aggroDistance) {
            float moveX = (dx / distance) * speed;
            float newX = x + moveX;

            // Проверяем столкновение с блоками перед перемещением
            if (!checkBlockCollisionHorizontal(newX)) {
                x = newX;
            }
        }

        // Гравитация
        if (!isOnGround()) {
            jumpSpeed += gravity;
            y += jumpSpeed;
        }

        // Проверка столкновений с землёй или блоками
        boolean isOnBlock = checkBlockCollisionVertical();

        if (!isOnBlock && y >= landRestriction) {
            y = landRestriction;
            jumpSpeed = 0;
            isOnGround = true;
        }
    }

    private boolean isOnGround() {
        return y >= landRestriction || checkBlockCollisionVertical();
    }

    // Проверка горизонтальных столкновений (чтобы не забираться на блоки)
    private boolean checkBlockCollisionHorizontal(float newX) {
        if (blocks == null) return false;

        for (Block block : blocks) {
            boolean xOverlap = (newX < block.getX() + block.getWidth()) &&
                    (newX + width > block.getX());
            boolean yOverlap = (y + height > block.getY()) &&
                    (y < block.getY() + block.getHeight());

            if (xOverlap && yOverlap) {
                // Если бот сталкивается с блоком сбоку, останавливаем его
                if (newX < x) { // Движение влево
                    x = block.getX() + block.getWidth();
                } else { // Движение вправо
                    x = block.getX() - width;
                }
                return true;
            }
        }
        return false;
    }

    // Проверка вертикальных столкновений (только для падения на землю)
    private boolean checkBlockCollisionVertical() {
        if (blocks == null) return false;

        for (Block block : blocks) {
            boolean xOverlap = (x < block.getX() + block.getWidth()) &&
                    (x + width > block.getX());
            boolean yOverlap = (y + height >= block.getY()) &&
                    (y + height <= block.getY() + block.getHeight());

            if (xOverlap && yOverlap && jumpSpeed >= 0) {
                // Если бот падает на блок сверху, останавливаем его
                y = block.getY() - height;
                jumpSpeed = 0;
                isOnGround = true;
                return true;
            }
        }
        return false;
    }

    private boolean isCollidingWithBlock(Block block) {
        float botLeft = x;
        float botRight = x + width;
        float botTop = y;
        float botBottom = y + height;

        float blockLeft = block.getX();
        float blockRight = block.getX() + block.getWidth();
        float blockTop = block.getY();
        float blockBottom = block.getY() + block.getHeight();

        return botRight > blockLeft && botLeft < blockRight &&
                botBottom > blockTop && botTop < blockBottom;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(botImage, x, y, null);
    }

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

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public boolean checkCollisionWithPlayer(Player player) {
        float playerLeft = player.getX();
        float playerRight = player.getX() + player.getWidth();
        float playerTop = player.getY();
        float playerBottom = player.getY() + player.getHeight();

        float botLeft = x;
        float botRight = x + width;
        float botTop = y;
        float botBottom = y + height;

        return botRight > playerLeft && botLeft < playerRight &&
                botBottom > playerTop && botTop < playerBottom;
    }
}