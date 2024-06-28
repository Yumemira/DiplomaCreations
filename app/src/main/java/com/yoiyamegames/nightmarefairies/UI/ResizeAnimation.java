package com.yoiyamegames.nightmarefairies.UI;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

import com.yoiyamegames.nightmarefairies.Bases.Card;

public class ResizeAnimation extends Animation {
    View view;
    int fromLeft;
    int fromTop;
    int fromRight;
    int fromBottom;
    int toLeft;
    int toTop;
    int toRight;
    int toBottom;

    public ResizeAnimation(View v, int toLeft, int toTop, int toRight, int toBottom) {
        this.view = v;
        this.toLeft = toLeft;
        this.toTop = toTop;
        this.toRight = toRight;
        this.toBottom = toBottom;

        fromLeft = (int)view.getX();
        fromTop = (int)view.getY();
        fromRight = (int)view.getX()+view.getLayoutParams().width;
        fromBottom = (int)view.getY()+view.getLayoutParams().height;

        setDuration(Card.duration);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {

        float left = fromLeft + (toLeft - fromLeft) * interpolatedTime;
        float top = fromTop + (toTop - fromTop) * interpolatedTime;
        float right = fromRight + (toRight - fromRight) * interpolatedTime;
        float bottom = fromBottom + (toBottom - fromBottom) * interpolatedTime;

        ViewGroup.LayoutParams p = view.getLayoutParams();

        view.setX(left);
        view.setY(top);
        p.width = (int) ((right - left) + 1);
        p.height = (int) ((bottom - top) + 1);

        view.requestLayout();
    }
}
