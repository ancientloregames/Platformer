package com.ancientlore.platformer;

import android.content.Context;

/**
 * Created by Firefly on 5/6/2016.
 */
public class GOTileWater extends GameObject{

    GOTileWater(float worldStartX, float worldStartY, char type) {

        //final int ANIMATION_FPS = 3;
        //final int ANIMATION_FRAME_COUNT = 3;
        final String BITMAP_NAME = "water";

        final float HEIGHT = 1;
        final float WIDTH = 1;

        setHeight(HEIGHT); // 1 metre tall
        setWidth(WIDTH); // 1 metre wide

        setType(type);
        // Now for the player's other attributes
        // Our game engine will use these
        setMoves(false);
        setActive(true);
        setVisible(true);

        // Choose a Bitmap
        setBitmapName(BITMAP_NAME);
        // Set this object up to be animated
        //setAnimFps(ANIMATION_FPS);
        //setAnimFrameCount(ANIMATION_FRAME_COUNT);
        setBitmapName(BITMAP_NAME);
        //setAnimated(context, pixelsPerMetre, true);

        // Where does the tile start
        // X and y locations from constructor parameters
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
    }

    public void update(long fps, float gravity) {
    }
}
