package com.example.firerrun;

public class WallUpDownScript {
    public int x;
    private int y;
    private int width;
    private int height;
    private float speed;
    private int minY;
    private int maxY;
    private boolean movingUp = true;

    public WallUpDownScript(int x, int y, int width, int height, float speed, int minY, int maxY) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minY = minY;
        this.maxY = maxY;
        this.speed = speed;
    }

    public void update() {
        if (movingUp) {
            y -= speed;
            if (y <= maxY) {
                y = maxY;
                movingUp = false;
            }
        } else {
            y += speed;
            if (y >= minY) {
                y = minY;
                movingUp = true;
            }
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
    public void setSpeed(float speed) { this.speed = speed; }
}