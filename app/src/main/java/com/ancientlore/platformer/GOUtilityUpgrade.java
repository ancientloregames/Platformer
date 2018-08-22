package com.ancientlore.platformer;

public class GOUtilityUpgrade extends GameObject{
    GOUtilityUpgrade(float worldStartX, float worldStartY, char type) {

        final float HEIGHT = .5f;
        final float WIDTH = .5f;

        setHeight(HEIGHT); 
        setWidth(WIDTH); 

        setType(type);

        // Choose a Bitmap

        setBitmapName("clip");

        // Where does the tile start
        // X and y locations from constructor parameters
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
    }

    public void update(long fps, float gravity){}
}
