package com.example.firerrun;

import android.annotation.SuppressLint;
import android.app.Activity;
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


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize buttons *after* setContentView()
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnJump = findViewById(R.id.btnJump);
        btnShoot = findViewById(R.id.btnShoot);
        btnPause = findViewById(R.id.btnPause); // pause

        btnPause.setVisibility(View.VISIBLE); // pause

        btnLeft.setVisibility(View.VISIBLE);
        btnRight.setVisibility(View.VISIBLE);
        btnJump.setVisibility(View.VISIBLE);
        btnShoot.setVisibility(View.VISIBLE);




        gameView = findViewById(R.id.gameView);
        Player player = gameView.getPlayer();
        playerController = new PlayerController(player, gameView);

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameView.isGamePaused()) {
                    gameView.resumeGame();
                    btnPause.setText("Pause");

                } else {
                    gameView.pauseGame();
                    btnPause.setText("Resume");
                    GameView.isMenuVisible = false;
                }
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
                playerController.jump(); // jump
            }
            return true;
        });

        btnShoot.setOnClickListener(v -> playerController.onShootButtonPressed());
    }
}