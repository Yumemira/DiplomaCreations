package com.yoiyamegames.nightmarefairies.Bases.Implementable;

import com.yoiyamegames.nightmarefairies.Bases.Card;
import com.yoiyamegames.nightmarefairies.Bases.Field;
import com.yoiyamegames.nightmarefairies.Bases.Player;

import java.util.List;

public interface Roomable {
    public String getRoom();
    public String getPlayerName();
    public List<Card> getDeck();
    public Field getPlayerHome();
    public Field getEnemyHome(String name);

    public Player getPlayer();
    public boolean getTurn();
}
