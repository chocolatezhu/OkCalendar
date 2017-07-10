package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2017/3/15.
 */

public class SlideInUpAnim extends BaseViewAnimator {
    float fromY;
    float toY;

    public SlideInUpAnim(float fromY, float toY) {
        this.fromY = fromY;
        this.toY = toY;
    }

    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0, 1),
                ObjectAnimator.ofFloat(target,"y", fromY, toY)
        );
    }
}
