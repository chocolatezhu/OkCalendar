package com.ppzhu.calendar.database;


import com.ppzhu.calendar.bean.Schedule;

import java.util.List;

/**
 * Created by Administrator on 2016/8/12.
 */
public interface ScheduleReader {

    //获取所有日程活动
    List<Schedule> getAllSchedule();

    //根据日程id获取日程活动
    Schedule getSchedule(String id);

    //根据日期获取当天所有日程
    List<Schedule> getDaySchedule(String date);

    //更新某天日程
    int updateSchedule(Schedule schedule);

   //根据id删除某个日程
    int deleteSchedule(String id);
    //记录某个日程
    Long saveSchedule(Schedule schedule);
    //模糊查询
    List<Schedule> getSearchSchedule(String title);
}
