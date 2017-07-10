package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2016/11/17.
 */
public class YearExitAnim extends BaseViewAnimator {
    float pivotX, pivotY;
    float translateX ,translateY ;
    float scaleVio;
    public YearExitAnim(float pivotX, float pivotY, float translateX, float translateY, float scaleVio) {
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.translateX = translateX;
        this.translateY = translateY;
        this.scaleVio = scaleVio;
    }

    @Override
    protected void prepare(View target) {
        ViewGroup parent = (ViewGroup)target.getParent();
        float scaleVio = target.getWidth() / (parent.getWidth() / 3);
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "pivotX", pivotX, pivotX),
                ObjectAnimator.ofFloat(target,"pivotY", pivotY, pivotY),
                ObjectAnimator.ofFloat(target,"translateX", 0, translateX),
                ObjectAnimator.ofFloat(target,"translateY", 0, translateY),
                ObjectAnimator.ofFloat(target, "alpha", 0.4f, 0),
                ObjectAnimator.ofFloat(target, "scaleX", 1, scaleVio),
                ObjectAnimator.ofFloat(target, "scaleY", 1, scaleVio)
        );
    }
}
