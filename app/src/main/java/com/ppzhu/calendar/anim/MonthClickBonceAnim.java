package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2016/11/25.
 */
public class MonthClickBonceAnim  extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 1.1f, 0.95f, 1.02f, 0.98f, 1.0f),
                ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 1.1f, 0.95f, 1.02f, 0.98f, 1.0f)
        );
    }
}
