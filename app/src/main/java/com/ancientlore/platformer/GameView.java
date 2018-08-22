package com.ancientlore.platformer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class GameView extends SurfaceView implements Runnable {

    private boolean debugging = false;
    private volatile boolean running;
    private Thread gameThread = null;

    // For drawing
    private Paint paint;
    private Paint paintButtonText;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    Context context;

    // Our new engine classes
    private ManagerLevel lm;
    private ManagerView vp;
    ManagerInput im;
    ManagerSound sm;
    private PlayerState ps;

    long startFrameTime;
    long timeThisFrame;
    long fps;

    GameView(Context context, int screenWidth, int screenHeight) {
        super(context);
        this.context = context;

        // Initialize our drawing objects
        ourHolder = getHolder();
        paint = new Paint();

        paintButtonText=new Paint();
        paintButtonText.setColor(Color.BLACK);
        paintButtonText.setTextSize(screenWidth/25);
        paintButtonText.setTextAlign(Paint.Align.CENTER);

        // Initialize the viewport
        vp = new ManagerView(screenWidth, screenHeight);
        sm = ManagerSound.getInstance();
        sm.initialize(context);
        ps = new PlayerState();

        loadLevel("Level1", 1, 17);

    }

    public void loadLevel(String level, float px, float py) {
        // Make the ManagerLevel null
        // As this method can be called at any time
        // Including when ManagerLevel is not null.
        lm = null;

        // Create a new ManagerLevel
        // Passing in a Context, screen details, level name and player location
        lm = new ManagerLevel(context, vp.getPixelsPerMetreX(), vp.getScreenWidth(), im, level, px, py);
        im = new ManagerInput(context, vp.getScreenWidth(), vp.getScreenHeight());

        PointF location = new PointF(px, py);
        ps.saveLocation(location);
		
		// Reload the players current fire rate from the player state
        lm.player.bfg.setFireRate(ps.getFireRate());

        //set the players location as the world centre of the viewport
        vp.setWorldCenter(lm.gameObjects.get(lm.playerIndex)
                        .getWorldLocation().x,
                        lm.gameObjects.get(lm.playerIndex)
                        .getWorldLocation().y);
    }

    @Override
    public void run() {

        while (running) {
            startFrameTime = System.currentTimeMillis();
            update();
            draw();
            timeThisFrame = System.currentTimeMillis() - startFrameTime;
            if (timeThisFrame >= 1)
                fps = 1000 / timeThisFrame;
        }
    }

    private void update() {

        for (GameObject go : lm.gameObjects) {
            if (go.isActive()) {
                // Clip anything off-screen
                if (!vp.clipObjects(go.getWorldLocation().x, go.getWorldLocation().y, go.getWidth(), go.getHeight())) {
                    if(go.getType()=='g'){
                        if (((GOEnemyGuard)go).resurrectTimer==0){
                            ((GOEnemyGuard) go).alive=true;
                        }
                        else if (!((GOEnemyGuard)go).alive){
                            ((GOEnemyGuard) go).resurrectTimer--;
                            continue;
                        }
                    }
                    // Set visible flag to true
                    go.setVisible(true);

                    // check collisions with player
                    int hit = lm.player.checkCollisions(go.getHitbox());

                    if (hit > 0) {
                        //collision! Now deal with different types

                        switch (go.getType()) {

                            case 'c':
                                sm.playSound("coin_pickup");
                                go.setActive(false);
                                go.setVisible(false);
                                ps.gotCredit();
                                // Now restore velocity that was removed by collision detection
                                if (hit != 2) {// Any hit except feet
                                    lm.player.restorePreviousVelocity();
                                }
                                break;

                            case 'u':
                                sm.playSound("gun_upgrade");
                                go.setActive(false);
                                go.setVisible(false);
                                lm.player.bfg.upgradeRateOfFire();
                                if (hit != 2) {// Any hit except feet
                                    // Now restore velocity that was removed by collision detection
                                    lm.player.restorePreviousVelocity();
                                }
                                break;

                            case 'e':
                                //extralife
                                go.setActive(false);
                                go.setVisible(false);
                                sm.playSound("extra_life");
                                ps.addLife();
                                if (hit != 2) {// Any hit except feet
                                    // Now restore velocity that was removed by collision detection
                                    lm.player.restorePreviousVelocity();
                                }
                                break;

                            case 'd':
                                PointF location;
                                //hit by drone
                                sm.playSound("player_burn");
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;

                            case 'g':
                                //hit by guard
                                sm.playSound("player_burn");
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;

                            case 'f':
                                sm.playSound("player_burn");
                                ps.loseLife();
                                location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                                lm.player.setWorldLocationX(location.x);
                                lm.player.setWorldLocationY(location.y);
                                lm.player.setxVelocity(0);
                                break;

                            default:// Probably a regular tile
                                if (hit == 1) {// Left or right
                                    lm.player.setxVelocity(0);
                                    lm.player.setPressingRight(false);
                                }

                                if (hit == 2) {// Feet
                                    lm.player.isFalling = false;
                                }


                                break;

                        }
                    }

                    //Check bullet collisions
                    for (int i = 0; i < lm.player.bfg.getNumBullets(); i++) {
                        //Make a hitbox out of the the current bullet
                        RectHitbox r = new RectHitbox();
                        r.setLeft(lm.player.bfg.getBulletX(i));
                        r.setTop(lm.player.bfg.getBulletY(i));
                        r.setRight(lm.player.bfg.getBulletX(i) + .1f);
                        r.setBottom(lm.player.bfg.getBulletY(i) + .1f);

                        if (go.getHitbox().intersects(r)) {
                            //collision detected
                            //make bullet disappear until it is respawned as a new bullet
                            lm.player.bfg.hideBullet(i);

                            //Now respond depending upon the type of object hit
                            if (go.getType() != 'g'
                                    && go.getType() != 'd') {

                                sm.playSound("ricochet");
                            } else if (go.getType() == 'g') {
                                // Knock the guard back
                                if (((GOEnemyGuard)go).alive){
                                    ((GOEnemyGuard) go).alive = false;
                                    ((GOEnemyGuard) go).resurrectTimer=50;
                                }
                                //go.setWorldLocationX(go.getWorldLocation().x + 2 * (lm.player.bfg.getDirection(i)));
                                sm.playSound("hit_guard");
                            } else if (go.getType() == 'd') {
                                //destroy the droid
                                sm.playSound("explode");
                                //permanently clip this drone
                                go.setWorldLocation(-100, -100, 0);
                            }


                        }

                    }

                    if (lm.isPlaying()) {
                        // Run any un-clipped updates
                        go.update(fps, lm.gravity);

                        if (go.getType() == 'd') {// Let any near by drones know where the player is
                            GOEnemyDrone d = (GOEnemyDrone) go;
                            d.setWaypoint(lm.player.getWorldLocation());
                        }
                    }

                } else {
                    // Set visible flag to false
                    go.setVisible(false);
                    // Now draw() can ignore them
                }
            }

        }


        if (lm.isPlaying()) {
            //Reset the players location as the world centre of the viewport
            //if game is playing
            vp.setWorldCenter(lm.gameObjects.get(lm.playerIndex)
                            .getWorldLocation().x,
                    lm.gameObjects.get(lm.playerIndex)
                            .getWorldLocation().y);

            //Has player fallen out of the map?
            if (lm.player.getWorldLocation().x < 0 ||
                    lm.player.getWorldLocation().x > lm.mapWidth ||
                    lm.player.getWorldLocation().y > lm.mapHeight) {

                sm.playSound("player_burn");
                ps.loseLife();
                PointF location = new PointF(ps.loadLocation().x, ps.loadLocation().y);
                lm.player.setWorldLocationX(location.x);
                lm.player.setWorldLocationY(location.y);
                lm.player.setxVelocity(0);
            }

            // Check if game is over
            if (ps.getLives() == 0) {
                ps = new PlayerState();
                loadLevel("Level1", 1, 16);
            }
        }




        }

    private void drawBackground(int start, int stop) {

        Rect fromRect1 = new Rect();
        Rect toRect1 = new Rect();
        Rect fromRect2 = new Rect();
        Rect toRect2 = new Rect();

        for (Background bg : lm.backgrounds) {

            if (bg.z < start && bg.z > stop) {
                // Is this layer in the viewport?
                // Clip anything off-screen
                if (!vp.clipObjects(-1, bg.y, 1000, bg.height)) {

                    float floatstartY = ((vp.getCenterY() - ((vp.getWorldCenterY() - bg.y) * vp.getPixelsPerMetreY())));
                    int startY = (int) floatstartY;

                    float floatendY = ((vp.getCenterY() - ((vp.getWorldCenterY() - bg.endY) * vp.getPixelsPerMetreY())));
                    int endY = (int) floatendY;

                    //define what portion of bitmaps to capture and what coordinates to draw them at
                    fromRect1 = new Rect(0, 0, bg.width - bg.xClip, bg.height);
                    toRect1 = new Rect(bg.xClip, startY, bg.width, endY);

                    fromRect2 = new Rect(bg.width - bg.xClip, 0, bg.width, bg.height);
                    toRect2 = new Rect(0, startY, bg.xClip, endY);
                }

                //draw backgrounds
                if (!bg.reversedFirst) {
                    
                    canvas.drawBitmap(bg.bitmap, fromRect1, toRect1, paint);
                    canvas.drawBitmap(bg.bitmapReversed, fromRect2, toRect2, paint);
                } else {
                    canvas.drawBitmap(bg.bitmap, fromRect2, toRect2, paint);
                    canvas.drawBitmap(bg.bitmapReversed, fromRect1, toRect1, paint);
                }

                
                bg.xClip -= lm.player.getxVelocity() / (20 / bg.speed);
                if (bg.xClip >= bg.width) {
                    bg.xClip = 0;
                    bg.reversedFirst = !bg.reversedFirst;
                } else if (bg.xClip <= 0) {
                    bg.xClip = bg.width;
                    bg.reversedFirst = !bg.reversedFirst;

                }
            }
        }
    }

    private void draw() {

        if (ourHolder.getSurface().isValid()) {
            //First we lock the area of memory we will be drawing to
            canvas = ourHolder.lockCanvas();

            // Rub out the last frame with arbitrary color
            paint.setColor(Color.argb(255, 0, 0, 0));
            canvas.drawColor(Color.argb(255, 53, 99, 36));

            //draw parallax backgrounds from -1 to -3
            drawBackground(0, -3);

            // Draw all the GameObjects
            Rect toScreen2d = new Rect();

            // Draw a layer at a time
            for (int layer = -1; layer <= 1; layer++) {

                for (GameObject go : lm.gameObjects) {
                    if (go.isVisible() && go.getWorldLocation().z == layer) { //Only draw if visible and this layer
                        if (go.getType()=='g')
                            if (!((GOEnemyGuard)go).alive)
                                continue;
                        toScreen2d.set(vp.worldToScreen
                                (go.getWorldLocation().x,
                                        go.getWorldLocation().y,
                                        go.getWidth(),
                                        go.getHeight()));

                        if (go.isAnimated()) {
                            // Get the next frame of the bitmap
                            // Rotate if necessary
                            if (go.getFacing() == 1) {
                                //Rotate
                                
                                Matrix flipper = new Matrix();
                                flipper.preScale(-1, 1);
                                Rect r = go.getRectToDraw(System.currentTimeMillis());
                                Bitmap b = Bitmap.createBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                        r.left,
                                        r.top,
                                        r.width(),
                                        r.height(),
                                        flipper,
                                        true);

                                canvas.drawBitmap(b,
                                        toScreen2d.left,
                                        toScreen2d.top, paint);

                            } else {

                                // draw it the regular way round
                                canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                        go.getRectToDraw(System.currentTimeMillis()),
                                        toScreen2d, paint);
                            }


                        } else { // Just draw the whole bitmap
                            canvas.drawBitmap(lm.bitmapsArray[lm.getBitmapIndex(go.getType())],
                                    toScreen2d.left,
                                    toScreen2d.top, paint);
                        }
                    }
                }
            }


            //draw the bullets
            paint.setColor(Color.argb(255, 255, 255, 255));
            for (int i = 0; i < lm.player.bfg.getNumBullets(); i++) {

                // Pass in the x and y coords as usual
                // then .25 and .05 for the bullet width and height
                toScreen2d.set(vp.worldToScreen
                        (lm.player.bfg.getBulletX(i),
                                lm.player.bfg.getBulletY(i),
                                .25f,
                                .05f));

                canvas.drawRect(toScreen2d, paint);
            }

            // Draw parallax backgrounds from layer 1 to 3
            drawBackground(4, 0);

            // Draw the HUD
            // This code relies on the bitmaps from the extra life, upgrade and coin
            // Therefore there must be at least one of each in the level
            int topSpace = vp.getPixelsPerMetreY() / 4;
            int iconSize = vp.getPixelsPerMetreX();
            int padding = vp.getPixelsPerMetreX() / 5;
            int centring = vp.getPixelsPerMetreY() / 6;
            paint.setTextSize(vp.getPixelsPerMetreY()/2);
            paint.setTextAlign(Paint.Align.CENTER);

            paint.setColor(Color.argb(100, 0, 0, 0));
            canvas.drawRect(0,0,iconSize * 7.0f, topSpace*2 + iconSize,paint);
            paint.setColor(Color.argb(255, 255, 255, 0));

            canvas.drawBitmap(lm.getBitmap('e'), 0, topSpace, paint);

            canvas.drawText("" + ps.getLives(), (iconSize * 1) + padding, (iconSize) - centring, paint);

            canvas.drawBitmap(lm.getBitmap('c'), (iconSize * 2.5f) + padding, topSpace, paint);

            canvas.drawText("" + ps.getCredits(), (iconSize * 3.5f) + padding * 2, (iconSize) - centring, paint);

            canvas.drawBitmap(lm.getBitmap('u'), (iconSize * 5.0f) + padding, topSpace, paint);

            canvas.drawText("" + ps.getFireRate(), (iconSize * 6.0f) + padding * 2, (iconSize) - centring, paint);



            // Text for debugging
            if (debugging) {
                paint.setTextSize(16);
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                canvas.drawText("fps:" + fps, 10, 60, paint);
                canvas.drawText("num objects:" + lm.gameObjects.size(), 10, 80, paint);
                canvas.drawText("num clipped:" + vp.getNumClipped(), 10, 100, paint);
                canvas.drawText("playerX:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().x, 10, 120, paint);
                canvas.drawText("playerY:" + lm.gameObjects.get(lm.playerIndex).getWorldLocation().y, 10, 140, paint);

                canvas.drawText("Gravity:" + lm.gravity, 10, 160, paint);
                canvas.drawText("X velocity:" + lm.gameObjects.get(lm.playerIndex).getxVelocity(), 10, 180, paint);
                canvas.drawText("Y velocity:" + lm.gameObjects.get(lm.playerIndex).getyVelocity(), 10, 200, paint);

                //for reset the number of clipped objects each frame
                vp.resetNumClipped();

            }

            //draw buttons
            paint.setColor(Color.argb(80, 255, 255, 255));
            ArrayList<GameButton> buttonsToDraw;
            buttonsToDraw = im.getButtons();

            for (GameButton button : buttonsToDraw) {
                RectF rf = new RectF(button.rect.left, button.rect.top, button.rect.right, button.rect.bottom);
                canvas.drawRoundRect(rf, 15f, 15f, paint);
                canvas.drawText(button.label,button.rect.centerX(),button.rect.centerY()+paintButtonText.getTextSize()/4,paintButtonText);
            }

            //draw paused text
            if (!this.lm.isPlaying()) {
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setColor(Color.argb(255, 255, 255, 255));

                paint.setTextSize(120);
                canvas.drawText(getResources().getString(R.string.ingame_pause), vp.getScreenWidth() / 2, vp.getScreenHeight() / 2, paint);

            }

            // Unlock and draw the scene
            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (lm != null) {
            im.handleInput(motionEvent, lm, sm, vp);
        }
        //invalidate();
        return true;
    }

    // Clean up our thread if the game is interrupted or the player quits
    public void pause() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("error", "failed to pause thread");
        }
    }

    // Make a new thread and start it
    // Execution moves to our run method
    public void resume() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

}// End of PlatformView
