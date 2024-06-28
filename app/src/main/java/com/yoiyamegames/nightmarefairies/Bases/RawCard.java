package com.yoiyamegames.nightmarefairies.Bases;

import android.content.Context;

public class RawCard {
    private int count;
    private int drawableID;



    public RawCard( int count, int drawableID){
        this.count = count;
        this.drawableID = drawableID;
    }

    public int getImgRes(){
        return drawableID;
    }

    public int getCount(){
        return count;
    }

    public boolean speciable(){
        return true;
    }
}
