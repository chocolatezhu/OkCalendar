package com.ppzhu.calendar.view.slidelayout;

/**
 * Created by Administrator on 2017/1/10.
 */
public class CalendarViewManager {
    public ScheduleState mState;

    public ScheduleState getState() {
        return mState;
    }

    public void setState(ScheduleState mState) {
        this.mState = mState;
    }

    public enum ScheduleState {
        Month,
        Week,
        Year
    }
}
