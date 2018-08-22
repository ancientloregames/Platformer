package com.ancientlore.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;

public class ManagerLevel {

    private String level;
    int mapWidth;
    int mapHeight;

    GOPlayer player;
    int playerIndex;

    private boolean playing;
    float gravity;

    LevelData levelData;
    ArrayList<GameObject> gameObjects;
    ArrayList<Background> backgrounds;

    ArrayList<Rect> currentButtons;
    Bitmap[] bitmapsArray;

    public ManagerLevel(Context context, int pixelsPerMetre, int screenWidth, ManagerInput ic, String level, float px, float py) {
        this.level = level;

        switch (level) {
            case "Level1":
                levelData = new Level1();
                break;

        }

        // To hold all our GameObjects
        gameObjects = new ArrayList<>();

        // To hold 1 of every Bitmap
        bitmapsArray = new Bitmap[25];

        // Load all the GameObjects and Bitmaps
        loadMapData(context, pixelsPerMetre, px, py);
        loadBackgrounds(context, pixelsPerMetre, screenWidth);

        // Set waypoints for our guards
        setWaypoints();

        //playing = true;
    }

    private void loadBackgrounds(Context context, int pixelsPerMetre, int screenWidth) {
        backgrounds = new ArrayList<Background>();
        //load the background data into the Background objects and
        // place them in our GameObject arraylist
        for (BackgroundData bgData : levelData.backgroundDataList) {
            backgrounds.add(new Background(context, pixelsPerMetre, screenWidth, bgData));
        }

    }

    public void setWaypoints() {
        // Loop through all game objects looking for Guards
        for (GameObject guard : this.gameObjects) {
            if (guard.getType() == 'g') {
                // Set waypoints for this guard
                // find the tile beneath the guard
                // this relies on the designer putting the guard in sensible location

                int startTileIndex = -1;
                int startGuardIndex = 0;
                float waypointX1 = -1;
                float waypointX2 = -1;
                //Log.d("yay","found a guard");
                //Log.d("before fors x1 = ", "" + waypointX1);
                //Log.d("before fors x2 = ", "" + waypointX2);

                for (GameObject tile : this.gameObjects) {
                    startTileIndex++;
                    if (tile.getWorldLocation().y == guard.getWorldLocation().y + 2) {
                        //tile is two space below current guard
                        // Now see if has same x coordinate
                        if (tile.getWorldLocation().x == guard.getWorldLocation().x) {

                            // Found the tile the guard is "standing" on
                            // Now go left as far as possible before non travers-able tile is found
                            // Either on guards row or tile row
                            // upto a maximum of 5 tiles. (5 is arbitrary value)
                            for (int i = 0; i < 5; i++) {// left for loop

                                if (!gameObjects.get(startTileIndex - i).isTraversable()) {
                                    //set the left waypoint
                                    waypointX1 = gameObjects.get(startTileIndex - (i + 1)).getWorldLocation().x;
                                    Log.d("set x1 = ", "" + waypointX1);
                                    break;// Leave left for loop

                                } else {
                                    //set to max 5 tiles as no non traversible tile found
                                    waypointX1 = gameObjects.get(startTileIndex - 5).getWorldLocation().x;
                                }
                            }// end get left waypoint

                            for (int i = 0; i < 5; i++) {// right for loop
                                if (!gameObjects.get(startTileIndex + i).isTraversable()) {
                                 //set the right waypoint

                                    waypointX2 = gameObjects.get(startTileIndex + (i - 1)).getWorldLocation().x;
                                    //Log.d("set x2 = ", "" + waypointX2);
                                    break;// Leave right for loop

                                } else {
                                    //set to max 5 tiles away
                                    waypointX2 = gameObjects.get(startTileIndex + 5).getWorldLocation().x;
                                }

                            }// end get right waypoint
                            GOEnemyGuard g = (GOEnemyGuard) guard;

                            g.setWaypoints(waypointX1, waypointX2);
                            //Log.d("after fors x1 = ", "" + waypointX1);
                        }

                    }
                }
            }
        }
    }

    public void switchPlayingStatus() {
        playing = !playing;
        if (playing) {
            gravity = 6;
        } else {
            gravity = 0;
        }
    }


    public boolean isPlaying() {
        return playing;
    }


    // Each index Corresponds to a bitmap
    public Bitmap getBitmap(char blockType) {
        return bitmapsArray[getBitmapIndex(blockType)];
    }

