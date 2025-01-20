
package com.example.firerrun;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

public class MainActivity extends Activity {

    private GameView gameView;
    private Button btnLeft, btnRight, btnJump, btnShoot;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameView = findViewById(R.id.gameView);
        btnLeft = findViewById(R.id.btnLeft);
        btnRight = findViewById(R.id.btnRight);
        btnJump = findViewById(R.id.btnJump);
        btnShoot = findViewById(R.id.btnShoot);


        btnLeft.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    gameView.moveLeft();
                    break;
                case MotionEvent.ACTION_UP:
                    gameView.stopLeft();
                    break;
            }
            return true;
        });

        btnRight.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    gameView.moveRight();
                    break;
                case MotionEvent.ACTION_UP:
                    gameView.stopRight();
                    break;
            }
            return true;
        });

        btnJump.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameView.jump();
            }
            return true;
        });

        btnShoot.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                gameView.shoot();
            }
            return true;
        });

    }
}
