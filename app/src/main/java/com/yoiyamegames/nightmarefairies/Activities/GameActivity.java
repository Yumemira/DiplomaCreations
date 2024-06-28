package com.yoiyamegames.nightmarefairies.Activities;

import static com.yoiyamegames.nightmarefairies.UI.WindowHandler.getScreenHeight;
import static com.yoiyamegames.nightmarefairies.UI.WindowHandler.getScreenWidth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoiyamegames.nightmarefairies.Bases.Card;
import com.yoiyamegames.nightmarefairies.Bases.EthernetStatics;
import com.yoiyamegames.nightmarefairies.Bases.Field;
import com.yoiyamegames.nightmarefairies.Bases.Implementable.Roomable;
import com.yoiyamegames.nightmarefairies.Bases.Player;
import com.yoiyamegames.nightmarefairies.R;
import com.yoiyamegames.nightmarefairies.Speciables.RoomManager;
import com.yoiyamegames.nightmarefairies.Speciables.StreamConnection;
import com.yoiyamegames.nightmarefairies.UI.WindowHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.emitter.Emitter;


public class GameActivity extends AppCompatActivity implements Roomable {

    StreamConnection connection;
    RoomManager manager;

    ConstraintLayout render;
    ConstraintLayout ingameUILayout;
    WindowHandler fieldUI;
    Player player;
    Field publicField;
    List<Player> enemies;
    private String room;
    int time = 0;
    Boolean turn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        render = findViewById(R.id.layoutGameActivity);
        Bundle b = getIntent().getExtras(); // gets all from previous
        room = b.getString("room");

        // UI layout for fields
        generateUI();




        // instantiate public field


        // create player
        player = new Player(fieldUI, this, b.getString("player"),0.25f, 0.7f, 0.6f);

