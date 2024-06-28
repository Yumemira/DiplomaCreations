package com.yoiyamegames.nightmarefairies.Bases;

import static com.yoiyamegames.nightmarefairies.UI.WindowHandler.getScreenHeight;
import static com.yoiyamegames.nightmarefairies.UI.WindowHandler.getScreenWidth;

import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yoiyamegames.nightmarefairies.Bases.Implementable.Roomable;
import com.yoiyamegames.nightmarefairies.Bases.Listeners.TouchDragging;
import com.yoiyamegames.nightmarefairies.R;
import com.yoiyamegames.nightmarefairies.Speciables.StreamConnection;
import com.yoiyamegames.nightmarefairies.UI.WindowHandler;
import com.yoiyamegames.nightmarefairies.UI.ResizeAnimation;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class Card {
    public static long duration = 600;
    public static float WIDTHDEFAULT = 0.2f;
    public static float HEIGHTDEFAULT = 0.2f;

    Roomable roomer;
    private int id;
    private int uid;
    public ImageView image;
    WindowHandler windowHandler;
    int xPos;
    int yPos;

    private int type;

    public Card(int id, int uid, Roomable roomer){
        this.roomer = roomer;
        this.uid = uid;
        this.id = id;
        if(id==0||id==15||id==19||id==20){
            type = 0;
        } else if (id == 7 || id==9 || id==16 || id==17 || id==18) {
            type = 1;
        } else if (id == 2 || id == 4 || id == 13 || id == 14) {
            type = 2;
        } else if (id == 6 || id == 10 || id == 11 || id == 12){
            type = 3;
        } else {
            type = 4;
        }
    }

    public void drawCard(float x, float y, float width, float height, WindowHandler windowHandler) {
        this.windowHandler = windowHandler;
        image = windowHandler.createImage();
        windowHandler.setSize(image, width, height);
        windowHandler.setPlace(image, x, y);
        image.setImageResource(cardIdToDraw());

    }

    public void moveToPos(float x, float y, float width, float height){
        Animation animation = new ResizeAnimation(image, getScreenWidth(image.getContext(), x),
                getScreenHeight(image.getContext(), y), getScreenWidth(image.getContext(), x+width),
                getScreenHeight(image.getContext(), y+height));
        image.startAnimation(animation);
        xPos = getScreenWidth(image.getContext(), x);
        yPos = getScreenHeight(image.getContext(), y);
    }

    public void setDraggable(){
        image.setOnTouchListener(new TouchDragging(this, roomer));
    }

    public void play() throws JSONException {
        WindowHandler frameUI = new WindowHandler(windowHandler.getRender(), image.getContext());
        image.setVisibility(View.INVISIBLE);
        TextView header = frameUI.createTextView();
        frameUI.setPlace(header, 0f, 0f);
        frameUI.setSize(header, 1f, 0.1f);
        header.setBackgroundColor(Color.argb(0.2f, 256,256,256));
        header.setGravity(Gravity.CENTER);
        JSONObject essentials = new JSONObject();
        essentials.put("id", id);
        essentials.put("uid", uid);
        essentials.put("room", roomer.getRoom());
        essentials.put("player", roomer.getPlayerName());

        Button goback = frameUI.createButton();
        frameUI.setPlace(goback, 0f,0f);
        frameUI.setSize(goback, 0.1f, 0.05f);
        goback.setOnClickListener(v -> {
            frameUI.selfCollapse();
            image.setX(xPos);
            image.setY(yPos);
            image.setVisibility(View.VISIBLE);
        });

        Button doPlay = frameUI.createButton();
        frameUI.setPlace(doPlay, 0.3f, 0.85f);
        frameUI.setSize(doPlay, 0.4f, 0.1f);
        doPlay.setText("Сыграть карту");


        switch (id){
            case 0:
                header.setText("Возьмите 2 карты из колоды, +2 ПО за каждого разыгранного купца");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 1:
                header.setText("Возьмите из запаса 1 доспех или 1 золотую монету");

                doPlay.setOnClickListener(view -> {
                    view.setVisibility(View.INVISIBLE);

                    Button cancelPlay = frameUI.createButton();
                    frameUI.setPlace(cancelPlay, 0.3f, 0.85f);
                    frameUI.setSize(cancelPlay, 0.4f, 0.1f);
                    cancelPlay.setText("Отменить");
                    cancelPlay.setOnClickListener(v -> {
                        view.setVisibility(View.VISIBLE);
                        frameUI.closeWindow(cancelPlay);
                    });

                    Button armor = frameUI.createButton();
                    frameUI.setSize(armor,0.3f, 0.1f);
                    frameUI.setPlace(armor, 0.1f, 0.6f);
                    armor.setText("Доспех");
                    armor.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 1);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button gold = frameUI.createButton();
                    frameUI.setSize(gold,0.3f, 0.1f);
                    frameUI.setPlace(gold, 0.6f, 0.6f);
                    gold.setText("Монета");
                    gold.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 1);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });

                break;
            case 2:
                header.setText("+2 ПО за каждого разыгранного злодея, сбросьте разыгранное место");
                doPlay.setOnClickListener(view -> {
                    view.setVisibility(View.INVISIBLE);

                    Button cancelPlay = frameUI.createButton();
                    frameUI.setPlace(cancelPlay, 0.3f, 0.85f);
                    frameUI.setSize(cancelPlay, 0.4f, 0.1f);
                    cancelPlay.setText("Отменить");
                    cancelPlay.setOnClickListener(v -> {
                        view.setVisibility(View.VISIBLE);
                        frameUI.closeWindow(cancelPlay);
                    });
                    List<Card> publicField = roomer.getDeck();
                    for(Card slot : publicField){
                        if(slot.getType()==4) slot.setOnClickListener(v -> {

                            ImageView cancelBackground = frameUI.createImage();
                            frameUI.setPlace(cancelBackground, 0, 0);
                            frameUI.setSize(cancelBackground, 1f, 1f);
                            Button playThis = frameUI.createButton();
                            frameUI.setPlace(playThis, 0.3f, 0.85f);
                            frameUI.setSize(playThis, 0.4f, 0.1f);
                            cancelBackground.setOnClickListener(v1 -> {
                                frameUI.closeWindow(playThis);
                                frameUI.closeWindow(cancelBackground);
                            });
                            playThis.setOnClickListener(v1 -> {
                                try {
                                    essentials.put("place", slot.getUid());
                                    playCard(essentials, frameUI);
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        });

                    }


                });
                    break;
            case 3:
                header.setText("Сейчас день, все разыгранные карты ночь сброшены");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 4:
                header.setText("Сбросьте 1 разыгранное место и 1 разыгранного мужчину, +2 ПО за каждую сброшенную карту");
                doPlay.setOnClickListener(view -> {
                    try {
                        view.setVisibility(View.INVISIBLE);
                        essentials.put("place", -1);
                        essentials.put("man", -1);
                        Button playThis = frameUI.createButton();
                        frameUI.setPlace(playThis, 0.85f, 0);
                        frameUI.setSize(playThis, 0.15f, 0.05f);
                        playThis.setOnClickListener(v -> {
                            playCard(essentials, frameUI);
                        });

                        List<Card> publicField = roomer.getDeck();
                        for(Card slot : publicField){
                            if(slot.getType()==4) {
                                slot.setOnClickListener(v -> {
                                    ImageView cancelBackground = frameUI.createImage();
                                    frameUI.setPlace(cancelBackground, 0, 0);
                                    frameUI.setSize(cancelBackground, 1f, 1f);
                                    Button addThis = frameUI.createButton();
                                    frameUI.setPlace(addThis, 0.3f, 0.85f);
                                    frameUI.setSize(addThis, 0.4f, 0.1f);
                                    addThis.setText("Выбрать");
                                    cancelBackground.setOnClickListener(v1 -> {
                                        frameUI.closeWindow(addThis);
                                        frameUI.closeWindow(cancelBackground);
                                    });
                                    addThis.setOnClickListener(v1 -> {
                                        try {
                                            essentials.put("place", slot.getUid());
                                            frameUI.closeWindow(addThis);
                                            frameUI.closeWindow(cancelBackground);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                });
                            } else if(slot.getType()==0) {
                                slot.setOnClickListener(v -> {
                                    ImageView cancelBackground = frameUI.createImage();
                                    frameUI.setPlace(cancelBackground, 0, 0);
                                    frameUI.setSize(cancelBackground, 1f, 1f);
                                    Button addThis = frameUI.createButton();
                                    frameUI.setPlace(addThis, 0.3f, 0.85f);
                                    frameUI.setSize(addThis, 0.4f, 0.1f);
                                    addThis.setText("Выбрать");
                                    cancelBackground.setOnClickListener(v1 -> {
                                        frameUI.closeWindow(addThis);
                                        frameUI.closeWindow(cancelBackground);
                                    });
                                    addThis.setOnClickListener(v1 -> {
                                        try {
                                            essentials.put("man", slot.getUid());
                                            frameUI.closeWindow(addThis);
                                            frameUI.closeWindow(cancelBackground);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                });
                            }

                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                });
                break;
            case 5:
                header.setText("Возьмите 1 карту из колоды. Если сейчас день все игроки берут по 1 карте из колоды. Если сейчас ночь все игроки сбрасывют по 1 карте с руки");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 6:
                doPlay.setOnClickListener(view -> {
                    header.setText("Возьмите из запаса 1 предмет: доспех, волшебную палку, золотую монету или меч");
                    Button armorb = frameUI.createButton();
                    frameUI.setSize(armorb, 0.3f, 0.1f);
                    frameUI.setPlace(armorb, 0.1f, 0.6f);
                    armorb.setText("Доспех");
                    armorb.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 1);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button goldb = frameUI.createButton();
                    frameUI.setSize(goldb, 0.3f, 0.1f);
                    frameUI.setPlace(goldb, 0.6f, 0.6f);
                    goldb.setText("Монета");
                    goldb.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 1);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button wizardrystaff = frameUI.createButton();
                    frameUI.setSize(wizardrystaff, 0.3f, 0.1f);
                    frameUI.setPlace(wizardrystaff, 0.1f, 0.7f);
                    wizardrystaff.setText("Волшебная палка");
                    wizardrystaff.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 1);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button sword = frameUI.createButton();
                    frameUI.setSize(sword, 0.3f, 0.1f);
                    frameUI.setPlace(sword, 0.6f, 0.7f);
                    sword.setText("Меч");
                    sword.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 1);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                break;
            case 7:
                header.setText("Возьмите из запаса 1 волшебную палку или 1 меч");
                doPlay.setOnClickListener(view -> {
                    Button staff = frameUI.createButton();
                    frameUI.setSize(staff, 0.3f, 0.1f);
                    frameUI.setPlace(staff, 0.1f, 0.6f);
                    staff.setText("Волшебная палка");
                    staff.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 1);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button swordb = frameUI.createButton();
                    frameUI.setSize(swordb, 0.3f, 0.1f);
                    frameUI.setPlace(swordb, 0.6f, 0.6f);
                    swordb.setText("Меч");
                    swordb.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 1);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                break;
            case 8:
                header.setText("Возьмите из запаса 1 волшебную палку или 1 золотую монету");
                doPlay.setOnClickListener(view -> {
                    Button staffb = frameUI.createButton();
                    frameUI.setSize(staffb, 0.3f, 0.1f);
                    frameUI.setPlace(staffb, 0.1f, 0.6f);
                    staffb.setText("Волшебная палка");
                    staffb.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 1);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button goldc = frameUI.createButton();
                    frameUI.setSize(goldc, 0.3f, 0.1f);
                    frameUI.setPlace(goldc, 0.6f, 0.6f);
                    goldc.setText("Монета");
                    goldc.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 1);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                break;
            case 9:
                header.setText("Все игроки раскрывают карты на руках. +1 ПО и +1 ПО за каждое раскрытое место. Возьмите 1 карту из колоды за каждую раскрытую циганку");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 10:
                header.setText("Возьмите 1 карту из колоды. Если сейчас день, +1 ПО за каждое разыгранное место. Если сейчас ночь, +2 ПО за каждое разыгранное место");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 11:
                header.setText("Возьмите 1 карту из колоды. Если сейчас день, +2 ПО за каждое разыгранное место. Если сейчас ночь, +1 ПО за каждое разыгранное место");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 12:
                header.setText("Сейчас ночь, все разыгранные карты день сброшены");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 13:
                header.setText("Если сейчас ночь, получите 2 ПО в конце своего хода");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 14:
                header.setText("Возьмите 1 разыгранное место и 1 разыгранную женщину");

                doPlay.setOnClickListener(view -> {
                    try {
                        view.setVisibility(View.INVISIBLE);
                        essentials.put("place", -1);
                        essentials.put("woman", -1);
                        Button playThis = frameUI.createButton();
                        frameUI.setPlace(playThis, 0.85f, 0);
                        frameUI.setSize(playThis, 0.15f, 0.05f);
                        playThis.setOnClickListener(v -> {
                            playCard(essentials, frameUI);
                        });

                        List<Card> publicField = roomer.getDeck();
                        for(Card slot : publicField){
                            if(slot.getType()==4) {
                                slot.setOnClickListener(v -> {
                                    ImageView cancelBackground = frameUI.createImage();
                                    frameUI.setPlace(cancelBackground, 0, 0);
                                    frameUI.setSize(cancelBackground, 1f, 1f);
                                    Button addThis = frameUI.createButton();
                                    frameUI.setPlace(addThis, 0.3f, 0.85f);
                                    frameUI.setSize(addThis, 0.4f, 0.1f);
                                    addThis.setText("Выбрать");
                                    cancelBackground.setOnClickListener(v1 -> {
                                        frameUI.closeWindow(addThis);
                                        frameUI.closeWindow(cancelBackground);
                                    });
                                    addThis.setOnClickListener(v1 -> {
                                        try {
                                            essentials.put("place", slot.getUid());
                                            frameUI.closeWindow(addThis);
                                            frameUI.closeWindow(cancelBackground);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                });
                            } else if(slot.getType()==1) {
                                slot.setOnClickListener(v -> {
                                    ImageView cancelBackground = frameUI.createImage();
                                    frameUI.setPlace(cancelBackground, 0, 0);
                                    frameUI.setSize(cancelBackground, 1f, 1f);
                                    Button addThis = frameUI.createButton();
                                    frameUI.setPlace(addThis, 0.3f, 0.85f);
                                    frameUI.setSize(addThis, 0.4f, 0.1f);
                                    addThis.setText("Выбрать");
                                    cancelBackground.setOnClickListener(v1 -> {
                                        frameUI.closeWindow(addThis);
                                        frameUI.closeWindow(cancelBackground);
                                    });
                                    addThis.setOnClickListener(v1 -> {
                                        try {
                                            essentials.put("woman", slot.getUid());
                                            frameUI.closeWindow(addThis);
                                            frameUI.closeWindow(cancelBackground);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                });
                            }

                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                });
                break;
            case 15:
                header.setText("Если сейчас день, +2 ПО в конце хода");

                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                break;
            case 16:
                header.setText("Возьмите на руку 1 разыгранного мужчину");
                doPlay.setOnClickListener(view -> {
                    try {
                        view.setVisibility(View.INVISIBLE);
                        essentials.put("man", -1);
                        Button playThis = frameUI.createButton();
                        frameUI.setPlace(playThis, 0.85f, 0);
                        frameUI.setSize(playThis, 0.15f, 0.05f);
                        playThis.setOnClickListener(v -> {
                            playCard(essentials, frameUI);
                        });

                        List<Card> publicField = roomer.getDeck();
                        for(Card slot : publicField){
                            if(slot.getType()==0) {
                                slot.setOnClickListener(v -> {
                                    ImageView cancelBackground = frameUI.createImage();
                                    frameUI.setPlace(cancelBackground, 0, 0);
                                    frameUI.setSize(cancelBackground, 1f, 1f);
                                    Button addThis = frameUI.createButton();
                                    frameUI.setPlace(addThis, 0.3f, 0.85f);
                                    frameUI.setSize(addThis, 0.4f, 0.1f);
                                    addThis.setText("Выбрать");
                                    cancelBackground.setOnClickListener(v1 -> {
                                        frameUI.closeWindow(addThis);
                                        frameUI.closeWindow(cancelBackground);
                                    });
                                    addThis.setOnClickListener(v1 -> {
                                        try {
                                            essentials.put("man", slot.getUid());
                                            frameUI.closeWindow(addThis);
                                            frameUI.closeWindow(cancelBackground);
                                        } catch (JSONException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                });
                            }

                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                });
                // TODO part
                break;
            case 17:
                header.setText("Посмотрите 2 верхние карты колоды. Добавьте одну из них на руку, а вторую удалите из игры. Можете разыграть 1 дополнительную карту в этом ходу");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                // TODO part
                break;
            case 18:
                header.setText("Вы не можете брать карты из колоды. Можете играть 1 дополнительную карту в свой ход. Сбросьте ведьму в начале своего хода, если у вас не осталось карт на руке или если карты закончились в колоде");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                // TODO part
                break;
            case 19:
                header.setText("Разыграйте верхнюю карту колоды");
                doPlay.setOnClickListener(v -> {
                    playCard(essentials, frameUI);
                });
                // TODO part
                break;
            case 20:
                header.setText("Возьмите из запаса 1 доспех или 1 меч");
                doPlay.setOnClickListener(view -> {
                    Button armord = frameUI.createButton();
                    frameUI.setSize(armord, 0.3f, 0.1f);
                    frameUI.setPlace(armord, 0.1f, 0.6f);
                    armord.setText("Доспех");
                    armord.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 1);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 0);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    Button swordd = frameUI.createButton();
                    frameUI.setSize(swordd, 0.3f, 0.1f);
                    frameUI.setPlace(swordd, 0.6f, 0.6f);
                    swordd.setText("Меч");
                    swordd.setOnClickListener(v -> {
                        try {
                            essentials.put("armor", 0);
                            essentials.put("gold", 0);
                            essentials.put("stuff", 0);
                            essentials.put("sword", 1);
                            playCard(essentials, frameUI);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                });
                break;
            default:
                header.setText("Unknown card. If you see it that means you missing some resources or found bug");
        }
    }


    private int cardIdToDraw(){
        switch (id){
            case 0:
                return R.drawable.blacksmith;
            case 1:
                return R.drawable.cursed_custle;
            case 2:
                return R.drawable.dark_lord;
            case 3:
                return R.drawable.day;
            case 4:
                return R.drawable.dragon;
            case 5:
                return R.drawable.enchant_forest;
            case 6:
                return R.drawable.enchantness;
            case 7:
                return R.drawable.fairy;
            case 8:
                return R.drawable.forgotten_cave;
            case 9:
                return R.drawable.gypsy;
            case 10:
                return R.drawable.king_madness;
            case 11:
                return R.drawable.king_wisdom;
            case 12:
                return R.drawable.night;
            case 13:
                return R.drawable.night_child;
            case 14:
                return R.drawable.ogre;
            case 15:
                return R.drawable.old_sage;
            case 16:
                return R.drawable.princess;
            case 17:
                return R.drawable.secret_helper;
            case 18:
                return R.drawable.witch;
            case 19:
                return R.drawable.wizard;
            case 20:
                return R.drawable.yong_hero;
            default:
                return -1;
        }
    }

    public int getType(){
        return type;
    }

    public int getUid(){
        return uid;
    }
    public int getId(){ return id;}

    public void setOnClickListener(View.OnClickListener listener){
        image.setOnClickListener(listener);
    }

    public void selfClear(){
        windowHandler.closeWindow(image);
    }

    private void playCard(JSONObject essentials, WindowHandler handler){
        handler.selfCollapse();
        roomer.getPlayer().hand.removeCard(this.getUid());
        StreamConnection.socket.emit("play card", essentials);
    }
}