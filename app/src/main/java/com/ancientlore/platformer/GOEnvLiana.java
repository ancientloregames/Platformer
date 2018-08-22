package com.ancientlore.platformer;

import java.util.Random;

public class GOEnvLiana extends GameObject {

    GOEnvLiana(float worldStartX, float worldStartY, char type) {

        final float HEIGHT = 2;
        final float WIDTH = 1;
        setHeight(HEIGHT);
        setWidth(WIDTH);
        setType(type);
        setBitmapName("liana");
        setActive(false);
        Random rand = new Random();
        if(rand.nextInt(2)==0) {
            setWorldLocation(worldStartX, worldStartY, -1);
        }else{
            setWorldLocation(worldStartX, worldStartY, 1);
        }
    }

    public void update(long fps, float gravity) {
    }
}
