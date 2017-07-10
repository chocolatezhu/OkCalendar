package com.ppzhu.calendar.schedule;

import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.bean.ScheduleListBean;

import java.util.List;

/**
 * Created by Administrator on 2016/10/12.
 */
public interface ScheduleListener {
    void OnGetSelectScheduleList(ScheduleListBean scheduleListBean);
    void OnGetAllScheduleList(List<Schedule> allScheduleList);
}
