package com.ppzhu.calendar.bean;

import java.util.List;

/**
 * Created by Administrator on 2017/1/20.
 */
public class ScheduleListBean {
    private List<Schedule> scheduleList;
    private AlmanacPojo almanacPojo;

    public List<Schedule> getScheduleList() {
        return scheduleList;
    }

    public void setScheduleList(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    public AlmanacPojo getAlmanacPojo() {
        return almanacPojo;
    }

    public void setAlmanacPojo(AlmanacPojo almanacPojo) {
        this.almanacPojo = almanacPojo;
    }
}
