package com.ppzhu.calendar.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.text.TextUtils;

import com.ppzhu.calendar.bean.ContactInfoPojo;
import com.ppzhu.calendar.bean.Schedule;
import com.ppzhu.calendar.constants.ConstData;
import com.ppzhu.calendar.constants.ScheduleConst;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 日程数据操作工具类
 * @author zzl
 * Created on 2016/11/9.
 */
public class ScheduleUtil {
    private final static String TAG = "ScheduleUtil";

    /**
     * 去掉id为空的数据
     * 对日程数据按照时间排序
     * @param list 需要排序的日程数据
     */
    public static List<Schedule> sortDailySchedule(List<Schedule> list) {
        Iterator<Schedule> iterator = list.iterator();
        while (iterator.hasNext()) {
            Schedule schedule = iterator.next();
            if (schedule.getId() == null) {
                iterator.remove();
            }
        }
        Collections.sort(list);
        String preDate = "";
        for (Schedule schedule: list) {
            if (schedule.getDate().equals(preDate)) {
                schedule.setBehind(true);
            }
            preDate = schedule.getDate();
        }
        return list;
    }

    /**
     * 去掉id为空的数据
     * @param list 需要过滤的日程数据
     */
    public static List<Schedule> filterNullSchedule(List<Schedule> list) {
        Iterator<Schedule> iterator = list.iterator();
        while (iterator.hasNext()) {
            Schedule schedule = iterator.next();
            if (schedule.getId() == null) {
                iterator.remove();
            }
        }
        return list;
    }

    /**
     * 判断两个日期是否相等，只精确到分
     * @param lhs
     * @param rhs
     * */
    public static boolean isSameCalendar(long lhs, long rhs) {
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(lhs);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(rhs);

        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
        int hour1 = calendar1.get(Calendar.HOUR);
        int minute1 = calendar1.get(Calendar.MINUTE);

        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
        int hour2 = calendar2.get(Calendar.HOUR);
        int minute2 = calendar2.get(Calendar.MINUTE);

        return (year1 == year2) && (month1 == month2) && (day1 == day2) && (hour1 == hour2) && (minute1 == minute2);
    }

    /**
     * 判断两个日期是否同一天
     * @param calendar1
     * @param calendar2
     * */
    public static boolean isSameDayCalendar(Calendar calendar1, Calendar calendar2 ) {
        int year1 = calendar1.get(Calendar.YEAR);
        int month1 = calendar1.get(Calendar.MONTH);
        int day1 = calendar1.get(Calendar.DAY_OF_MONTH);

        int year2 = calendar2.get(Calendar.YEAR);
        int month2 = calendar2.get(Calendar.MONTH);
        int day2 = calendar2.get(Calendar.DAY_OF_MONTH);

        return (year1 == year2) && (month1 == month2) && (day1 == day2);
    }

