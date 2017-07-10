package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2016/11/16.
 */
public class MonthExitAnim extends BaseViewAnimator {
    float startPivotX, startPivotY;
    float stopPivotX, stopPivotY;

    public MonthExitAnim(float startPivotX, float startPivotY, float stopPivotX, float stopPivotY) {
        this.startPivotX = startPivotX;
        this.startPivotY = startPivotY;
        this.stopPivotX = stopPivotX;
        this.stopPivotY = stopPivotY;
    }

    @Override
    protected void prepare(View target) {
        ViewGroup parent = (ViewGroup)target.getParent();
        float scaleVio = (float)(parent.getWidth() / 3)  / target.getWidth();
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target,"pivotX", stopPivotX, stopPivotX),
                ObjectAnimator.ofFloat(target,"pivotY", stopPivotY, stopPivotY),
                ObjectAnimator.ofFloat(target, "alpha", 0.1f, 0),
                ObjectAnimator.ofFloat(target, "scaleX", 1, scaleVio),
                ObjectAnimator.ofFloat(target, "scaleY", 1, scaleVio)
        );
    }
}
