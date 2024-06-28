package com.yoiyamegames.nightmarefairies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yoiyamegames.nightmarefairies.Bases.EthernetStatics;
import com.yoiyamegames.nightmarefairies.Bases.Player;
import com.yoiyamegames.nightmarefairies.Speciables.RoomManager;
import com.yoiyamegames.nightmarefairies.Speciables.StreamConnection;
import com.yoiyamegames.nightmarefairies.UI.WindowHandler;
import com.yoiyamegames.nightmarefairies.R;

import org.json.JSONObject;

import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    ConstraintLayout render;
    WindowHandler UI;
    StreamConnection connection;
    RoomManager roomManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            connection = new StreamConnection();
        } catch (URISyntaxException e){
            e.printStackTrace();
        }
        roomManager = new RoomManager();

        render = findViewById(R.id.layoutMainActivity);

        UI = new WindowHandler(render, getBaseContext());
        ImageView background_menu = UI.createImage();
        UI.setPlace(background_menu,0 ,0);
        UI.setSize(background_menu, 1,1);
        background_menu.setImageResource(R.drawable.background_menu);
        background_menu.setScaleType(ImageView.ScaleType.FIT_XY);

        Button play = UI.createButton();
        UI.setPlace(play, 0.2f, 0.6f);
        UI.setSize(play, 0.6f, 0.1f);
        play.setBackgroundResource(R.drawable.button_first);
        play.setText("Начать игру");


        play.setOnClickListener((v) -> {
        WindowHandler startWindow = new WindowHandler(render, getBaseContext());
            ImageView background = startWindow.createImage();
            startWindow.setPlace(background, 0, 0);
            startWindow.setSize(background, 1, 1);
            background.setOnClickListener((view)-> { startWindow.selfCollapse(); });

            ImageView windowImage = startWindow.createImage();
            startWindow.setPlace(windowImage, 0.01f, 0.16f);
            startWindow.setSize(windowImage, 0.98f, 0.68f);
            windowImage.setImageResource(R.drawable.background_window);
            windowImage.setOnClickListener((view) -> { });

            EditText playerName = startWindow.createEditView();
            startWindow.setPlace(playerName, 0.2f, 0.3f);
            startWindow.setSize(playerName, 0.6f,0.1f);
            playerName.setHint("Введите желаемое имя");

            EditText idText = startWindow.createEditView();
            startWindow.setPlace(idText, 0.2f, 0.4f);
            startWindow.setSize(idText, 0.6f, 0.1f);
            idText.setHint("Введите код комнаты");

            Button join = startWindow.createButton();
            startWindow.setPlace(join, 0.12f, 0.5f);
            startWindow.setSize(join, 0.36f, 0.1f);
            join.setText("Присоединиться");
            join.setBackgroundResource(R.drawable.button_first);
            join.setOnClickListener((view) -> {
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("player", playerName.getText().toString());
                            obj.put("id", idText.getText().toString());
                            JSONObject roomjs = new JSONObject(roomManager.post(EthernetStatics.ipaddr + "/join-room", obj.toString()));
                            if(roomjs.getBoolean("res")) {
                                Intent myIntent = new Intent(MainActivity.this, LobbyActivity.class);
                                Bundle b = new Bundle();
                                b.putBoolean("status", Player.MEMBER);
                                b.putString("player", playerName.getText().toString());
                                b.putString("roomid", roomjs.get("id").toString());
                                myIntent.putExtras(b);
                                startActivity(myIntent);
                            }
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            });

            Button create = startWindow.createButton();
            startWindow.setPlace(create, 0.52f,0.5f);
            startWindow.setSize(create, 0.36f, 0.1f);
            create.setText("Создать лобби");
            create.setBackgroundResource(R.drawable.button_first);
            create.setOnClickListener((view) -> {
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        try {
                            JSONObject obj = new JSONObject();
                            obj.put("player", playerName.getText().toString());
                            JSONObject roomjs = new JSONObject(roomManager.post(EthernetStatics.ipaddr + "/create-room", obj.toString()));
                            Intent myIntent = new Intent(MainActivity.this, LobbyActivity.class);
                            Bundle b = new Bundle();
                            b.putBoolean("status", Player.OWNER);
                            b.putString("player", playerName.getText().toString());
                            b.putString("roomid", roomjs.get("id").toString());
                            myIntent.putExtras(b);
                            startActivity(myIntent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
            });
        });


        Button information = UI.createButton();
        UI.setPlace(information, 0.2f, 0.8f);
        UI.setSize(information, 0.6f, 0.1f);
        information.setBackgroundResource(R.drawable.button_first);
        information.setText("Об игре");
    }
}