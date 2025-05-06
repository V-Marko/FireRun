package com.example.firerrun;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.util.Log;

import java.util.List;

public class SwitchCader {
    public static boolean isCameraMoving;
    private List<FinishScript> finishScripts;
    private List<SpeedGreenScript> speedGreenScripts;
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
        float threshold = screenSize / 4; //TODO: cader switch size
        float playerX = player.getX();

        if (isAnimating) {
            return;
        }

        if (playerX > threshold && player.getVelocityX() > 0) {
            gameView.post(() -> startAnimationLeft());
            index++;
        } else if (playerX < (screenSize - threshold) && player.getVelocityX() < 0) {
            if (index > 1) {
                gameView.post(() -> startAnimationRight());
                index--;
            }
        }

        ensurePlayerVisible();
    }

    private void ensurePlayerVisible() {
        float playerX = player.getX();
        float minX = CAMERA_BORDER;
        float maxX = screenSize - CAMERA_BORDER;

        if (playerX < minX) {
            float correction = minX - playerX;
            moveGameObjects(correction);
            player.setX(minX);
        } else if (playerX > maxX) {
            float correction = maxX - playerX;
            moveGameObjects(correction);
            player.setX(maxX);
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

        float dynamicOffsetSpeed = offsetValue * 2;

        if (gameView.getLevel() == 2) {
            dynamicOffsetSpeed *= 1.5f;
        }

        animator = ValueAnimator.ofFloat(0, dynamicOffsetSpeed);
        animator.setDuration(400);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float offset = (float) animation.getAnimatedValue();
            float deltaOffset = offset - lastOffset;
            lastOffset = offset;

            moveGameObjects(deltaOffset);
            player.setX(player.getX() + deltaOffset);

            if (gameView.getBadBox() != null) {
                gameView.getBadBox().x += deltaOffset;
            }

            gameView.invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnimating = false;
                isCameraMoving = false;
                lastOffset = 0;
                Log.d("SwitchCader", "Animation ended, lastOffset reset to 0");
            }
        });

        animator.start();
    }

    private void moveGameObjects(float deltaOffset) {
        for (FinishScript finishScript : finishScripts) {
            if (finishScript != null) {
                finishScript.x += deltaOffset;
            }
        }
        for (SmallRunBoom smallRunBoom : gameView.smallRunBooms) {
            smallRunBoom.x += deltaOffset;
        }
        for (Block block : gameView.getBlockList()) {
            block.x += deltaOffset;
        }
        for (SpeedGreenScript sgs : speedGreenScripts) {
            sgs.x += deltaOffset;
        }
        for (BlowingStone stone : gameView.blowingStones) {
            stone.x += deltaOffset;
        }
        for (BadBox badBox : gameView.getBadBoxList()) {
            badBox.x += deltaOffset;
        }
        for (Bullet bullet : gameView.getBullets()) {
            bullet.x += deltaOffset;
        }
        for (BadBoxBotScript bbs : gameView.getBadBoxBot()) {
            bbs.x += deltaOffset;
        }
        for (BoomScript boom : gameView.boomScripts) {
            boom.x += deltaOffset;
        }
        for (Coolest coolest : gameView.coolestList) {
            coolest.x += deltaOffset;
        }
        for (WallUpDownScript wall : gameView.wallUpDownScripts) {
            wall.x += deltaOffset;
        }
        for (Switch s : gameView.switches) {
            s.x += deltaOffset;
            s.BlockX += deltaOffset;
            s.targetX += deltaOffset;
        }
        for (BlockMoveScript bms : gameView.blockMoveScripts) {
            bms.x += deltaOffset;
            bms.position1X += deltaOffset;
            bms.position2X += deltaOffset;
        }
        for (ilusoryblocks ib : gameView.illusoryBlocks) {
            ib.x += deltaOffset;
        }
        for (TurentScript turret : gameView.turrets) {
            turret.x += deltaOffset;
        }
        for (TurretBullet turretBullet : gameView.turretBullets) {
            turretBullet.x += deltaOffset;
        }
    }
}