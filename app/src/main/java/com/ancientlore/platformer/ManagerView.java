package com.ancientlore.platformer;
/*Description:
* controls visibility of the objects
* on screen
 */
import android.graphics.Rect;

class ManagerView {
    private Vector2Point5D currentWorldCenter;
    private Rect convertedRect;
    private int pixelsPerMetreX;
    private int pixelsPerMetreY;
    private int screenXResolution;
    private int screenYResolution;
    private int screenCenterX;
    private int screenCenterY;
    private int metresToShowX;
    private int metresToShowY;
    private int numClipped;

    ManagerView(int x, int y){
        screenXResolution = x;
        screenYResolution = y;

        screenCenterX = screenXResolution / 2;
        screenCenterY = screenYResolution / 2;

        pixelsPerMetreX = screenXResolution / 32;
        pixelsPerMetreY = screenYResolution / 18;

        metresToShowX = 34;
        metresToShowY = 20;

        convertedRect = new Rect();
        currentWorldCenter = new Vector2Point5D();
    }

    void moveViewportRight(int maxWidth){
        if(currentWorldCenter.x < maxWidth - (metresToShowX/2)+3) {

            currentWorldCenter.x += 1;
        }
    }

    void moveViewportLeft(){
        if(currentWorldCenter.x > (metresToShowX/2)-3){

            currentWorldCenter.x -= 1;
        }
    }

    void moveViewportUp(){
        if(currentWorldCenter.y > (metresToShowY /2)-3) {
            currentWorldCenter.y -= 1;
        }
    }

    void moveViewportDown(int maxHeight){
        if(currentWorldCenter.y < maxHeight - (metresToShowY / 2)+3) {
            currentWorldCenter.y += 1;
        }
    }

    void setWorldCenter(float x, float y){
        currentWorldCenter.x  = x;
        currentWorldCenter.y  = y;

    }

    Rect worldToScreen(float objectX, float objectY, float objectWidth, float objectHeight){
        int left = (int) (screenCenterX - ((currentWorldCenter.x - objectX) * pixelsPerMetreX));
        int top =  (int) (screenCenterY - ((currentWorldCenter.y - objectY) * pixelsPerMetreY));
        int right = (int) (left + (objectWidth * pixelsPerMetreX));
        int bottom = (int) (top + (objectHeight * pixelsPerMetreY));
        convertedRect.set(left, top, right, bottom);
        return convertedRect;
    }

    boolean clipObjects(float objectX, float objectY, float objectWidth, float objectHeight) {
        boolean clipped = true;

        if (objectX - objectWidth < currentWorldCenter.x + (metresToShowX / 2)) {
            if (objectX + objectWidth> currentWorldCenter.x - (metresToShowX / 2)) {
                if (objectY - objectHeight< currentWorldCenter.y + (metresToShowY / 2)) {
                    if (objectY + objectHeight > currentWorldCenter.y - (metresToShowY / 2)){
                        clipped = false;
                    }

                }
            }
        }

        //for debugging
        if(clipped){
            numClipped++;
        }

        return clipped;
    }

    int getScreenWidth(){return  screenXResolution;}
    int getScreenHeight(){return  screenYResolution;}
    int getPixelsPerMetreX(){return  pixelsPerMetreX;}
    int getPixelsPerMetreY(){return  pixelsPerMetreY;}
    int getCenterX(){return screenCenterX;}
    int getCenterY(){return screenCenterY;}
    float getWorldCenterX(){return currentWorldCenter.x;}
    float getWorldCenterY(){return currentWorldCenter.y;}
    int getNumClipped(){return numClipped;}

    void resetNumClipped(){numClipped = 0;}




}

