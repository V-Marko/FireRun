package com.example.firerrun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.content.Context;

public class Block {
    private BlocksList blocks; // Instance of BlocksList
    static float width= 150;
    static float height = 150;
    private Bitmap image;
    public static int drawIndex = 0;

    // Constructor
    public Block(float width, float height, int imageResource, Context context) {
        this.width = width;
        this.height = height;
        this.image = BitmapFactory.decodeResource(context.getResources(), imageResource);
        this.image = Bitmap.createScaledBitmap(this.image, (int) width, (int) height, false);
        this.blocks = new BlocksList();
    }

    // Draw method
    public void draw(Canvas canvas) {
        for (int i = 0; i < blocks.Blocks.length; i++) {
            drawIndex = i;
            int[] block = blocks.Blocks[i];
            int x = block[0];
            int y = block[1];
            canvas.drawBitmap(image, x, y, null);
        }
    }
}
