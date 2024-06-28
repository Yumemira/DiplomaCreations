package com.yoiyamegames.nightmarefairies.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoiyamegames.nightmarefairies.R;
import com.yoiyamegames.nightmarefairies.Speciables.StreamConnection;
import com.yoiyamegames.nightmarefairies.UI.WindowHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LobbyActivity extends AppCompatActivity {
    Button startPlay;
    Button leftRoom;
    TextView roomId;
    WindowHandler windowHandler;
    WindowHandler membersUI;
    StreamConnection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        ConstraintLayout layout = findViewById(R.id.lobbyactivity);
        Bundle b = getIntent().getExtras();
        windowHandler = new WindowHandler(layout, getBaseContext());
        membersUI = new WindowHandler(layout, getBaseContext());
        try {
            connection = new StreamConnection();
            connection.socket.on("change members", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(() -> {
                        membersUI.selfCollapse();

                        TextView count = membersUI.createTextView();
                        membersUI.setSize(count, 0.8f, 0.1f);
                        membersUI.setPlace(count, 0.1f, 0.6f);
                        List<String> list = new ArrayList<>();
                        JSONArray arr = (JSONArray)args[0];
                        for(int i = 0; i < arr.length(); i++){
                            try {
                                list.add(arr.get(i).toString());
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        count.setBackgroundResource(R.drawable.textfield_first);
                        count.setGravity(Gravity.CENTER);
                        count.setText(String.format("Игроков: %d/4", list.size()));


                        for(int i = 0; i < list.size(); i++){
                            TextView player = membersUI.createTextView();
                            membersUI.setSize(player, 0.7f, 0.1f);
                            membersUI.setPlace(player, 0, 0.05f+0.11f*i);
                            player.setText(list.get(i));
                            player.setGravity(Gravity.CENTER);
                            player.setBackgroundResource(R.drawable.background_window_short);
                        }
                    });
                }
            }).on("to game", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Intent intent = new Intent(LobbyActivity.this, GameActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("room", b.getString("roomid"));
                    bundle.putString("player", b.getString("player"));

                    intent.putExtras(bundle);
                    connection.shutdownConnect();
                    startActivity(intent);
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                @Override
                public void call(Object... args) {}

            });
            connection.socket.connect();
            connection.socket.emit("subscribe", b.getString("roomid"));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ImageView background_menu = windowHandler.createImage();
        windowHandler.setSize(background_menu, 1,1);
        windowHandler.setPlace(background_menu, 0, 0);
        background_menu.setImageResource(R.drawable.background_menu);
        background_menu.setScaleType(ImageView.ScaleType.FIT_XY);

        leftRoom = windowHandler.createButton();
        windowHandler.setSize(leftRoom, 0.15f, 0.07f);
        windowHandler.setPlace(leftRoom, 0.85f,0);
        leftRoom.setBackgroundResource(R.drawable.button_left);
        leftRoom.setOnClickListener((v) -> {
            connection.socket.emit("unsubscribe", b.getString("roomid"), b.getString("player"));
            Intent intent = new Intent(LobbyActivity.this, MainActivity.class);
            startActivity(intent);
        });

        roomId = windowHandler.createTextView();
        windowHandler.setSize(roomId, 1, 0.1f);
        windowHandler.setPlace(roomId, 0, 0.75f);
        roomId.setText("Код комнаты: " + b.getString("roomid"));
        roomId.setGravity(Gravity.CENTER);
        roomId.setBackgroundResource(R.drawable.textfield_second);
        if(b.getBoolean("status")){
            startPlay = windowHandler.createButton();
            windowHandler.setSize(startPlay, 1, 0.1f);
            windowHandler.setPlace(startPlay, 0, 0.9f);
            startPlay.setText("Начать игру");
            startPlay.setGravity(Gravity.CENTER);
            startPlay.setBackgroundResource(R.drawable.button_first);
            startPlay.setOnClickListener((v)-> {
                connection.socket.emit("start game", b.getString("roomid"), b.getString("player"));
            });
        }


    }
}