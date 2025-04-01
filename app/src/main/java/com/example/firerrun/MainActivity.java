package com.example.firerrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

    private GameView gameView;
    private PlayerController playerController;
    public static Button btnLeft;
    public static Button btnRight;
    public static Button btnJump;
    public static Button btnShoot;
    public static Button btnPause;
    public static Button btnUse;

    public static MediaPlayer shoot_voice;

    public static MediaPlayer run_voice;
    public static MediaPlayer backgorund_voice;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnJump = findViewById(R.id.btnJump);
        btnShoot = findViewById(R.id.btnShoot);
        btnPause = findViewById(R.id.btnPause);
        btnUse = findViewById(R.id.btnUse);

        btnPause.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        btnJump.setVisibility(View.VISIBLE);
        btnShoot.setVisibility(View.VISIBLE);
        btnUse.setVisibility(View.GONE);

        gameView = findViewById(R.id.gameView);
        Player player = gameView.getPlayer();
        playerController = new PlayerController(player, gameView);

        shoot_voice = MediaPlayer.create(this, R.raw.shoot_voice_n1);
        run_voice = MediaPlayer.create(this, R.raw.runing_voice);
        backgorund_voice = MediaPlayer.create(this, R.raw.background_voice);
        backgorund_voice.setLooping(true);
        float volume = 0.3f;
        backgorund_voice.setVolume(volume, volume);
        backgorund_voice.start();

        btnPause.setOnClickListener(v -> {
            if (gameView.isGamePaused()) {
                gameView.resumeGame();
                btnPause.setText("Pause");
            } else {
                gameView.pauseGame();
                btnPause.setText("Resume");
                GameView.isMenuVisible = false;
            }
        });

        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:

                    playerController.moveLeft();
                    break;
                case MotionEvent.ACTION_UP:
                    playerController.stopLeft();
                    break;
            }
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    playerController.moveRight();
                    break;
                case MotionEvent.ACTION_UP:
                    playerController.stopRight();
                    break;
            }
            return true;
        });

        btnJump.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                playerController.jump();
            }
            return true;
        });

        btnShoot.setOnClickListener(v -> playerController.onShootButtonPressed());

        btnUse.setOnClickListener(v -> {
            for (Switch switchObj : gameView.getSwitches()) {
                if (switchObj.checkCollision(player) && !switchObj.isActivated()) {
                    switchObj.activate();
                    android.util.Log.i("Switch", "Block movement started via button at (" + switchObj.getBlockX() + ", " + switchObj.getBlockY() + ")");
                    break;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pauseGame();
            btnPause.setText("Resume");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null && !GameView.isMenuVisible) {
            gameView.resumeGame();
            btnPause.setText("Pause");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null) {
            gameView.pauseGame();
        }
    }
}