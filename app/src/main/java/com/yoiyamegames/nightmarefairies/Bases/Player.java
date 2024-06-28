package com.yoiyamegames.nightmarefairies.Bases;

import android.graphics.Color;
import android.widget.TextView;

import com.yoiyamegames.nightmarefairies.Bases.Implementable.Roomable;
import com.yoiyamegames.nightmarefairies.UI.WindowHandler;

import java.util.ArrayList;
import java.util.List;

public class Player {
    public static boolean MEMBER = false;
    public static boolean OWNER = true;
    public boolean hideState = true;
    public Hand hand;

    private float yStatistics;

    Field home;
    WindowHandler windowHandler;
    Roomable roomer;

    private String name;
    private int wp;
    private int armor;
    private int gold;
    private int stuff;
    private int sword;
    private TextView wptxt;
    private TextView armortxt;
    private TextView goldtxt;
    private TextView stufftxt;
    private TextView swordtxt;

    public Player(WindowHandler windowHandler, Roomable roomer, String name, float x, float y, float yStatistics){
        this.yStatistics = yStatistics;
        home = new Field(x,y,Card.WIDTHDEFAULT, Card.HEIGHTDEFAULT, 0.5f, windowHandler.getRender(), windowHandler.getRender().getContext());
        this.roomer = roomer;
        this.windowHandler = windowHandler;
        hand = new Hand(windowHandler, roomer);
        this.name = name;
        createEssentials();
    }

    public Field getField(){
        return home;
    }
    public boolean hasCard(int uid){
        return hand.hasCard(uid);
    }

    public String getName(){
        return name;
    }

    public void actualizeStatistics(int wp, int armor, int gold, int stuff, int sword){
        setWp(wp);
        setArmor(armor);
        setGold(gold);
        setStuff(stuff);
        setSword(sword);
    }

    public void setWp(int wp){
        this.wp=wp;
        wptxt.setText("ПО: "+ wp);
    }
    public int getWp() {
        return wp;
    }
    public void setArmor(int armor){
        this.armor=armor;
        armortxt.setText("ЗЩТ: " + armor);
    }

    public int getArmor() {
        return armor;
    }
    public void setGold(int gold){
        this.gold=gold;
        goldtxt.setText("ЗЛТ: "+gold);
    }
    public int getGold() {
        return gold;
    }
    public void setStuff(int stuff){
        this.stuff=stuff;
        stufftxt.setText("ПСХ: "+stuff);
    }
    public int getStuff() {
        return stuff;
    }
    public void setSword(int sword){
        this.sword=sword;
        swordtxt.setText("МЕЧ: " + sword);
    }
    public int getSword() {
        return sword;
    }

    private void createEssentials(){
        wptxt = windowHandler.createTextView();
        windowHandler.setPlace(wptxt, 0.03f, yStatistics);
        windowHandler.setSize(wptxt, 0.2f, 0.05f);
        wptxt.setText("0");
        wptxt.setTextColor(Color.WHITE);
        armortxt = windowHandler.createTextView();
        windowHandler.setPlace(armortxt, 0.03f, yStatistics+0.05f);
        windowHandler.setSize(armortxt, 0.2f, 0.05f);
        armortxt.setText("0");
        armortxt.setTextColor(Color.WHITE);
        goldtxt = windowHandler.createTextView();
        windowHandler.setPlace(goldtxt, 0.03f, yStatistics+0.1f);
        windowHandler.setSize(goldtxt, 0.2f, 0.05f);
        goldtxt.setText("0");
        goldtxt.setTextColor(Color.WHITE);
        stufftxt = windowHandler.createTextView();
        windowHandler.setPlace(stufftxt, 0.03f, yStatistics+0.15f);
        windowHandler.setSize(stufftxt, 0.2f, 0.05f);
        stufftxt.setText("0");
        stufftxt.setTextColor(Color.WHITE);
        swordtxt = windowHandler.createTextView();
        windowHandler.setPlace(swordtxt, 0.03f, yStatistics+0.2f);
        windowHandler.setSize(swordtxt, 0.2f, 0.05f);
        swordtxt.setText("0");
        swordtxt.setTextColor(Color.WHITE);
    }

}