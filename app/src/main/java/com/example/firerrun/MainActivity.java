package com.example.firerrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends Activity {

    private GameView gameView;
    private PlayerController playerController;
    public static ImageButton btnLeft;
    public static ImageButton btnRight;
    public static ImageButton btnJump;
    public static ImageButton btnShoot;
    public static MaterialButton btnPause;
    public static ImageButton btnUse;

    public static MediaPlayer shoot_voice;
    public static MediaPlayer run_voice;
    public static MediaPlayer backgorund_voice;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnJump = findViewById(R.id.btnJump);
        btnShoot = findViewById(R.id.btnShoot);
        btnPause = findViewById(R.id.btnPause);
        btnUse = findViewById(R.id.btnUse);

        // Set initial visibility
        btnPause.setVisibility(View.VISIBLE);
        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        btnJump.setVisibility(View.VISIBLE);
        btnShoot.setVisibility(View.VISIBLE);
        btnUse.setVisibility(View.GONE);

        // Initialize game view and player controller
        gameView = findViewById(R.id.gameView);
        Player player = gameView.getPlayer();
        playerController = new PlayerController(player, gameView);

        // Initialize media players
        shoot_voice = MediaPlayer.create(this, R.raw.shoot_voice_n1);
        run_voice = MediaPlayer.create(this, R.raw.runing_voice);
        backgorund_voice = MediaPlayer.create(this, R.raw.background_voice);
        backgorund_voice.setLooping(true);
        float volume = 0.3f;
        backgorund_voice.setVolume(volume, volume);
        backgorund_voice.start();

        // Set button listeners
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
            gameView.activateSwitch();
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (gameView != null) {
            gameView.pauseGame();
            btnPause.setText("Resume");
        }
        if (backgorund_voice != null) {
            backgorund_voice.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameView != null && !GameView.isMenuVisible) {
            gameView.resumeGame();
            btnPause.setText("Pause");
        }
        if (backgorund_voice != null) {
            backgorund_voice.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameView != null) {
            gameView.pauseGame();
        }
        if (shoot_voice != null) {
            shoot_voice.release();
            shoot_voice = null;
        }
        if (run_voice != null) {
            run_voice.release();
            run_voice = null;
        }
        if (backgorund_voice != null) {
            backgorund_voice.release();
            backgorund_voice = null;
        }
    }
}