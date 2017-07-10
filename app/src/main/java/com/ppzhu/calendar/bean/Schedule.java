package com.ppzhu.calendar.bean;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

/**
 * Created by Administrator on 2016/8/11.
 */
public class Schedule implements Parcelable, Comparable<Schedule> {
    //日程id，必须要有，主键
    private String id;
    //标题
    private String title;
    //地点
    private String location;
    //备注
    private String remark;
    //日程开始时间，已经格式化了
    private String startTime;
    //日程结束时间，已经格式化了
    private String endTime;
    //日程重复模式，0是单个重复（永不、每天、每周...），1是自定义重复（周日、周一...）
    private int repeatMode;
    //日程重复id，repeatMode=0时：0永不，1每天，2每周，3每月，4每年；repeatMode=1时：0周日，1周一，2周二，3周三，4周四，5周五，6周六
    private String repeatId;
    //日程提醒id 0:无，1:日程发生时，2:5分钟前，3:15分钟前，4:30分钟前，5:1小时前，6：2小时前，7:1天前，8:2天前
    private int remindId;
    //是否全天模式 0:false;1:true
    private int allDay;
    //开始的日期，年月日，已经格式化了
    private String date;
    //日程提醒时间，long类型
    private long alertTime;
    //日程结束时间，long类型
    private long endTimeMill;
    //用于日程列表排序，但默认用alertTime排序，该字段暂不存入数据库
    private long sortTimeMill;
    //跳转联系人详情页uri，该字段暂不存入数据库
    private String lookUpUri;
    /**
     * 是否是同一日期下的后续日程
     */
    private boolean isBehind;

    protected Schedule(Parcel in) {
        id = in.readString();
        title = in.readString();
        location = in.readString();
        remark = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        repeatMode = in.readInt();
        repeatId = in.readString();
        remindId = in.readInt();
        allDay = in.readInt();
        date = in.readString();
        alertTime = in.readLong();
        endTimeMill = in.readLong();
        sortTimeMill = in.readLong();
        lookUpUri = in.readString();
        isBehind = in.readByte() != 0;
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Schedule(){

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public int getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(int repeatMode) {
        this.repeatMode = repeatMode;
    }

    public int getRemindId() {
        return remindId;
    }

    public void setRemindId(int remindId) {
        this.remindId = remindId;
    }

    public int getAllDay() {
        return allDay;
    }

    public void setAllDay(int allDay) {
        this.allDay = allDay;
    }

    public long getAlertTime() {
        return alertTime;
    }

    public void setAlertTime(long alertTime) {
        this.alertTime = alertTime;
    }

    public long getEndTimeMill() {
        return endTimeMill;
    }

    public void setEndTimeMill(long endTimeMill) {
        this.endTimeMill = endTimeMill;
    }

    public boolean isBehind() {
        return isBehind;
    }

    public void setBehind(boolean behind) {
        isBehind = behind;
    }

    public long getSortTimeMill() {
        return sortTimeMill;
    }

    public void setSortTimeMill(long sortTimeMill) {
        this.sortTimeMill = sortTimeMill;
    }

    public String getLookUpUri() {
        return lookUpUri;
    }

    public void setLookUpUri(String lookUpUri) {
        this.lookUpUri = lookUpUri;
    }

    @Override
    public int compareTo(@NonNull Schedule other) {
        if (alertTime < other.getAlertTime()) {
            return -1;
        } else {
            return 1;
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(location);
        dest.writeString(remark);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeInt(repeatMode);
        dest.writeString(repeatId);
        dest.writeInt(remindId);
        dest.writeInt(allDay);
        dest.writeString(date);
        dest.writeLong(alertTime);
        dest.writeLong(endTimeMill);
        dest.writeLong(sortTimeMill);
        dest.writeString(lookUpUri);
        dest.writeByte((byte) (isBehind ? 1 : 0));
    }

    public boolean valueEqual(Schedule schedule) {
        if (null == schedule) {
            return true;
        }
        return title.equals(schedule.getTitle()) &&
                location.equals(schedule.getLocation()) &&
                allDay == schedule.getAllDay() &&
                startTime.equals(schedule.getStartTime()) &&
                endTime.equals(schedule.getEndTime()) &&
                repeatMode == schedule.getRepeatMode() &&
                repeatId.equals(schedule.getRepeatId()) &&
                remindId == schedule.getRemindId() &&
                remark.equals(schedule.getRemark());
    }

}
