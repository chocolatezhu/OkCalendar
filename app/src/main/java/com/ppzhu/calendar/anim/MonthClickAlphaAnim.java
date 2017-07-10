package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2016/11/25.
 */
public class MonthClickAlphaAnim extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 0.8f, 1.0f)
        );
    }
}