    public static Calendar getCalendar(long timeMill) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeMill);
        return calendar;
    }

    /**
     * 获取全天状态时的结束日期
     * <p> 时分秒设置为0，这里只需要整天时间，比如结束日期是：2016年10月3日 23:59 </p>
     * */
    public static Calendar getAllDayEndTime(long mEndTimeMill) {
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(mEndTimeMill);
        endCalendar.set(
                endCalendar.get(Calendar.YEAR),
                endCalendar.get(Calendar.MONTH),
                endCalendar.get(Calendar.DAY_OF_MONTH),
                ScheduleConst.MAX_HOUR,
                ScheduleConst.MAX_MINUTE,
                ScheduleConst.MAX_SECOND);
        return endCalendar;
    }

    /**
     * 获取全天状态时的开始日期
     * <p> 这里整天时间，提醒时间设置为上午9点，比如开始日期是：2016年10月5日 9:00 </p>
     * */
    public static Calendar getAllDayStartTime(long mAlertTime) {
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(mAlertTime);
        startCalendar.set(Calendar.HOUR_OF_DAY, 0);
        startCalendar.set(Calendar.MINUTE, 0);
        return startCalendar;
    }

    /**
     * 根据id获取联系人信息
     * @param context
     * @param contactId 该联系人id
     * @param contactInfoPojo 保存联系人信息实体类
     * */
    private static ContactInfoPojo getContactInfoById(Context context, String contactId, ContactInfoPojo contactInfoPojo) {
        Cursor cursor = null;
        String[] projection = {ContactsContract.Data.DISPLAY_NAME, ContactsContract.Contacts.LOOKUP_KEY};

        try {
            long start1 = System.currentTimeMillis();
            cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                    projection, ContactsContract.Contacts._ID + "=?",
                    new String[]{contactId}, null);
            while (null != cursor && cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
//                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
                String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                //获取该联系人uri，用于跳转到联系人详情界面
                Uri lookupUri =  ContactsContract.Contacts.getLookupUri(Long.parseLong(contactId), lookupKey);
                contactInfoPojo.setName(name);
                contactInfoPojo.setLookupUri(lookupUri);
                contactInfoPojo.setContactId(Integer.parseInt(contactId));
            }
        } catch (Exception e) {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        return contactInfoPojo;
    }


    /**
     * 查询联系人信息
     */
    private static List<ContactInfoPojo> getContactInfo(Context context) {
        long start = System.currentTimeMillis();
        String contactId = "";
        String name = "";
        String lookupKey = "";
        Uri lookupUri = null;

        List<ContactInfoPojo> list = new ArrayList<>();
        Cursor cursor = null;
        ContactInfoPojo contactInfoPojo;
        String[] projection = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Data.DATA1, Event.TYPE,
                ContactsContract.Data.DISPLAY_NAME, ContactsContract.Contacts.LOOKUP_KEY};

        String selection = Event.MIMETYPE + "='"
                + Event.CONTENT_ITEM_TYPE + "' AND ("
                + Event.TYPE + "='" + Event.TYPE_BIRTHDAY + "')";
        try {
            long start1 = System.currentTimeMillis();
            cursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
            while (null != cursor && cursor.moveToNext()) {
                String birthday = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DATA1));
                if (!TextUtils.isEmpty(birthday)) {
                    Calendar birthdayCalendar = DateFormatter.formatToCalendar(birthday, "yyyy-MM-dd");
                    int birthdayYear = birthdayCalendar.get(Calendar.YEAR);
                    if (ConstData.MIN_YEAR > birthdayYear || ConstData.MAX_YEAR < birthdayYear ) {
                        //忽略超出最小或最大日期的生日
                        continue;
                    }
                    contactInfoPojo = new ContactInfoPojo();

                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
//                    String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY));
                    lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    //获取该联系人uri，用于跳转到联系人详情界面
                    lookupUri = ContactsContract.Contacts.getLookupUri(Long.parseLong(contactId), lookupKey);

                    contactInfoPojo.setName(name);
                    contactInfoPojo.setLookupUri(lookupUri);
                    contactInfoPojo.setContactId(Integer.parseInt(contactId));
                    contactInfoPojo.setBirthday(birthday);
                    list.add(contactInfoPojo);
                }
            }
        } catch (Exception e) {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        } finally {
            if (null != cursor) {
                cursor.close();
                cursor = null;
            }
        }
        return list;
    }

    /**
     * 查询该联系人的生日
     *
     * @param context
     * @param contactId
     * @return birthday yyyy-mm-dd
     */
    private static String getBirthday(Context context, int contactId) {
        //获取联系人生日
        String birthday = "";
        String[] projection = new String[]{Event.DATA1};
        String selection = ContactsContract.Data.MIMETYPE + "='" + Event.CONTENT_ITEM_TYPE + "'"
                + " and " + Event.TYPE + "='" + Event.TYPE_BIRTHDAY + "'"
                + " and " + Event.CONTACT_ID
                + " = " + contactId;
        long start = System.currentTimeMillis();
        //这里耗时
        Cursor birthdayCursor = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI, projection, selection, null, null);
        while (null != birthdayCursor && birthdayCursor.moveToNext()) {
            birthday = birthdayCursor.getString(birthdayCursor.getColumnIndex(Event.DATA));
        }
        if (null != birthdayCursor) {
            birthdayCursor.close();
        }
        return birthday;
    }

    public static List<Schedule> getSelectBirthDayScheduleList(Context context, LocalDate selectDate) {
        List<ContactInfoPojo> list = getContactInfo(context);
        if (null == list || list.isEmpty()) {
            return null;
        }
        Date dateTime = selectDate.toDate();
        Calendar selectCalendar = Calendar.getInstance();
        selectCalendar.setTime(dateTime);
        List<Schedule> scheduleList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            String birthday = list.get(i).getBirthday();
            String name = list.get(i).getName();
            Uri lookUpUri = list.get(i).getLookupUri();
            Calendar birthdayCalendar = DateFormatter.formatToCalendar(birthday, "yyyy-MM-dd");
            //同一天
            if (selectCalendar.get(Calendar.YEAR) == birthdayCalendar.get(Calendar.YEAR) &&
                    selectCalendar.get(Calendar.MONTH) == birthdayCalendar.get(Calendar.MONTH) &&
                    selectCalendar.get(Calendar.DAY_OF_MONTH) == birthdayCalendar.get(Calendar.DAY_OF_MONTH)) {
                Schedule schedule = new Schedule();
                schedule.setId(System.currentTimeMillis() + "");
                schedule.setTitle(name + "生日");
                schedule.setLocation("");
                schedule.setAllDay(ScheduleConst.SCHEDULE_IS_ALL_DAY);
                long time = DateFormatter.formatToCalendar(birthday, "yyyy-MM-dd").getTimeInMillis();
                long alertTimeMill = ScheduleUtil.getAllDayStartTime(time).getTimeInMillis();
                long endTimeMill = ScheduleUtil.getAllDayEndTime(time).getTimeInMillis();
                schedule.setAlertTime(alertTimeMill);
                schedule.setEndTimeMill(endTimeMill);
                schedule.setStartTime(DateFormatter.timeToFormat(alertTimeMill, "yyyy年MM月dd日"));
                schedule.setEndTime(DateFormatter.timeToFormat(endTimeMill, "yyyy年MM月dd日"));
                schedule.setDate(DateFormatter.timeToFormat(alertTimeMill, "yyyy年MM月dd日"));
                schedule.setRepeatMode(0);
                schedule.setRepeatId(ScheduleConst.SCHEDULE_REPEAT_EVERY_YEAR);
                schedule.setRemindId(ScheduleConst.SCHEDULE_ALERT_HAPPEN);
                schedule.setRemark("");
                if (null != lookUpUri) {
                    schedule.setLookUpUri(lookUpUri.toString());
                }
                scheduleList.add(schedule);
            }
        }

        return scheduleList;
    }

    /**
     * 获取所有联系人的生日
     * */
    public static List<Schedule> getAllBirthDayScheduleList(Context context) {
        long start = System.currentTimeMillis();
        List<ContactInfoPojo> list = getContactInfo(context);
        if (null == list || list.isEmpty()) {
            return null;
        }
        List<Schedule> scheduleList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int contactId = list.get(i).getContactId();
            String birthday = list.get(i).getBirthday();
            String name = list.get(i).getName();
            Uri lookUpUri = list.get(i).getLookupUri();
            Schedule schedule = new Schedule();
            schedule.setTitle(name + "生日");
            schedule.setLocation("");
            schedule.setAllDay(ScheduleConst.SCHEDULE_IS_ALL_DAY);
            long time = DateFormatter.formatToCalendar(birthday, "yyyy-MM-dd").getTimeInMillis();
            long alertTimeMill = ScheduleUtil.getAllDayStartTime(time).getTimeInMillis();
            long endTimeMill = ScheduleUtil.getAllDayEndTime(time).getTimeInMillis();
            schedule.setAlertTime(alertTimeMill);
            schedule.setEndTimeMill(endTimeMill);
            schedule.setStartTime(DateFormatter.timeToFormat(alertTimeMill, "yyyy年MM月dd日"));
            schedule.setEndTime(DateFormatter.timeToFormat(endTimeMill, "yyyy年MM月dd日"));
            schedule.setDate(DateFormatter.timeToFormat(alertTimeMill, "yyyy年MM月dd日"));
            schedule.setRepeatMode(0);
            schedule.setRepeatId(ScheduleConst.SCHEDULE_REPEAT_EVERY_YEAR);
            schedule.setRemindId(ScheduleConst.SCHEDULE_ALERT_HAPPEN);
            schedule.setRemark("");
            if (null != lookUpUri) {
                schedule.setLookUpUri(lookUpUri.toString());
            }
            schedule.setId(DateFormatter.timeToFormat(alertTimeMill, "yyyyMMdd") + contactId);
            scheduleList.add(schedule);
        }

        return scheduleList;
    }

}
