package com.example.fierrun;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public class Bullet {
    private float x, y;
    public static final float DEFAULT_SPEED = 20f;
    private float speed;
    private Bitmap image;
    private float PlayerWidth = Player.width;
    public Bullet(float x, float y, Bitmap image) {
        this.x = x;
        this.y = y;
        this.image = Bitmap.createScaledBitmap(image, 50, 20, false);
        this.speed = DEFAULT_SPEED;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void update() {
        if(PlayerWidth>0){
            x += speed;
        } else if(PlayerWidth< 0 ) {
            x -= speed;
        }


    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y - 50, null);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Bitmap getImage() {
        return image;
    }
}
