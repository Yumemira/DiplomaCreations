package com.yoiyamegames.nightmarefairies.Bases;

import com.yoiyamegames.nightmarefairies.Bases.Implementable.Roomable;
import com.yoiyamegames.nightmarefairies.UI.WindowHandler;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    List<Card> cards;
    WindowHandler windowHandler;
    Roomable roomer;
    public Hand(WindowHandler windowHandler, Roomable roomer){
        this.roomer = roomer;
        this.windowHandler = windowHandler;
        cards = new ArrayList<>();
    }

    public boolean hasCard(int uid){
        for(Card card : cards){
            if(card.getUid() == uid) return true;
        }
        return false;
    }

    public void addCardsToHand(List<Card> cards){
        for (int i = 0; i<cards.size(); i++){
            cards.get(i).drawCard(0.05f+0.5f/(cards.size()-1)*i, 0.4f, 0.4f, 0.4f, windowHandler);
            cards.get(i).setDraggable();
            this.cards.add(cards.get(i));
        }
    }

    public void updatePositions(){
        for (int i = 0; i<cards.size(); i++){
            cards.get(i).moveToPos(0.15f+0.4f/(cards.size()-1)*i, 0.9f, 0.3f, 0.3f);
        }
    }


    public void removeCard(int uid){
        for(Card card : cards){
            if(card.getUid() == uid) {
                card.selfClear();
                cards.remove(card);
                return;
            }
        }
    }
}
