package com.example.firerrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

public class MainActivity extends Activity {

    private GameView gameView;
    private PlayerController playerController;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        Player player = gameView.getPlayer();
        playerController = new PlayerController(player, gameView);


         Button btnLeft = findViewById(R.id.btnLeft);
         Button btnRight = findViewById(R.id.btnRight);
         Button btnJump = findViewById(R.id.btnJump);
         Button btnShoot = findViewById(R.id.btnShoot);

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