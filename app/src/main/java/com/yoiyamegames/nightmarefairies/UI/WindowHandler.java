package com.yoiyamegames.nightmarefairies.UI;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class WindowHandler {
    private Context context;
    private ConstraintLayout render;
    private List<View> content;

    public WindowHandler(ConstraintLayout layout, Context context){
        this.context = context;
        content = new ArrayList<>();
        render = new ConstraintLayout(context);
        layout.addView(render);
        render.setX(0);
        render.setY(0);
        render.getLayoutParams().width = getScreenWidth(context, 1);
        render.getLayoutParams().height = getScreenHeight(context, 1);
    }



    public void closeWindow(View view){
        if(content.remove(view)){
            render.removeView(view);
        }
    }

    public void selfCollapse(){
        for (View view : content){
            render.removeView(view);
        }
        content.clear();
    }

    public void setPlace(View view, float xPercents, float yPercents){
        view.setX(getScreenWidth(context, xPercents));
        view.setY(getScreenHeight(context, yPercents));
    }

    public void setSize(View view, float xPercents, float yPercents){
        if(content.contains(view)){
            view.getLayoutParams().width=getScreenWidth(context, xPercents);
            view.getLayoutParams().height=getScreenHeight(context, yPercents);
        }
    }

    public ConstraintLayout getRender(){
        return render;
    }

    public Button createButton(){
        Button button = new Button(context);
        addWindow(button);
        return button;
    }

    public ImageView createImage(){
        ImageView image = new ImageView(context);
        addWindow(image);
        return image;
    }

    public TextView createTextView(){
        TextView text = new TextView(context);
        addWindow(text);
        return text;
    }

    public EditText createEditView(){
        EditText text = new EditText(context);
        addWindow(text);
        return text;
    }

    private void addWindow(View view){
        render.addView(view);
        content.add(view);
    }


    public static int getScreenWidth(@NonNull Context context, float scale){ return (int)(context.getResources().getDisplayMetrics().widthPixels*scale); }

    public static int getScreenHeight(@NonNull Context context, float scale){ return  (int)(context.getResources().getDisplayMetrics().heightPixels*scale); }
}