package com.example.firerrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;

public class SwitchCader {
    public static boolean isCameraMoving;  // флаг для отслеживания движения камеры
    private Player player;
    private GameView gameView;
    private static float screenSize;
    private int index = 1;
    private ValueAnimator animator;
    private boolean isAnimating = false;  // флаг анимации
    private static final float CAMERA_OFFSET_SPEED = 20;  // скорость камеры
    private static final float CAMERA_BORDER = 100; // границы камеры

    public SwitchCader(Player player, GameView gameView) {
        this.player = player;
        this.gameView = gameView;
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
    }

    private void startAnimationRight() {
        animateCaderTransition(CAMERA_OFFSET_SPEED, false);
    }

    private void startAnimationLeft() {
        animateCaderTransition(-CAMERA_OFFSET_SPEED, true);
    }

    private void animateCaderTransition(float offsetValue, boolean isLeft) {
        isAnimating = true;  // Камера начала анимацию
        isCameraMoving = true;  // Устанавливаем флаг, что камера двигается

        animator = ValueAnimator.ofFloat(0, offsetValue);
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float offset = (float) animation.getAnimatedValue();

            // Обновляем позицию блоков и объектов, включая BadBoxRun
            for (Block block : gameView.getBlockList()) {
                block.x += offset;
            }
            for (BadBox badBox : gameView.getBadBoxList()) {
                badBox.x += offset;
            }

            for (Bullet bullet : gameView.getBullets()) {
                bullet.x += offset;
            }

            player.setX(player.getX() + offset);

            if (gameView.getBadBox() != null) {
                gameView.getBadBox().x += offset;
            }
        
            gameView.invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimating = false;  // Камера закончила анимацию
                isCameraMoving = false;  // Сбрасываем флаг, камера не двигается
            }
        });

        animator.start();
    }
}