    // This method allows each GameObject which 'knows'
    // its type to get the correct index to its Bitmap
    // in the Bitmap array.
    public int getBitmapIndex(char blockType) {

        int index;
        switch (blockType) {
            case '.':
                index = 0;
                break;
            case 'p':
                index = 1;
                break;
            case 'c':
                index = 2;
                break;
            case 'u':
                index = 3;
                break;
            case 'e':
                index = 4;
                break;
            case 'f':
                index = 5;
                break;
            case 'd':
                index = 6;
                break;
            case 'g':
                index = 7;
                break;

            case '0':
                index = 10;
                break;
            case '1':
                index = 11;
                break;
            case 'i':
                index = 12;
                break;
            case 'l':
                index = 13;
                break;
            case '7':
                index = 14;
                break;

            case 'w':
                index = 15;
                break;
            case 'r':
                index = 16;
                break;
            case 'z':
                index = 17;
                break;

            case 't':
                index = 22;
                break;

            default:
                index = 0;
                break;
        }

        return index;
    }

    // For now we just load all the grass tiles
    // and the player. Soon we will have many GameObjects
    void loadMapData(Context context, int pixelsPerMetre, float px, float py) {

        char c;

        //Keep track of where we load our game objects
        int currentIndex = -1;
        int teleportIndex = -1;
        // how wide and high is the map? ManagerView needs to know
        mapHeight = levelData.tiles.size();
        mapWidth = levelData.tiles.get(0).length();

        for (int i = 0; i < levelData.tiles.size(); i++) {
            for (int j = 0; j < levelData.tiles.get(i).length(); j++) {

                c = levelData.tiles.get(i).charAt(j);
                if (c != '.') {// Don't want to load the empty spaces
                    currentIndex++;
                    switch (c) {
                        case '0':
                            // Add a tile to the gameObjects
                            gameObjects.add(new GOTileGrass(j, i, c));
                            break;
                        case '1':
                            // Add a tile to the gameObjects
                            gameObjects.add(new GOTileGrass(j, i, c));
                            break;
                        case 'i':
                            // Add a tile to the gameObjects
                            gameObjects.add(new GOTileGrass(j, i, c));
                            break;
                        case 'l':
                            // Add a tile to the gameObjects
                            gameObjects.add(new GOTileGrass(j, i, c));
                            break;
                        case '7':
                            // Add a tile to the gameObjects
                            gameObjects.add(new GOTileStone(j, i, c));
                            break;

                        case 'p':// a player
                            // Add a player to the gameObjects
                            gameObjects.add(new GOPlayer
                                    (context, px, py, pixelsPerMetre));

                            // We want the index of the player
                            playerIndex = currentIndex;
                            // We want a reference to the player object
                            player = (GOPlayer) gameObjects.get(playerIndex);

                            break;

                        case 'c':
                            // Add a coin to the gameObjects
                            gameObjects.add(new GOUsefulCoin(j, i, c));
                            break;
                        case 'u':
                            // Add a machine gun upgrade to the gameObjects
                            gameObjects.add(new GOUtilityUpgrade(j, i, c));
                            break;
                        case 'e':
                            // Add an extra life to the gameObjects
                            gameObjects.add(new GOUtilityLife(j, i, c));
                            break;

                        case 'd':
                            // Add a drone to the gameObjects
                            gameObjects.add(new GOEnemyDrone(j, i, c));
                            break;
                        case 'g':
                            // Add a guard to the gameObjects
                            gameObjects.add(new GOEnemyGuard(context, j, i, c, pixelsPerMetre));
                            break;
                        case 'f':
                            // Add a fire tile the gameObjects
                            gameObjects.add(new GOEnvFire(context, j, i, c, pixelsPerMetre));
                            break;


                        case 'w':
                            // Add a tree to the gameObjects
                            gameObjects.add(new GOEnvTree(j, i, c));
                            break;

                        case 'r':
                            // Add a stalactite to the gameObjects
                            gameObjects.add(new GOEnvLiana(j, i, c));
                            break;

                        case 'm':
                            // Add a cart to the gameObjects
                            gameObjects.add(new GOTileWater(j, i, c));
                            break;

                        case 'z':
                            // Add a boulders to the gameObjects
                            gameObjects.add(new GOEnvBoulder(j, i, c));
                            break;


                    }

                    // If the bitmap isn't prepared yet
                    if (bitmapsArray[getBitmapIndex(c)] == null) {
                        // Prepare it now and put it in the bitmapsArrayList
                        bitmapsArray[getBitmapIndex(c)] =
                                gameObjects.get(currentIndex).
                                        prepareBitmap(context,
                                                gameObjects.get(currentIndex).
                                                        getBitmapName(),
                                                pixelsPerMetre);

                    }
                }
            }
        }
    }

}
