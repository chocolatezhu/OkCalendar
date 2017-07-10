package com.ppzhu.calendar.utils;


import com.ppzhu.calendar.constants.ConstData;

import java.util.Calendar;
import java.util.Date;

/**
 * 农历日历</p> 算法及基础数据来自：http://sean.o4u.com/ap/calendar/ <br/>
 * 2012-12-31
 * 
 * @author mostone&#64;hotmail.com
 * 
 */
public class LunarCalendar {

	/** 一天的毫秒数 */
	public static final long DAY_MILLIS = 24 * 60 * 60 * 1000;
	/** 农历1900-1-1 的公历毫秒数(与公历1970-1-1的偏移值) */
	public static final long LUNAR_BASE_MILLIS = -2206425952000L;

	/** 农历年字段 */
	public static final int LUNAR_YEAR = 0;
	/** 农历月字段 */
	public static final int LUNAR_MONTH = 1;
	/** 农历日字段 */
	public static final int LUNAR_DAY = 2;
	/** 农历是否为闰月字段 */
	public static final int LUNAR_IS_LEAP = 3;

	//年月周视图使用，周视图需要多显示下一年的日期，所以加一
	private static final int YEAR_MAX_2037 = ConstData.MAX_YEAR + 1;
	private static final int YEAR_MAX = 2100;
	private static final int YEAR_MIN = 1900;

	private Calendar gregorianDate;
	private int[] lunarFields;

	// 公历节日
	private static final int[][] GREGORIAN_FESTIVALS = { { 1, 1, 0 }, // 元旦
			{ 1, 1, 0 }, // 元旦
			{ 2, 14, 1 }, // 情人节
			{ 3, 8, 2 }, // 妇女节
			{ 3, 12, 3 }, // 植树节
			{ 4, 1, 4 }, // 愚人节
			{ 4, 22, 5 }, // 地球日
			{ 5, 1, 6 }, // 劳动节
			{ 5, 4, 7 }, // 青年节
			{ 6, 1, 8 }, // 儿童节
			{ 6, 5, 9 }, // 环境日
			{ 7, 1, 10 }, // 建党节
			{ 8, 1, 11 }, // 建军节
			{ 9, 10, 12 }, // 教师节
			{ 10, 1, 13}, // 国庆节
			{ 10, 31, 14 }, // 万圣夜
			{ 11, 1, 15 }, // 万圣节
			{ 12, 24, 16 }, // 平安夜
			{ 12, 25, 17 }, // 圣诞节
	};



	// 农历节日
	private static final int[][] LUNAR_FESTIVALS = {
			{ 1, 1, 0 }, //春节
			{ 1, 15, 1 }, // 元宵节
			{ 5, 5, 2 }, // 端午节
			{ 7, 7, 3}, // 七夕
			{ 8, 15, 4 }, // 中秋节
			{ 9, 9, 5 }, // 重阳节
			{ 12, 8, 6 }, // 腊八节
			{ 12, 23, 7 }, // 小年
			{ 12, 24, 8 }, // 南方小年
			{ 12, 0, 9} // 除夕
	};

	// 农历信息字段总数
	private static final int LUNAR_FIELD_COUNT = 4;

