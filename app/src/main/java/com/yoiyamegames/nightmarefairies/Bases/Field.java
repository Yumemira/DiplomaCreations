package com.yoiyamegames.nightmarefairies.Bases;

import android.content.Context;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.yoiyamegames.nightmarefairies.UI.WindowHandler;

import java.util.ArrayList;
import java.util.List;

public class Field {
    private float x;
    private float y;
    private float width;
    private float height;
    private float size;
    private List<Card> cards;
    WindowHandler cardUI;

    public Field(float x, float y, float width, float height, float size, ConstraintLayout layout, Context context) {
        cards = new ArrayList<>();
        this.size = size;
        cardUI = new WindowHandler(layout, context);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

    }

    public void addCardToList(Card card){
        cards.add(card);
        card.drawCard(x,y,Card.WIDTHDEFAULT, Card.HEIGHTDEFAULT, cardUI);
        updatePositions();
    }

    private void updatePositions(){
        int finalsize = countUnicCards();
        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i<cards.size();i++){
            if(!ids.contains(cards.get(i).getId())){
                ids.add(cards.get(i).getId());
            }
            cards.get(i).moveToPos(x+size*(ids.indexOf(cards.get(i).getId())/(finalsize-Card.WIDTHDEFAULT)), y, Card.WIDTHDEFAULT, Card.HEIGHTDEFAULT);
        }
    }



    public boolean removeByUid(int uid){
        for(Card card : cards){
            if(card.getUid()==uid){
                card.selfClear();
                cards.remove(card);
                return true;
            }
        }

        return false;
    }

    private int countUnicCards(){
        List<Integer> ids = new ArrayList<>();
        for(int i = 0; i<cards.size();i++){
            if(!ids.contains(cards.get(i).getId())) ids.add(cards.get(i).getId());
        }
        return ids.size();
    }
    public List<Card> getCards(){
        return cards;
    }
}
