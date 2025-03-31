package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;

public class Animation {
    private Player player;
    private int walkIndex = 0;
    private int stopIndex = 0;
    private Bitmap[] walkFrames, walkFramesStop;
    private Bitmap gunImage;
    Handler handler = new Handler();
    private int frameDuration = 500;
    private boolean isAnimating = false;
    private boolean isStopping = false;

    public Animation(Player player) {
        this.player = player;
        loadWalkFrames();
        loadGunImage();
    }

    private void loadWalkFrames() {
        int[] frameResources = {
                R.drawable.person_walk_1, R.drawable.person_walk_2, R.drawable.person_walk_3,
                R.drawable.person_walk_4, R.drawable.person_walk_5, R.drawable.person_walk_6,
                R.drawable.person_walk_7, R.drawable.person_walk_8
        };
        int[] frameResourcesStop = {R.drawable.person_stop1, R.drawable.person_stop2};
        walkFrames = new Bitmap[frameResources.length];
        walkFramesStop = new Bitmap[frameResourcesStop.length];

        for (int i = 0; i < frameResources.length; i++) {
            walkFrames[i] = BitmapFactory.decodeResource(player.getContext().getResources(), frameResources[i]);
            if (walkFrames[i] == null) {
                Log.e("Animation", "Failed to load walkFrame " + i);
            }
        }
        for (int i = 0; i < frameResourcesStop.length; i++) {
            walkFramesStop[i] = BitmapFactory.decodeResource(player.getContext().getResources(), frameResourcesStop[i]);
            if (walkFramesStop[i] == null) {
                Log.e("Animation", "Failed to load walkFrameStop " + i);
            }
        }
    }

    private void loadGunImage() {
        gunImage = BitmapFactory.decodeResource(player.getContext().getResources(), R.drawable.person_gun);
        if (gunImage == null) {
            Log.e("Animation", "Failed to load gunImage");
        }
    }

    public void startWalkingAnimation() {
        if (!isAnimating) {
            isAnimating = true;
            isStopping = false;
            handler.post(animationRunnable);
        }
    }

    public void stopWalkingAnimation() {
        isAnimating = false;
        isStopping = true;
        handler.post(animationRunnable);
    }

    private Runnable animationRunnable = new Runnable() {
        @Override
        public void run() {
            if (isAnimating) {
                player.setPlayerImage(walkFrames[walkIndex], player.headImage, gunImage);
                walkIndex = (walkIndex + 1) % walkFrames.length;
                handler.postDelayed(this, frameDuration);
            } else if (isStopping) {
                player.setPlayerImage(walkFramesStop[stopIndex], player.headImage, gunImage);
                stopIndex = (stopIndex + 1) % walkFramesStop.length;
                handler.postDelayed(this, frameDuration);
            }
        }
    };
}