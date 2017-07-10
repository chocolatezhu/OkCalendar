package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2017/2/17.
 */
public class SearchEnterAnim extends BaseViewAnimator {
    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "alpha", 1.0f, 0.2f),
                ObjectAnimator.ofFloat(target, "scaleX", 1.0f, 1.3f),
                ObjectAnimator.ofFloat(target, "scaleY", 1.0f, 1.3f)
        );
    }
}
