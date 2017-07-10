package com.ppzhu.calendar.anim;

import android.animation.ObjectAnimator;
import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;

/**
 * Created by Administrator on 2017/3/13.
 */

public class ScheduleListSwitchAnim extends BaseViewAnimator {
    private float fromY;
    private float toY;

    public ScheduleListSwitchAnim(float fromY, float toY) {
        this.fromY = fromY;
        this.toY = toY;
    }

    @Override
    protected void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target,"y", fromY, toY)
        );
    }
}
