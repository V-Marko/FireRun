package com.example.firerrun;

import android.util.Log;

public class PlayerController {
    private Player player;
    private GameView gameView;
    private long lastJumpTime = 0;
    private final long JUMP_COOLDOWN = 1000;

    public PlayerController(Player player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
    }

    public void onShootButtonPressed() {
        gameView.shoot();
    }

    public void moveLeft() {

        player.setMovingLeft(true);
        player.setMovingRight(false);
    }
    
    public void stopLeft() {
        player.setMovingLeft(false);
    }

    public void moveRight() {
        player.setMovingRight(true);
        player.setMovingLeft(false);
    }

    public void stopRight() {
        player.setMovingRight(false);
    }

    public void jump() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastJumpTime >= JUMP_COOLDOWN) {
            player.jump();
            lastJumpTime = currentTime;
        } else {

        }
    }
}
