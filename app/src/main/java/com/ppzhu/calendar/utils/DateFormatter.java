package com.ppzhu.calendar.utils;

import android.content.res.Resources;
import android.text.format.DateFormat;


import com.ppzhu.calendar.R;
import com.ppzhu.calendar.constants.ConstData;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormatter {

	private Resources resources;

	public DateFormatter(Resources resources) {
		this.resources = resources;
	}

	private String getArrayString(int resid, int index) {
		return resources.getStringArray(resid)[index];
	}

	private static String getArrayString(Resources resources, int resid, int index) {
		return resources.getStringArray(resid)[index];
	}

	public CharSequence getDayName(LunarCalendar lc) {
		StringBuilder result = new StringBuilder();
		int day = lc.getLunar(LunarCalendar.LUNAR_DAY);
		if (day < 11) {
			result.append(getArrayString(R.array.chinesePrefix, 0));
			result.append(getArrayString(R.array.chineseDigital, day));
		} else if (day < 20) {
			result.append(getArrayString(R.array.chinesePrefix, 1));
			result.append(getArrayString(R.array.chineseDigital, day - 10));
		} else if (day == 20) {
			result.append("二十");
		} else if (day < 30) {
			result.append(getArrayString(R.array.chinesePrefix, 2));
			result.append(getArrayString(R.array.chineseDigital, day - 20));
		} else {
			result.append("三十");
		}

		return result;
	}

	/**
	 * 获取黄历日期
	 * @param resources 获取res资源用
	 * @param timeMill long类型时间
	 * */
	public static String getAlmanacDate(Resources resources, long timeMill) {
		LunarCalendar lunarCalendar = new LunarCalendar(timeMill);
		int lYear = -1;
		int lMonth = -1;
		int lDay = -1;

		int year = lunarCalendar.getGregorianDate(Calendar.YEAR);
		int month = lunarCalendar.getGregorianDate(Calendar.MONTH);
		int day = lunarCalendar.getGregorianDate(Calendar.DAY_OF_MONTH);

		// 取年柱,以春分为分界点
		int st_spring = LunarCalendar.getSolarTerm(year, 3); // 立春
		if ((month == 1 && st_spring > day) || month < 1) {
			lYear = -1;
		} else {
			lYear = 0;
		}
		lYear = year - 1900 + lYear + 36;

		// 月柱,月柱以节令为界
		int st_monthFirst = (month == 1 ? st_spring : LunarCalendar
				.getSolarTerm(year, month * 2 + 1));
		lMonth = (st_monthFirst > day ? -1 : 0);
		lMonth = (year - 1900) * 12 + +month + lMonth + 13;

		// 日柱,单纯的日循环
		lDay = (int) ((lunarCalendar.getTimeInMillis() - LunarCalendar.LUNAR_BASE_MILLIS) / LunarCalendar.DAY_MILLIS) + 40;

		StringBuilder cs = new StringBuilder();
		cs.append(getArrayString(resources, R.array.chinese_gan, lYear % 10));
		cs.append(getArrayString(resources, R.array.chinese_zhi, lYear % 12));
		cs.append(getArrayString(resources, R.array.chineseTime, 0)); // 年
		cs.append("【");
		cs.append(getArrayString(resources, R.array.chinese_animal, lYear % 12));
		cs.append(getArrayString(resources, R.array.chineseTime, 0)); // 年
		cs.append("】");
		cs.append(' ');
		cs.append(getArrayString(resources, R.array.chinese_gan, lMonth % 10));
		cs.append(getArrayString(resources, R.array.chinese_zhi, lMonth % 12));
		cs.append(getArrayString(resources, R.array.chineseTime, 1)); // 月
		cs.append(' ');
		cs.append(getArrayString(resources, R.array.chinese_gan, lDay % 10));
		cs.append(getArrayString(resources, R.array.chinese_zhi, lDay % 12));
		cs.append(getArrayString(resources, R.array.chineseTime, 2)); // 日

		return cs.toString();
	}

	public CharSequence getGregorianFestivalName(int index) {
		return getArrayString(R.array.gregorianFestivals, index);
	}

	public CharSequence getLunarFestivalName(int index) {
		return getArrayString(R.array.lunarFestivals, index);
	}

	public CharSequence getMonthName(LunarCalendar lc) {
		StringBuilder result = new StringBuilder();
		if (lc.getLunar(LunarCalendar.LUNAR_IS_LEAP) == 1) {
			result.append(getArrayString(R.array.chinesePrefix, 6));
		}
		int month = lc.getLunar(LunarCalendar.LUNAR_MONTH);
		switch (month) {
		case 1:
			result.append(getArrayString(R.array.chinesePrefix, 3));
			break;
		case 11:
			result.append(getArrayString(R.array.chinesePrefix, 4));
			break;
		case 12:
			result.append(getArrayString(R.array.chinesePrefix, 5));
			break;
		default:
			result.append(getArrayString(R.array.chineseDigital, month));
			break;
		}
		result.append(getArrayString(R.array.chineseTime, 1));
		return result;
	}

	public CharSequence getSolarTermName(int index) {
		return getArrayString(R.array.solarTerm, index);
	}

	public CharSequence getYearName(LunarCalendar lc) {
		StringBuilder result = new StringBuilder();
		int year = lc.getLunar(LunarCalendar.LUNAR_YEAR);
		int resid = R.array.chineseDigital;
		result.append(getArrayString(resid, (year / 1000) % 10));
		result.append(getArrayString(resid, (year / 100) % 10));
		result.append(getArrayString(resid, (year / 10) % 10));
		result.append(getArrayString(resid, year % 10));
		result.append(getArrayString(R.array.chineseTime, 0));
		return result;
	}


	/**
	 * 获取星期几
	 * @param date 当前日期
	 * */
	public static String getWeekDay(Calendar date){
		if (null == date){
			return "";
		}
		int weekDay = date.get(Calendar.DAY_OF_WEEK);
		switch (weekDay){
			case 1:
				return "周日";
			case 2:
				return "周一";
			case 3:
				return "周二";
			case 4:
				return "周三";
			case 5:
				return "周四";
			case 6:
				return "周五";
			case 7:
				return "周六";
			default:
				return "周日";
		}
	}

	/**
	 * 获取星期几
	 * @param timeMill 当前long类型时间
	 * */
	public static String getWeekDay(long timeMill){
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(timeMill);

		if (null == date){
			return "";
		}
		int weekDay = date.get(Calendar.DAY_OF_WEEK);
		switch (weekDay){
			case 1:
				return "周日";
			case 2:
				return "周一";
			case 3:
				return "周二";
			case 4:
				return "周三";
			case 5:
				return "周四";
			case 6:
				return "周五";
			case 7:
				return "周六";
			default:
				return "周日";
		}
	}

	/**
	 * 获取农历日期
	 * @param timeMill long类型时间
	 * */
	public static String getLunarDay(long timeMill) {
		LunarCalendar lu = new LunarCalendar(timeMill);
		if (lu.getLunarDay() > 10) {
			return ConstData.LUNAR_MONTHS[lu.getLunarMonth() - 1] + ConstData.LUNAR_DAY[lu.getLunarDay() - 1];
		} else {
			return ConstData.LUNAR_MONTH[lu.getLunarMonth() - 1] + ConstData.LUNAR_DAY[lu.getLunarDay() - 1];
		}
	}

	public static String getDialogScheduleAlertTime(long timeMill) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeMill);

		StringBuilder builder = new StringBuilder();
		Calendar todayCalendar = Calendar.getInstance();
		int nowYear = todayCalendar.get(Calendar.YEAR);
		int nowMonth = todayCalendar.get(Calendar.MONTH);
		int nowDay = todayCalendar.get(Calendar.DAY_OF_MONTH);

		int scheduleYear = calendar.get(Calendar.YEAR);
		int scheduleMonth = calendar.get(Calendar.MONTH);
		int scheduleDay = calendar.get(Calendar.DAY_OF_MONTH);

		if (nowYear == scheduleYear ) {
			if (nowMonth == scheduleMonth && nowDay == scheduleDay) {
				builder.append("今天 ");
			} else {
				builder.append(DateFormat.format("M月d日", calendar));
			}
		} else {
			builder.append(DateFormat.format("yyyy年M月d日", calendar));
		}
		builder.append(DateFormat.format("HH:mm", calendar));

		return builder.toString();
	}

	public static Calendar formatToCalendar(String time ,String format){
		Date date = null;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			date = sdf.parse(time);
			cal.setTimeInMillis(date.getTime());
		}catch(Exception e){
			e.printStackTrace();
		}
		return cal;
	}

	/**
	 * 转为指定格式时间
	 * */
	public static String timeToFormat(long time, String format) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(time);
		return  (String) DateFormat.format(format, calendar);
	}

	/**
	 * 年月日转为long类型时间
	 * @param year
	 * @param month
	 * @param day
	 * */
	public static long getTimeMill(int year, int month, int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		return calendar.getTimeInMillis();
	}

	/**
	 * 获取两个时间相差的天数
	 * @param startTimeMill
	 * @param endTimeMill
	 * */
	public static int getGapDay(long startTimeMill, long endTimeMill) {
		Date date_start=new Date(startTimeMill);
		Date date_end=new Date(endTimeMill);

		//计算日期从开始时间于结束时间的0时计算
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(date_start);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(date_end);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return  (int) ((toCalendar.getTimeInMillis() - fromCalendar.getTimeInMillis())/ (1000 * 60 * 60 * 24));
	}

}
