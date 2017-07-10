package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * @author zzl
 * Created on 2016/11/17.
 */
public class YearEnterAnim extends BaseViewAnimator {
    float startPivotX, startPivotY;
    float stopPivotX, stopPivotY;

    public YearEnterAnim(float startPivotX, float startPivotY, float stopPivotX, float stopPivotY) {
        this.startPivotX = startPivotX;
        this.startPivotY = startPivotY;
        this.stopPivotX = stopPivotX;
        this.stopPivotY = stopPivotY;
    }

    @Override
    protected void prepare(View target) {
        ViewGroup parent = (ViewGroup)target.getParent();
        float scaleVio = (float)parent.getHeight() / (parent.getHeight() / 4);
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "pivotX", stopPivotX, stopPivotX),
                ObjectAnimator.ofFloat(target, "pivotY", stopPivotY, stopPivotY),
                ObjectAnimator.ofFloat(target, "alpha", 0.0f, 1),
                ObjectAnimator.ofFloat(target, "scaleX", scaleVio, 1),
                ObjectAnimator.ofFloat(target, "scaleY", scaleVio, 1)
        );
    }
}
