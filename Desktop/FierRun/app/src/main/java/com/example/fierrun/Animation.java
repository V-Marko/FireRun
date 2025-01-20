package com.example.fierrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

public class Animation {
    private Player player;
    private int walkIndex = 0;
    private Bitmap[] walkFrames;
    private Bitmap headImage;
    private Bitmap gunImage;
    private Handler handler = new Handler();
    private int frameDuration = 100;
    private boolean isAnimating = false;

    public Animation(Player player) {
        this.player = player;
        loadWalkFrames();
        loadHeadImage();
        loadGunImage();
    }

    private void loadWalkFrames() {
        int[] frameResources = {
                R.drawable.person_walk_1,
                R.drawable.person_walk_2,
                R.drawable.person_walk_3,
                R.drawable.person_walk_4,
                R.drawable.person_walk_5,
                R.drawable.person_walk_6,
                R.drawable.person_walk_7,
                R.drawable.person_walk_8,
        };
        walkFrames = new Bitmap[frameResources.length];
        for (int i = 0; i < frameResources.length; i++) {
            walkFrames[i] = BitmapFactory.decodeResource(player.getContext().getResources(), frameResources[i]);
        }
    }

    private void loadHeadImage() {
        headImage = BitmapFactory.decodeResource(player.getContext().getResources(), R.drawable.person_head);
    }

    private void loadGunImage() {
        gunImage = BitmapFactory.decodeResource(player.getContext().getResources(), R.drawable.person_gun);
    }

    public void startWalkingAnimation() {
        if (!isAnimating) {
            isAnimating = true;
            handler.post(animationRunnable);
        }
    }

    public void stopWalkingAnimation() {
        isAnimating = false;
        handler.removeCallbacks(animationRunnable);
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAnimating) {
                player.setPlayerImage(walkFrames[walkIndex], headImage, gunImage);
                walkIndex = (walkIndex + 1) % walkFrames.length;
                handler.postDelayed(this, frameDuration);
            }
        }
    };
}