        // http request manager
        manager = new RoomManager();
        Thread thread = new Thread(() -> {
            JSONObject body = new JSONObject();
            try {
                body.put("room", room);
                body.put("player", player.getName());
                JSONObject gameState = new JSONObject(manager.post(EthernetStatics.ipaddr + "/get-gameinfo", body.toString()));

                JSONObject hand = new JSONObject(manager.post(EthernetStatics.ipaddr + "/get-hand", body.toString()));
                runOnUiThread(() -> {
                    try {
                        Log.i("turn", String.valueOf(gameState.getBoolean("turn")));
                        turn = gameState.getBoolean("turn");
                        instantiatePlayers(gameState.getJSONArray("players"));
                        generateDeck(gameState);
                        generateGraveyard(gameState); // TODO due to not completed
                        generatePublicField(gameState); // creates field that contains public cards
                        addToHand(hand); // adding all cards to hand in start
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        thread.start();

        try {
            connection = new StreamConnection();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        StreamConnection.socket.on("callback event", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        Log.i("updating", "player is attached to room " + args[0]);
                    }
                }).on("add to hand", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("hand", args[0].toString());
                    try {
                        addToHand((JSONObject)args[0]);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            })
            .on("start turn", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    runOnUiThread(() ->{
                        turn = true;
                        WindowHandler alert = new WindowHandler(ingameUILayout, getBaseContext());
                        TextView txt = alert.createTextView();
                        alert.setSize(txt, 0.8f,0.4f);
                        alert.setPlace(txt, 0.1f, 0.3f);
                        txt.setBackgroundColor(Color.argb(0.8f, 1000,400, 0));
                        txt.setText("Ваш ход");
                        txt.setTextSize(30);
                        txt.setGravity(Gravity.CENTER);
                        final Handler handler = new Handler();
                        handler.postDelayed(() -> alert.selfCollapse(), 1000);
                    });
                }
            })
            .on("add to field", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("field", args[0].toString());
                        runOnUiThread(() -> {
                            try {
                                JSONObject obj = (JSONObject) args[0];
                                publicField.addCardToList(new Card(obj.getJSONObject("card").getInt("id"),
                                        obj.getJSONObject("card").getInt("uid"), GameActivity.this));
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        });
                }
            })
            .on("add to player", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    Log.i("player", args[0].toString());

                    JSONObject obj = (JSONObject) args[0];
                    try {
                        if (player.getName().equals(obj.getString("player"))) {
                            runOnUiThread(() -> {
                                try {
                                    player.getField().addCardToList(new Card(obj.getJSONObject("card").getInt("id"),
                                            obj.getJSONObject("card").getInt("uid"), GameActivity.this));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        } else {
                            for (Player p : enemies) {
                                if (p.getName().equals(obj.getString("player"))) {
                                    runOnUiThread(() ->{
                                        try {
                                            p.getField().addCardToList(new Card(obj.getJSONObject("card").getInt("id"),
                                                    obj.getJSONObject("card").getInt("uid"), GameActivity.this));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });

                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            })
            .on("remove from player", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Log.i("plremoving", args[0].toString());
                        JSONObject obj = (JSONObject) args[0];
                        if (player.getName().equals(obj.getString("name"))) {
                            runOnUiThread(()->{
                                try {
                                    if(obj.getJSONObject("card").getInt("uid")!=-1) player.getField().removeByUid(obj.getJSONObject("card").getInt("uid"));
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                        } else {
                            for (Player p : enemies) {
                                if (p.getName().equals(obj.getString("name"))) {
                                    runOnUiThread(()-> {
                                        try {
                                            if(obj.getJSONObject("card").getInt("uid")!=-1) p.getField().removeByUid(obj.getJSONObject("card").getInt("uid"));
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                    break;
                                }
                            }
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            })
            .on("remove from field", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Log.i("errpoint", args[0].toString());
                    JSONArray arr = ((JSONObject) args[0]).getJSONArray("cards");

                            runOnUiThread(() ->{
                                for(int i = 0; i<arr.length();i++) {
                                    try {
                                        publicField.removeByUid(arr.getJSONObject(i).getInt("uid"));
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            })
            .on("add to graveyard",new Emitter.Listener() {
                @Override
                public void call(Object... args) {

                }
            })
            .on("choose one card", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    JSONArray arr = (JSONArray) args[0];
                    List<Card> cards = new ArrayList<>();
                    for(int i = 0; i< arr.length(); i++){
                        try {
                            cards.add(new Card(arr.getJSONObject(i).getInt("id"), arr.getJSONObject(i).getInt("uid"), GameActivity.this));
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    for(int i = 0; i<cards.size();i++){
                        cards.get(i).drawCard(0.1f+(0.55f/cards.size())*i,0.3f,0.35f,0.4f, fieldUI);
                    }
                }
            })
            .on("set time", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        time = ((JSONObject) args[0]).getInt("time");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            })
            .on("update player", new Emitter.Listener() {
                @Override
                public void call(Object... args) {
                    try {
                        Player recipient = null;
                        if (((JSONObject) args[0]).getString("name").equals(player.getName())) {
                            recipient = player;
                        } else {
                            for (Player p : enemies) {
                                if (p.getName().equals(((JSONObject) args[0]).getString("name"))) {
                                    recipient = p;
                                    break;
                                }
                            }
                        }

                        recipient.actualizeStatistics(Integer.parseInt(((JSONObject) args[0]).get("wp").toString()),
                                Integer.parseInt(((JSONObject) args[0]).get("armor").toString()),
                                Integer.parseInt(((JSONObject) args[0]).get("gold").toString()),
                                Integer.parseInt(((JSONObject) args[0]).get("stuff").toString()),
                                Integer.parseInt(((JSONObject) args[0]).get("sword").toString()));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        StreamConnection.socket.connect();

        StreamConnection.socket.emit("establish connection", room, player.getName());
    }

    private void instantiatePlayers(JSONArray players) throws JSONException{
        enemies = new ArrayList<>();
        for(int i = 0; i<players.length();i++){
            if(!players.getJSONObject(i).getString("name").equals(player.getName())){
                Player p = new Player(fieldUI, this, players.getJSONObject(i).getString("name"), 0.03f, 0.1f, 0.05f);
                for(int a = 0; a < players.getJSONObject(i).getJSONArray("home").length(); a++) {
                    p.getField().addCardToList(new Card(players.getJSONObject(i).getJSONArray("home").getJSONObject(a).getInt("id"),
                            players.getJSONObject(i).getJSONArray("home").getJSONObject(a).getInt("uid"), this));
                }
                p.setWp(players.getJSONObject(i).getInt("wp"));
                p.setArmor(players.getJSONObject(i).getInt("armor"));
                p.setGold(players.getJSONObject(i).getInt("gold"));
                p.setStuff(players.getJSONObject(i).getInt("stuff"));
                p.setSword(players.getJSONObject(i).getInt("sword"));
                enemies.add(p);
            } else {
                for(int a = 0; a < players.getJSONObject(i).getJSONArray("home").length(); a++) {
                    player.getField().addCardToList(new Card(players.getJSONObject(i).getJSONArray("home").getJSONObject(a).getInt("id"),
                            players.getJSONObject(i).getJSONArray("home").getJSONObject(a).getInt("uid"), this));
                }
                player.setWp(players.getJSONObject(i).getInt("wp"));
                player.setArmor(players.getJSONObject(i).getInt("armor"));
                player.setGold(players.getJSONObject(i).getInt("gold"));
                player.setStuff(players.getJSONObject(i).getInt("stuff"));
                player.setSword(players.getJSONObject(i).getInt("sword"));
            }
        }
    }

    private void generateDeck(JSONObject res) {
        ImageView deckBackgound = fieldUI.createImage();
        fieldUI.setSize(deckBackgound, 0.2f, 0.2f);
        fieldUI.setPlace(deckBackgound, 0.03f, 0.4f);

        deckBackgound.setImageResource(R.drawable.card_deck);
        deckBackgound.setOnClickListener((v) -> {
            TextView background = fieldUI.createTextView();
            fieldUI.setPlace(background, 0, 0);
            fieldUI.setSize(background, 1, 1);
            try {
                background.setText(res.get("deck").toString());
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            background.setTextSize(50);
            background.setTextColor(Color.WHITE);
            background.setGravity(Gravity.CENTER);
            background.setOnClickListener((view)-> { fieldUI.closeWindow(background); });
        });
    }

    // should be overrwriten
    private void generateGraveyard(JSONObject res) throws JSONException{
//        ImageView deckBackground = fieldUI.createImage();
//        fieldUI.setSize(deckBackground, 0.1f, 0.1f);
//        fieldUI.setPlace(deckBackground, 0.03f, 0.53f);
//        deckBackground.setImageResource(R.drawable.empty_card_background);
//
//        List<Integer> cards = new ArrayList<>();
//        JSONArray arr = res.getJSONArray("graveyard");
//        for(int i = 0; i<arr.length(); i++){
//            cards.add(arr.getInt(i));
//        }
//
//        graveDeck = new Deck(getBaseContext(), fieldUI, cards);
//
//        // TODO create slider
//        deckBackground.setOnClickListener((v) -> {
//            TextView background = fieldUI.createTextView();
//            fieldUI.setPlace(background, 0, 0);
//            fieldUI.setSize(background, 1, 1);
//            background.setText("" + graveDeck.getSize());
//            background.setTextSize(50);
//            background.setGravity(Gravity.CENTER);
//            background.setOnClickListener((view)-> { fieldUI.closeWindow(background); });
//        });
//
//        graveDeck.addDeckInteractivable(deckBackground);
    }

    private void generateUI(){
        ingameUILayout = new ConstraintLayout(getBaseContext());
        render.addView(ingameUILayout);
        ingameUILayout.setX(0);
        ingameUILayout.setY(0);
        ingameUILayout.getLayoutParams().width = getScreenWidth(getBaseContext(), 1);
        ingameUILayout.getLayoutParams().height = getScreenHeight(getBaseContext(), 1);
        ingameUILayout.setBackgroundResource(R.drawable.table_background);


        fieldUI = new WindowHandler(ingameUILayout, getBaseContext());
        ImageView topborder = fieldUI.createImage();
        fieldUI.setPlace(topborder, 0,0);
        fieldUI.setSize(topborder, 1f, 0.3f);
        topborder.setImageResource(R.drawable.top_border);
        topborder.setScaleType(ImageView.ScaleType.FIT_XY);

        ImageView botborder = fieldUI.createImage();
        fieldUI.setPlace(botborder, 0,0.7f);
        fieldUI.setSize(botborder, 1f, 0.3f);
        botborder.setImageResource(R.drawable.bot_border);
        botborder.setScaleType(ImageView.ScaleType.FIT_XY);

        Button endTurn = fieldUI.createButton();
        fieldUI.setPlace(endTurn,0.87f, 0.45f);
        fieldUI.setSize(endTurn, 0.8f, 0.1f);
        endTurn.setOnClickListener(view -> {
            turn=false;
            StreamConnection.socket.emit("end turn", room, player.getName());
        });
    }

    private void generatePublicField(JSONObject res) throws JSONException {
        publicField = new Field(0.25f, 0.4f, Card.WIDTHDEFAULT, Card.HEIGHTDEFAULT, 0.6f, ingameUILayout, this.getBaseContext());
        JSONArray arr = res.getJSONArray("field");
        for(int i = 0; i<arr.length(); i++){
            publicField.addCardToList(new Card(arr.getJSONObject(i).getInt("id"),arr.getJSONObject(i).getInt("uid"),this));
        }
    }



    private void addToHand(JSONObject res) throws JSONException{
        List<Card> cards = new ArrayList<>();
        JSONArray arr = res.getJSONArray("hand");
        for(int i = 0; i<arr.length(); i++){
            if(!player.hasCard(arr.getJSONObject(i).getInt("uid"))) cards.add(new Card(arr.getJSONObject(i).getInt("id"),
                    arr.getJSONObject(i).getInt("uid"), this));
        }
        runOnUiThread(() -> {
            player.hand.addCardsToHand(cards);
            player.hand.updatePositions();
        });
    }

    @Override
    public String getRoom() {return room;}

    @Override
    public String getPlayerName() {return player.getName();}

    @Override
    public List<Card> getDeck(){
        return publicField.getCards();
    }

    @Override
    public Field getPlayerHome() {
        return player.getField();
    }

    @Override
    public Field getEnemyHome(String name) {
        for(Player enemy : enemies){
            if(enemy.getName().equals(name)) return enemy.getField();
        }
        return null;
    }

    @Override
    public Player getPlayer() {
        return player;
    }
    @Override
    public boolean getTurn(){
        return turn;
    }
}