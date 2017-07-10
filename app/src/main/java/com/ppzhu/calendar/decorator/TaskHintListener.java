package com.ppzhu.calendar.decorator;

/**
 * @author zzl
 * 日程圆点回调
 * Created by on 2017/2/9.
 */
public interface TaskHintListener {
    void onGetTaskHintCircle(boolean[] taskHintCircle, int taskHintType);
}
