package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2017/3/10.
 */

public class SearchExitAnim extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target,"alpha",0, 1),
                ObjectAnimator.ofFloat(target, "scaleX", 1.1f, 1.0f),
                ObjectAnimator.ofFloat(target, "scaleY", 1.1f, 1.0f)
        );
    }
}
