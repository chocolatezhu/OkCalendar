package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2016/11/17.
 */
public class MonEnterAnim extends BaseViewAnimator {
    float pivotX, pivotY;
    float scale;
    public MonEnterAnim(float pivotX, float pivotY, float scale) {
        this.pivotX = pivotX;
        this.pivotY = pivotY;
        this.scale = scale;
    }

    @Override
    protected void prepare(View target) {
//        ViewGroup parent = (ViewGroup)target.getParent();
//        float scaleVio = (parent.getHeight() / 4) / target.getHeight();
////        target.setPivotX(pivotX);
//        target.setPivotY(pivotY);

        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target,"pivotX", pivotX, pivotX),
                ObjectAnimator.ofFloat(target,"pivotY", pivotY, pivotY),
                ObjectAnimator.ofFloat(target, "alpha", 0, 1),
                ObjectAnimator.ofFloat(target, "scaleX", scale, 1),
                ObjectAnimator.ofFloat(target, "scaleY", scale, 1)
        );
    }
}
