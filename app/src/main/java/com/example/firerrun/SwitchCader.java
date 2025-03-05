package com.example.firerrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import java.util.List;

public class SwitchCader {
    public static boolean isCameraMoving;
    private List<FinishScript> finishScripts;
    private List<SpeedGreenScript> speedGreenScripts; // Добавляем список объектов SpeedGreenScript
    private Player player;
    private GameView gameView;
    private static float screenSize;
    public static int index = 1;
    private ValueAnimator animator;
    private boolean isAnimating = false;
    private static final float CAMERA_OFFSET_SPEED = 200;
    private static final float CAMERA_BORDER = 100;
    private float lastOffset = 0;

    public SwitchCader(Player player, GameView gameView, List<SpeedGreenScript> speedGreenScripts) {
        this.player = player;
        this.gameView = gameView;
        this.finishScripts = gameView.finishScripts;
        this.speedGreenScripts = speedGreenScripts;
        this.screenSize = gameView.getScreenWidth(gameView.getContext());
    }

    public void updateCader() {
        float threshold = (screenSize / 4);

        if (isAnimating) {
            return;
        }

        if (player.getX() > threshold && player.getVelocityX() > 0) {
            gameView.post(() -> startAnimationLeft());
            index++;
        } else if (player.getX() < threshold && player.getVelocityX() < 0) {
            if (index > 1) {
                gameView.post(() -> startAnimationRight());
                index--;
            }
        }
        for (FinishScript finishScript : finishScripts) {
            finishScript.x += 0;
        }
    }

    private void startAnimationRight() {
        animateCaderTransition(CAMERA_OFFSET_SPEED, false);
    }

    private void startAnimationLeft() {
        animateCaderTransition(-CAMERA_OFFSET_SPEED, true);
    }

    private void animateCaderTransition(float offsetValue, boolean isLeft) {
        isAnimating = true;
        isCameraMoving = true;

        animator = ValueAnimator.ofFloat(0, offsetValue);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float offset = (float) animation.getAnimatedValue();
            float deltaOffset = offset - lastOffset;
            lastOffset = offset;

            for (FinishScript finishScript : finishScripts) {
                if (finishScript != null) {
                    finishScript.x += deltaOffset;
                }
            }

            for (Block block : gameView.getBlockList()) {
                block.x += deltaOffset;
            }

            // Исправляем цикл для списка объектов SpeedGreenScript
            for (SpeedGreenScript sgs : speedGreenScripts) {
                sgs.x += deltaOffset;
            }

            for (BadBox badBox : gameView.getBadBoxList()) {
                badBox.x += deltaOffset;
            }

            for (Bullet bullet : gameView.getBullets()) {
                bullet.x += deltaOffset;
            }

            player.setX(player.getX() + deltaOffset);

            if (gameView.getBadBox() != null) {
                gameView.getBadBox().x += deltaOffset;
            }

            gameView.invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;
                isCameraMoving = false;
                lastOffset = 0;
            }
        });

        animator.start();
    }
}