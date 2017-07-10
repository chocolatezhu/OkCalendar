package com.ppzhu.calendar.database;


import com.ppzhu.calendar.bean.Festival;

import java.util.List;

/**
 * 日历数据库读取接口
 *
 * @author 甘巧
 * @date: 2015-12-17
 */
public interface IDatabaseCalendarReader {
    byte[] writeLock = new byte[011];

    // 通过年份获取节假日信息
    List<Festival> getFestivalObjListByYear(String year);

    // 通过月份获取节假日信息
    List<Festival> getFestivalObjListByMonth(String month);

    // 通过日期获取节假日信息
    int getFestivalObjByThemeId(String time);

    // 保存节假日信息
    boolean saveFestivalObjList(List<Festival> list);

//	// 删除所有数据库信息节假日信息
//	 boolean deleteAllFestivalObjList(Long AddTime);


}