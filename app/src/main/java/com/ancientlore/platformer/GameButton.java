package com.ancientlore.platformer;

import android.graphics.Rect;

/**
 * Created by Firefly on 5/22/2016.
 */
public class GameButton {
    String label;
    Rect rect;

    GameButton(final String label, final Rect rect){
        this.label=label;
        this.rect=rect;
    }
}
