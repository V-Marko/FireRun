package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

public class TurentScript {
    public float x;
    public float y;
    public float widthFier;
    public float heightFier;
    public float width;
    public float height;
    private float fireRate = 1.0f;
    private float timeSinceLastShot = 0.0f;
    private boolean canShoot = true;
    private GameView gameView;
    private Bitmap bulletBitmap;
    private Bitmap turretBodyBitmap;
    private Bitmap turretFireBitmap;
    private float angle;
    private float barrelLength;
    private float distance;
    private boolean isPlayerInRange;
    private int lives;
    private int maxLives;
    private float speed = 1500.0f;

    private static final int HEALTH_BAR_WIDTH = 150;
    private static final int HEALTH_BAR_HEIGHT = 20;
    private static final int HEALTH_BAR_OFFSET_Y = 10;
    private Paint healthBarBackgroundPaint;
    private Paint healthBarFillPaint;

    public TurentScript(float x, float y, float width, float height, float widthFier, float heightFier, float distance, int lives, GameView gameView) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.widthFier = widthFier;
        this.heightFier = heightFier;
        this.distance = distance;
        this.lives = lives;
        this.maxLives = lives;
        this.gameView = gameView;
        this.bulletBitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.bullet_turret);
        this.turretBodyBitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.turret_body);
        this.turretBodyBitmap = Bitmap.createScaledBitmap(turretBodyBitmap, (int) width, (int) height, false);
        this.turretFireBitmap = BitmapFactory.decodeResource(gameView.getResources(), R.drawable.turret_fire);
        this.turretFireBitmap = Bitmap.createScaledBitmap(turretFireBitmap, (int) widthFier, (int) heightFier, false);
        float scaleFactor = heightFier / 621.0f;
        this.barrelLength = 3005.0f * scaleFactor;
        this.angle = 180;
        this.isPlayerInRange = false;

        healthBarBackgroundPaint = new Paint();
        healthBarBackgroundPaint.setColor(Color.RED);
        healthBarBackgroundPaint.setStyle(Paint.Style.FILL);

        healthBarFillPaint = new Paint();
        healthBarFillPaint.setColor(Color.GREEN);
        healthBarFillPaint.setStyle(Paint.Style.FILL);
    }

    public void update(float deltaTime, Player player) {
        float deltaX = player.getX() + player.getWidth() / 2 - (x + width / 2);
        float deltaY = player.getY() + player.getHeight() / 2 - (y + height / 2);
        float distanceToPlayer = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        isPlayerInRange = distanceToPlayer <= distance;

        if (isPlayerInRange) {
            angle = (float) Math.toDegrees(Math.atan2(deltaY, deltaX));
        }

        timeSinceLastShot += deltaTime;
        if (timeSinceLastShot >= fireRate) {
            canShoot = true;
            timeSinceLastShot = 0.0f;
        }

        if (canShoot && isPlayerInRange) {
            aimAndFire(player);
            canShoot = false;
        }
    }

    private void aimAndFire(Player player) {
        fireProjectile((float) Math.toRadians(angle));
    }

    private void fireProjectile(float fireAngle) {
        fireAngle -= 0.01f;
        float vx = (float) Math.cos(fireAngle) * speed;
        float vy = (float) Math.sin(fireAngle) * speed;

        float pivotX = x + width / 2;
        float pivotY = y + 100;
        float adjustedAngle = (float) Math.toDegrees(fireAngle) + 90;
        float barrelEndX = pivotX + (float) Math.cos(Math.toRadians(adjustedAngle)) * heightFier;
        float barrelEndY = pivotY + (float) Math.sin(Math.toRadians(adjustedAngle)) * heightFier;

        float bulletX = barrelEndX - TurretBullet.width;
        float bulletY = barrelEndY;

        TurretBullet bullet = new TurretBullet(bulletX, bulletY, bulletBitmap, vx, vy);
        gameView.addTurretBullet(bullet);
    }

    public void draw(Canvas canvas) {
        if (lives > 0) {
            float healthBarX = x + (width - HEALTH_BAR_WIDTH) / 2;
            float healthBarY = y - HEALTH_BAR_HEIGHT - HEALTH_BAR_OFFSET_Y-50;

            RectF backgroundRect = new RectF(
                    healthBarX,
                    healthBarY,
                    healthBarX + HEALTH_BAR_WIDTH,
                    healthBarY + HEALTH_BAR_HEIGHT
            );
            canvas.drawRect(backgroundRect, healthBarBackgroundPaint);

            float healthRatio = (float) lives / maxLives;
            float filledWidth = HEALTH_BAR_WIDTH * healthRatio;
            RectF fillRect = new RectF(
                    healthBarX,
                    healthBarY,
                    healthBarX + filledWidth,
                    healthBarY + HEALTH_BAR_HEIGHT
            );
            canvas.drawRect(fillRect, healthBarFillPaint);
        }

        if (turretFireBitmap != null) {
            canvas.save();
            float pivotX = x + width / 2;
            float pivotY = y;
            float adjustedAngle = angle + 180;
            canvas.rotate(adjustedAngle, pivotX, pivotY);
            float drawX = x + (width - widthFier) / 2;
            float drawY = y - heightFier + 50;
            canvas.drawBitmap(turretFireBitmap, drawX, drawY, null);
            canvas.restore();
        }

        if (turretBodyBitmap != null) {
            canvas.drawBitmap(turretBodyBitmap, x, y, null);
        }
    }

    public boolean checkCollisionBullet(Bullet bullet) {
        return bullet.getX() + Bullet.width > x &&
                bullet.getX() < x + width &&
                bullet.getY() + Bullet.height > y &&
                bullet.getY() < y + height;
    }

    public void takeDamage(int damage) {
        lives -= damage;
    }

    public boolean isAlive() {
        return lives > 0;
    }
}