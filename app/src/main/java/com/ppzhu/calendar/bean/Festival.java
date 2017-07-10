package com.ppzhu.calendar.bean;

/**
 * 节假日信息实体类
 * */
public class Festival {
	//节假日时间 yyMMdd
	private String day;
	//年份 yyMM
	private String year;
	//月份 MM
	private String month;
	//创建者 xg，服务器返回，并没有保存在数据库里
	private String createUser;
	//节假日名称
	private String festival;
	//是否放假，0班，1假
	private int isFestival;

	public int getIsFestival() {
		return isFestival;
	}

	public void setIsFestival(int isFestival) {
		this.isFestival = isFestival;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFestival() {
		return festival;
	}

	public void setFestival(String festival) {
		this.festival = festival;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public String getDay() {
		return day;
	}

	public void setDay(String day) {
		this.day = day;
	}

	@Override
	public String toString() {
		return " 【 isFestival  = " + isFestival +
				"  year  =  " + year +
				" month  =  " + month +
				" day  =  " + day +
				" 】 ";
	}
}
