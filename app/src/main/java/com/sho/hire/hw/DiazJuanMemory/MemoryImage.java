package com.sho.hire.hw.DiazJuanMemory;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.GridLayout;

/**
 * @author juandiaz <juandiaz@us.univision.com> Android Developer
 *         Copyright (C) 2016, Univision Communications Inc.
 *
 *         Sets up the GridLayout and keeps track of flips and match
 */
public class MemoryImage extends Button {

    private Long frontImageDrawableId;

    private boolean isFlipped = false;
    private boolean isMatch = false;

    protected Drawable front;
    protected Drawable back;


    public MemoryImage(Context context, int row, int column, Bitmap bitmap, Long frontImageDrawableId) {
        super(context);

        this.frontImageDrawableId = frontImageDrawableId;

        front = new BitmapDrawable(getResources(), bitmap);
        back = AppCompatDrawableManager.get().getDrawable(context, R.drawable.ic_pets_black_24dp);

        setBackground(back);

        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column));
        gridParams.width = (int) getResources().getDisplayMetrics().density * 75;
        gridParams.height = (int) getResources().getDisplayMetrics().density * 75;
        gridParams.setGravity(Gravity.CENTER);
        setLayoutParams(gridParams);

    }

    public boolean isMatch() {
        return isMatch;
    }

    public void setMatch(boolean match) {
        isMatch = match;
    }


    public Long getFrontImageDrawableId() {
        return frontImageDrawableId;
    }

    public void flip(){

        if(isMatch){
            return;
        }

        if(isFlipped){
            isFlipped = false;
            setBackground(back);
        } else {
            isFlipped = true;
            setBackground(front);
        }
    }
}