	// 1900-2100 农历日期信息
	private static final long[] LUNAR_INFO = { 0x4bd8L, 0x4ae0L, 0xa570L, 0x54d5L, 0xd260L, 0xd950L, 0x5554L, 0x56afL, 0x9ad0L, 0x55d2L, 0x4ae0L,
			0xa5b6L, 0xa4d0L, 0xd250L, 0xd295L, 0xb54fL, 0xd6a0L, 0xada2L, 0x95b0L, 0x4977L, 0x497fL, 0xa4b0L, 0xb4b5L, 0x6a50L, 0x6d40L, 0xab54L,
			0x2b6fL, 0x9570L, 0x52f2L, 0x4970L, 0x6566L, 0xd4a0L, 0xea50L, 0x6a95L, 0x5adfL, 0x2b60L, 0x86e3L, 0x92efL, 0xc8d7L, 0xc95fL, 0xd4a0L,
			0xd8a6L, 0xb55fL, 0x56a0L, 0xa5b4L, 0x25dfL, 0x92d0L, 0xd2b2L, 0xa950L, 0xb557L, 0x6ca0L, 0xb550L, 0x5355L, 0x4dafL, 0xa5b0L, 0x4573L,
			0x52bfL, 0xa9a8L, 0xe950L, 0x6aa0L, 0xaea6L, 0xab50L, 0x4b60L, 0xaae4L, 0xa570L, 0x5260L, 0xf263L, 0xd950L, 0x5b57L, 0x56a0L, 0x96d0L,
			0x4dd5L, 0x4ad0L, 0xa4d0L, 0xd4d4L, 0xd250L, 0xd558L, 0xb540L, 0xb6a0L, 0x95a6L, 0x95bfL, 0x49b0L, 0xa974L, 0xa4b0L, 0xb27aL, 0x6a50L,
			0x6d40L, 0xaf46L, 0xab60L, 0x9570L, 0x4af5L, 0x4970L, 0x64b0L, 0x74a3L, 0xea50L, 0x6b58L, 0x5ac0L, 0xab60L, 0x96d5L, 0x92e0L, 0xc960L,
			0xd954L, 0xd4a0L, 0xda50L, 0x7552L, 0x56a0L, 0xabb7L, 0x25d0L, 0x92d0L, 0xcab5L, 0xa950L, 0xb4a0L, 0xbaa4L, 0xad50L, 0x55d9L, 0x4ba0L,
			0xa5b0L, 0x5176L, 0x52bfL, 0xa930L, 0x7954L, 0x6aa0L, 0xad50L, 0x5b52L, 0x4b60L, 0xa6e6L, 0xa4e0L, 0xd260L, 0xea65L, 0xd530L, 0x5aa0L,
			0x76a3L, 0x96d0L, 0x4afbL, 0x4ad0L, 0xa4d0L, 0xd0b6L, 0xd25fL, 0xd520L, 0xdd45L, 0xb5a0L, 0x56d0L, 0x55b2L, 0x49b0L, 0xa577L, 0xa4b0L,
			0xaa50L, 0xb255L, 0x6d2fL, 0xada0L, 0x4b63L, 0x937fL, 0x49f8L, 0x4970L, 0x64b0L, 0x68a6L, 0xea5fL, 0x6b20L, 0xa6c4L, 0xaaefL, 0x92e0L,
			0xd2e3L, 0xc960L, 0xd557L, 0xd4a0L, 0xda50L, 0x5d55L, 0x56a0L, 0xa6d0L, 0x55d4L, 0x52d0L, 0xa9b8L, 0xa950L, 0xb4a0L, 0xb6a6L, 0xad50L,
			0x55a0L, 0xaba4L, 0xa5b0L, 0x52b0L, 0xb273L, 0x6930L, 0x7337L, 0x6aa0L, 0xad50L, 0x4b55L, 0x4b6fL, 0xa570L, 0x54e4L, 0xd260L, 0xe968L,
			0xd520L, 0xdaa0L, 0x6aa6L, 0x56dfL, 0x4ae0L, 0xa9d4L, 0xa4d0L, 0xd150L, 0xf252L, 0xd520L };

	// 二十四节气在各月的基准日期
	private static final int[] SOLAR_TERM_BASE = { 4, 19, 3, 18, 4, 19, 4, 19, 4, 20, 4, 20, 6, 22, 6, 22, 6, 22, 7, 22, 6, 21, 6, 21 };

