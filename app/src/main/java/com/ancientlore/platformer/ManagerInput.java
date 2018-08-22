package com.ancientlore.platformer;

import android.content.Context;
import android.graphics.Rect;
import android.view.MotionEvent;

import java.util.ArrayList;

public class ManagerInput {

    GameButton left;
    GameButton right;
    GameButton jump;
    GameButton shoot;
    GameButton pause;

    ManagerInput(Context context,int screenWidth, int screenHeight) {

        //Configure the player buttons
        int buttonWidth = screenWidth / 8;
        int buttonHeight = screenHeight / 7;
        int buttonPadding = screenWidth / 80;

        left = new GameButton(context.getResources().getString(R.string.ingame_left),new Rect(buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth,
                screenHeight - buttonPadding));

        right = new GameButton(context.getResources().getString(R.string.ingame_right),new Rect(buttonWidth + buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                buttonWidth + buttonPadding + buttonWidth,
                screenHeight - buttonPadding));

        jump = new GameButton(context.getResources().getString(R.string.ingame_jump),new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding - buttonHeight - buttonPadding));

        shoot = new GameButton(context.getResources().getString(R.string.ingame_shoot),new Rect(screenWidth - buttonWidth - buttonPadding,
                screenHeight - buttonHeight - buttonPadding,
                screenWidth - buttonPadding,
                screenHeight - buttonPadding));

        pause = new GameButton(context.getResources().getString(R.string.ingame_pause),new Rect(screenWidth - buttonPadding - buttonWidth,
                buttonPadding,
                screenWidth - buttonPadding,
                buttonPadding + buttonHeight));



    }

    public ArrayList getButtons(){
        //create an array of buttons for the draw method
        ArrayList<GameButton> currentButtonList = new ArrayList<>();
        currentButtonList.add(left);
        currentButtonList.add(right);
        currentButtonList.add(jump);
        currentButtonList.add(shoot);
        currentButtonList.add(pause);
        return  currentButtonList;
    }


    public void handleInput(MotionEvent motionEvent, ManagerLevel l, ManagerSound sound, ManagerView vp){
        int pointerCount = motionEvent.getPointerCount();

        for (int i = 0; i < pointerCount; i++) {

            int x = (int) motionEvent.getX(i);
            int y = (int) motionEvent.getY(i);

            if(l.isPlaying()) {

                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        if (right.rect.contains(x, y)) {
                            l.player.setPressingRight(true);
                            l.player.setPressingLeft(false);
                        } else if (left.rect.contains(x, y)) {
                            l.player.setPressingLeft(true);
                            l.player.setPressingRight(false);
                        } else if (jump.rect.contains(x, y)) {
                            l.player.startJump(sound);
                        } else if (shoot.rect.contains(x, y)) {
                            if (l.player.pullTrigger()) {
                                sound.playSound("shoot");
                            }
                        } else if (pause.rect.contains(x, y)) {
                            l.switchPlayingStatus();

                        }

                        break;


                    case MotionEvent.ACTION_UP:
                        if (right.rect.contains(x, y)) {
                            l.player.setPressingRight(false);
                        } else if (left.rect.contains(x, y)) {
                            l.player.setPressingLeft(false);
                        }


                        break;


                    case MotionEvent.ACTION_POINTER_DOWN:
                        if (right.rect.contains(x, y)) {
                            l.player.setPressingRight(true);
                            l.player.setPressingLeft(false);
                        } else if (left.rect.contains(x, y)) {
                            l.player.setPressingLeft(true);
                            l.player.setPressingRight(false);
                        } else if (jump.rect.contains(x, y)) {
                            l.player.startJump(sound);
                        } else if (shoot.rect.contains(x, y)) {
                            if (l.player.pullTrigger()) {
                                sound.playSound("shoot");
                            }
                        } else if (pause.rect.contains(x, y)) {
                            l.switchPlayingStatus();
                        }
                        break;


                    case MotionEvent.ACTION_POINTER_UP:
                        if (right.rect.contains(x, y)) {
                            l.player.setPressingRight(false);
                        } else if (left.rect.contains(x, y)) {
                            l.player.setPressingLeft(false);
                        }
                        break;
                }
            }else {// Not playing
                //Move the viewport around to explore the map
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:
                        if (right.rect.contains(x, y)) {
                            vp.moveViewportRight(l.mapWidth);
                            //Log.w("right:", "DOWN" );
                        } else if (left.rect.contains(x, y)) {
                            vp.moveViewportLeft();
                            //Log.w("left:", "DOWN" );
                        } else if (jump.rect.contains(x, y)) {
                            vp.moveViewportUp();
                            //Log.w("jump:", "DOWN" );/
                        } else if (shoot.rect.contains(x, y)) {
                            vp.moveViewportDown(l.mapHeight);
                            //Log.w("shoot:", "DOWN" );/
                        } else if (pause.rect.contains(x, y)) {
                            l.switchPlayingStatus();
                            //Log.w("pause:", "DOWN" );
                        }

                        break;


                }



            }
        }

    }
}
