package com.ancientlore.platformer;

public class GOTileGrass extends GameObject {

    GOTileGrass(float worldStartX, float worldStartY, char type) {
        setTraversable();

        final float HEIGHT = 1;
        final float WIDTH = 1;

        setHeight(HEIGHT); // 1 metre tall
        setWidth(WIDTH); // 1 metre wide

        setType(type);


        // Choose a Bitmap
        switch (type){
            case '0':
                setBitmapName("grass_middle");
                break;
            case '1':
                setBitmapName("grass_top");
                break;
            case 'i':
                setBitmapName("grass_island");
                break;
            case 'l':
                setBitmapName("grass_lake");
                break;
        }

        // Where does the tile start
        // X and y locations from constructor parameters
        setWorldLocation(worldStartX, worldStartY, 0);
        setRectHitbox();
    }

    public void update(long fps, float gravity) {
    }
}