	// 1900-2100各年的二十四节气分布种类，对应上表中的序号
	private static final int[] SOLAR_TERM_INDEX = { 0, 1, 2, 3, 4, 1, 5, 3, 4, 1, 5, 3, 6, 7, 8, 9, 10, 11, 12, 9, 10, 13, 12, 14, 10, 13, 1, 14, 15,
			0, 1, 2, 16, 0, 1, 5, 16, 0, 1, 5, 16, 0, 1, 5, 17, 18, 7, 8, 19, 20, 21, 8, 19, 20, 13, 1, 22, 20, 0, 1, 23, 24, 0, 1, 23, 24, 0, 1, 25,
			24, 0, 1, 25, 26, 0, 27, 28, 29, 30, 11, 28, 29, 18, 21, 31, 32, 20, 33, 34, 35, 36, 0, 34, 37, 24, 0, 34, 38, 24, 0, 34, 39, 24, 0, 34,
			39, 29, 0, 40, 41, 29, 30, 42, 43, 29, 18, 44, 45, 32, 36, 46, 47, 35, 36, 48, 47, 39, 24, 48, 47, 39, 24, 48, 47, 39, 29, 48, 47, 39,
			29, 48, 49, 41, 29, 50, 51, 43, 29, 52, 53, 45, 35, 54, 53, 45, 55, 54, 56, 47, 55, 57, 56, 47, 39, 57, 56, 47, 39, 58, 56, 49, 39, 58,
			59, 49, 43, 58, 59, 60, 43, 58, 61, 62, 45, 63, 64, 53, 45, 65, 64, 56, 47, 65, 66, 56, 47, 67, 68, 56, 47, 39 };

	// 二十四节气以年为单位，在1900-2100年间，共有69种分布种类，共有 24* 69 项)
	private static final int[] SOLAR_TERM_OS = { 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2,
			2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 2,
			2, 3, 2, 3, 2, 2, 2, 3, 2, 3, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 1, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 2, 2, 2, 1, 1, 1, 2, 1, 1,
			1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			2, 2, 2, 2, 3, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 1, 2, 2, 1, 1,
			1, 2, 1, 1, 1, 2, 2, 1, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2,
			2, 2, 1, 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 3, 2, 2, 2, 2, 2, 3, 2, 2,
			2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2,
			1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2,
			1, 2, 1, 1, 2, 2, 1, 1, 1, 2, 2, 2, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 2, 1,
			1, 2, 1, 2, 1, 1, 2, 2, 1, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 1, 1, 1, 1, 1, 2,
			1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2,
			1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1,
			1, 1, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 1, 1,
			1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1,
			1, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 1, 2, 1, 1, 2, 2, 2, 1, 1, 2, 2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 2, 1, 1, 2, 2, 2, 1, 2, 2, 2, 2, 1,
			1, 2, 2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 2, 2, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 0, 1, 1, 1, 1, 1, 1,
			0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 1, 1,
			1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1,
			1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 2, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0,
			1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 2, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1,
			1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0, 2, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 0,
			0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1,
			1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0,
			0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0,
			0, 0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 1, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1,
			1, 1, 1, 1, 0, 1, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 0, 1,
			0, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0, 0, 1,
			0, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1, 0, 0,
			0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 1, 0, 1,
			0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 };

	public int getLunarYear(){
		return lunarFields[0];
	}
	public int getLunarMonth(){
		return lunarFields[1];
	}
	public int getLunarDay(){
		return lunarFields[2];
	}

	/**
	 * 支持范围最大年份
	 * 
	 * @return
	 */
	public static int getMaxYear() {
		return YEAR_MAX;
	}

	/**
	 * 支持范围最小年份
	 * 
	 * @return
	 */
	public static int getMinYear() {
		return YEAR_MIN + 1;
	}

	/**
	 * 取得二十四节气
	 * 
	 * @param year
	 *            公历年
	 * @param index
	 *            节气序号,从小寒开始的节气序号 [1-24]
	 * @return 公历当月日期
	 */
	public static int getSolarTerm(int year, int index) {
		return SOLAR_TERM_OS[SOLAR_TERM_INDEX[year - YEAR_MIN] * 24 + index - 1] + SOLAR_TERM_BASE[index - 1];
	}

	public LunarCalendar() {
		this(new Date());
	}

	public LunarCalendar(Calendar date) {
		this(date.getTimeInMillis());
	}

	public LunarCalendar(Date date) {
		this(date.getTime());
	}

	public LunarCalendar(long milliSeconds) {
		Calendar date = Calendar.getInstance();
		date.setTimeInMillis(milliSeconds);
		lunarFields = new int[LUNAR_FIELD_COUNT];
		gregorianDate = date;
		computeLunar();
	}

