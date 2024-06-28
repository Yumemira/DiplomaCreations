package com.yoiyamegames.nightmarefairies.Bases;

import android.content.Context;
import android.widget.ImageView;

import com.yoiyamegames.nightmarefairies.UI.WindowHandler;

import java.util.List;

public class Deck {
    WindowHandler windowHandler;
    Context context;
    private List<Integer> cards;
    ImageView image;

    public Deck(Context context, WindowHandler windowHandler, List<Integer> cards){
        this.windowHandler = windowHandler;
        this.context = context;
        this.cards = cards;
    }

    public int getSize() { return cards.size();}
    public void addDeckInteractivable(ImageView image) {
        this.image = image;
    }
}
