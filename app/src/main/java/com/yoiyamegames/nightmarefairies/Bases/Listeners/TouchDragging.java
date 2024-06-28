package com.yoiyamegames.nightmarefairies.Bases.Listeners;

import static com.yoiyamegames.nightmarefairies.UI.WindowHandler.getScreenHeight;

import android.view.MotionEvent;
import android.view.View;

import com.yoiyamegames.nightmarefairies.Bases.Card;
import com.yoiyamegames.nightmarefairies.Bases.Implementable.Roomable;

import org.json.JSONException;

public class TouchDragging implements View.OnTouchListener {
    Card owner;
    Roomable roomer;
    public TouchDragging(Card owner, Roomable roomer){
        this.owner = owner;
        this.roomer = roomer;
    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(roomer.getTurn()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x_cord = (int) (event.getRawX() - v.getLayoutParams().width / 2);
                    int y_cord = (int) (event.getRawY() - v.getLayoutParams().height / 2);

                    v.setX(x_cord);
                    v.setY(y_cord);
                    break;
                case MotionEvent.ACTION_UP:
                    if (v.getY() < getScreenHeight(v.getContext(), 0.7f)) {
                        try {
                            owner.play();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        roomer.getPlayer().hand.updatePositions();
                    }
                    break;
                default:
                    break;
            }
        }
        return true;
    }
}