	/**
	 * 返回公历信息<br/>
	 * 参照:{@link Calendar#get(int)}
	 * 
	 * @param field
	 * @return
	 */
	public int getGregorianDate(int field) {
		return gregorianDate.get(field);
	}


	/**
	 * 获取农历信息
	 * 
	 * @param field
	 *            字段 id
	 * @return 字段值，闰月返回1
	 */
	public int getLunar(int field) {
		return lunarFields[field];
	}

	/**
	 * 返回当前的农历节日序号,从0开始,如果不是农历节日,返回-1
	 * 
	 * @return 农历节日序号 [-1, 0, ...]
	 */
	public int getLunarFestival() {
		for (int i = 0; i < LUNAR_FESTIVALS.length; i++) {
			// 当前月,并且不是闰月
			if (LUNAR_FESTIVALS[i][0] == lunarFields[LUNAR_MONTH] && lunarFields[LUNAR_IS_LEAP] == 0) {
				// 日期相同
				if (LUNAR_FESTIVALS[i][1] == lunarFields[LUNAR_DAY]) {
					return LUNAR_FESTIVALS[i][2];
				} else if (LUNAR_FESTIVALS[i][1] == 0
						&& lunarFields[LUNAR_DAY] == getDaysOfLunarMonth(lunarFields[LUNAR_YEAR], lunarFields[LUNAR_MONTH])) {
					// 月内最后一天(除夕)
					return LUNAR_FESTIVALS[i][2];
				}
			}
		}
		return -1;
	}

	/**
	 * @see Calendar#getTimeInMillis()
	 * @return
	 */
	public long getTimeInMillis() {
		return gregorianDate.getTimeInMillis();
	}

	/**
	 * 是否为今天
	 * 
	 * @return
	 */
	public boolean isToday() {
		Calendar today = Calendar.getInstance();
		return (gregorianDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)) && (gregorianDate.get(Calendar.MONTH) == today.get(Calendar.MONTH))
				&& (gregorianDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH));
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("[Gregorian:");
		result.append(gregorianDate.get(Calendar.YEAR));
		result.append('-');
		result.append(gregorianDate.get(Calendar.MONTH));
		result.append('-');
		result.append(gregorianDate.get(Calendar.DAY_OF_MONTH));
		result.append("] [Lunar:");
		result.append(lunarFields[LUNAR_YEAR]);
		result.append('-');
		result.append(lunarFields[LUNAR_MONTH]);
		result.append('-');
		result.append(lunarFields[LUNAR_DAY]);
		result.append('(');
		result.append(lunarFields[LUNAR_IS_LEAP]);
		result.append(")]");
		return result.toString();
	}

	/**
	 * 由公历计算农历
	 */
	private void computeLunar() {
		if ((gregorianDate.get(Calendar.YEAR) <= YEAR_MIN && gregorianDate.get(Calendar.MONTH) < 1) || gregorianDate.get(Calendar.YEAR) > YEAR_MAX) {
			throw new IllegalArgumentException("Current lunar calendar only support the year from 1901 to 2100.");
		}
		// 取得与农历1900-1-1相差的天数
		long offset = (gregorianDate.getTimeInMillis() - LUNAR_BASE_MILLIS) / DAY_MILLIS;
		int year = YEAR_MIN;
		int daysOfYear = 0;
		for (; year < YEAR_MAX && offset > 0; year++) {
			daysOfYear = getDaysOfLunarYear(year);
			offset -= daysOfYear;
		}

		if (offset < 0) {
			offset += daysOfYear;
			year--;
		}

		lunarFields[LUNAR_YEAR] = year;
		int leapMonth = getLeapMonthOfLunar(year);
		lunarFields[LUNAR_IS_LEAP] = 0;

		int month = 1;
		int daysOfMonth = 0;
		for (; month < 13 && offset > 0; month++) {
			if (leapMonth > 0 && month == (leapMonth + 1) && lunarFields[LUNAR_IS_LEAP] == 0) {
				month--;
				lunarFields[LUNAR_IS_LEAP] = 1;
				daysOfMonth = getLeapDaysOfLunar(year);
			} else {
				daysOfMonth = getDaysOfLunarMonth(year, month);
			}

			if (lunarFields[LUNAR_IS_LEAP] == 1 && month == leapMonth + 1) {
				lunarFields[LUNAR_IS_LEAP] = 0;
			}

			offset -= daysOfMonth;
		}

		if (offset == 0 && leapMonth > 0 && month == leapMonth + 1) {
			if (lunarFields[LUNAR_IS_LEAP] == 1) {
				lunarFields[LUNAR_IS_LEAP] = 0;
			} else {
				lunarFields[LUNAR_IS_LEAP] = 1;
				month--;
			}
		}

		if (offset < 0) {
			offset += daysOfMonth;
			month--;
		}

		lunarFields[LUNAR_MONTH] = month;
		lunarFields[LUNAR_DAY] = (int) offset + 1;
	}

	/**
	 * 根据公历日历计算农历日期
	 * @param y 公历年
	 * @param m 公历月
	 * @param d 公历日
	 * @return lunarFields[i]，0农历年，1农历月，2农历日，3是否有闰月
	 */
	public static long[] computeLunar(int y, int m, int d) {
		long[] lunarFields = new long[LUNAR_FIELD_COUNT];
		Calendar gregorianDate = Calendar.getInstance();
		gregorianDate.set(Calendar.YEAR, y);
		gregorianDate.set(Calendar.MONTH, m);
		gregorianDate.set(Calendar.DAY_OF_MONTH, d);
		if ((gregorianDate.get(Calendar.YEAR) <= YEAR_MIN && gregorianDate.get(Calendar.MONTH) < 1) || gregorianDate.get(Calendar.YEAR) > YEAR_MAX) {
			throw new IllegalArgumentException("Current lunar calendar only support the year from" + YEAR_MIN +  " to "  + YEAR_MAX + ",now is " +  gregorianDate.get(Calendar.YEAR));
		}
		// 取得与农历1900-1-1相差的天数
		long offset = (gregorianDate.getTimeInMillis() - LUNAR_BASE_MILLIS) / DAY_MILLIS;
		int year = YEAR_MIN;
		int daysOfYear = 0;
		for (; year < YEAR_MAX && offset > 0; year++) {
			daysOfYear = getDaysOfLunarYear(year);
			offset -= daysOfYear;
		}

		if (offset < 0) {
			offset += daysOfYear;
			year--;
		}

		lunarFields[LUNAR_YEAR] = year;
		int leapMonth = getLeapMonthOfLunar(year);
		lunarFields[LUNAR_IS_LEAP] = 0;

		int month = 1;
		int daysOfMonth = 0;
		for (; month < 13 && offset > 0; month++) {
			if (leapMonth > 0 && month == (leapMonth + 1) && lunarFields[LUNAR_IS_LEAP] == 0) {
				month--;
				lunarFields[LUNAR_IS_LEAP] = 1;
				daysOfMonth = getLeapDaysOfLunar(year);
			} else {
				daysOfMonth = getDaysOfLunarMonth(year, month);
			}

			if (lunarFields[LUNAR_IS_LEAP] == 1 && month == leapMonth + 1) {
				lunarFields[LUNAR_IS_LEAP] = 0;
			}

			offset -= daysOfMonth;
		}

		if (offset == 0 && leapMonth > 0 && month == leapMonth + 1) {
			if (lunarFields[LUNAR_IS_LEAP] == 1) {
				lunarFields[LUNAR_IS_LEAP] = 0;
			} else {
				lunarFields[LUNAR_IS_LEAP] = 1;
				month--;
			}
		}

		if (offset < 0) {
			offset += daysOfMonth;
			month--;
		}

		lunarFields[LUNAR_MONTH] = month;
		lunarFields[LUNAR_DAY] = (int) offset + 1;

		return lunarFields;
	}

	/**
	 * 根据公历日历计算农历日期，限制最大日期
	 * @param y 公历年
	 * @param m 公历月
	 * @param d 公历日
	 * @return lunarFields[i]，0农历年，1农历月，2农历日，3是否有闰月
	 */
	public static long[] computeLunarLimitMaxDate(int y, int m, int d) {
		long[] lunarFields = new long[LUNAR_FIELD_COUNT];
		Calendar gregorianDate = Calendar.getInstance();
		gregorianDate.set(Calendar.YEAR, y);
		gregorianDate.set(Calendar.MONTH, m);
		gregorianDate.set(Calendar.DAY_OF_MONTH, d);
		if ((gregorianDate.get(Calendar.YEAR) <= YEAR_MIN && gregorianDate.get(Calendar.MONTH) < 1) || gregorianDate.get(Calendar.YEAR) > YEAR_MAX_2037) {
			throw new IllegalArgumentException("Current lunar calendar only support the year from" + YEAR_MIN +  " to "  + YEAR_MAX_2037 + ",now is " +  gregorianDate.get(Calendar.YEAR));
		}
		// 取得与农历1900-1-1相差的天数
		long offset = (gregorianDate.getTimeInMillis() - LUNAR_BASE_MILLIS) / DAY_MILLIS;
		int year = YEAR_MIN;
		int daysOfYear = 0;
		for (; year < YEAR_MAX_2037 && offset > 0; year++) {
			daysOfYear = getDaysOfLunarYear(year);
			offset -= daysOfYear;
		}

		if (offset < 0) {
			offset += daysOfYear;
			year--;
		}

		lunarFields[LUNAR_YEAR] = year;
		int leapMonth = getLeapMonthOfLunar(year);
		lunarFields[LUNAR_IS_LEAP] = 0;

		int month = 1;
		int daysOfMonth = 0;
		for (; month < 13 && offset > 0; month++) {
			if (leapMonth > 0 && month == (leapMonth + 1) && lunarFields[LUNAR_IS_LEAP] == 0) {
				month--;
				lunarFields[LUNAR_IS_LEAP] = 1;
				daysOfMonth = getLeapDaysOfLunar(year);
			} else {
				daysOfMonth = getDaysOfLunarMonth(year, month);
			}

			if (lunarFields[LUNAR_IS_LEAP] == 1 && month == leapMonth + 1) {
				lunarFields[LUNAR_IS_LEAP] = 0;
			}

			offset -= daysOfMonth;
		}

		if (offset == 0 && leapMonth > 0 && month == leapMonth + 1) {
			if (lunarFields[LUNAR_IS_LEAP] == 1) {
				lunarFields[LUNAR_IS_LEAP] = 0;
			} else {
				lunarFields[LUNAR_IS_LEAP] = 1;
				month--;
			}
		}

		if (offset < 0) {
			offset += daysOfMonth;
			month--;
		}

		lunarFields[LUNAR_MONTH] = month;
		lunarFields[LUNAR_DAY] = (int) offset + 1;

		return lunarFields;
	}

	// 返回农历指定月份的天数
	private static int getDaysOfLunarMonth(int year, int month) {
		return (LUNAR_INFO[year - YEAR_MIN] & (0x10000 >> month)) == 0 ? 29 : 30;
	}

	// 取得农历年天数
	private static int getDaysOfLunarYear(int year) {
		int result = 348;
		for (int i = 0x8000; i > 0x8; i >>= 1) {
			result += (LUNAR_INFO[year - YEAR_MIN] & i) == 0 ? 0 : 1;
		}
		return result + getLeapDaysOfLunar(year);
	}

	// 取得农历年闰月天数
	private static int getLeapDaysOfLunar(int year) {
		if (getLeapMonthOfLunar(year) > 0) {
			return (LUNAR_INFO[year - YEAR_MIN + 1] & 0xF) == 0xF ? 30 : 29;
		} else {
			return 0;
		}
	}

	// 取得农历年闰月月份 1-12,如果没闰,返回0
	private static int getLeapMonthOfLunar(int year) {
		int leapMonth = (int) (LUNAR_INFO[year - YEAR_MIN] & 0xF);
		return leapMonth == 0xF ? 0 : leapMonth;
	}



}
