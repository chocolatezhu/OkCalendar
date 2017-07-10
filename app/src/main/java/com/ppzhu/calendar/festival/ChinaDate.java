package com.ppzhu.calendar.festival;

import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * @author zzl
 * 获取农历日期、公历和公历节假日、24节气
 * Created on 2016/9/23
 * */
public class ChinaDate {
    final private static long[] lunarInfo = new long[]{0x04bd8, 0x04ae0,
            0x0a570, 0x054d5, 0x0d260, 0x0d950, 0x16554, 0x056a0, 0x09ad0,
            0x055d2, 0x04ae0, 0x0a5b6, 0x0a4d0, 0x0d250, 0x1d255, 0x0b540,
            0x0d6a0, 0x0ada2, 0x095b0, 0x14977, 0x04970, 0x0a4b0, 0x0b4b5,
            0x06a50, 0x06d40, 0x1ab54, 0x02b60, 0x09570, 0x052f2, 0x04970,
            0x06566, 0x0d4a0, 0x0ea50, 0x06e95, 0x05ad0, 0x02b60, 0x186e3,
            0x092e0, 0x1c8d7, 0x0c950, 0x0d4a0, 0x1d8a6, 0x0b550, 0x056a0,
            0x1a5b4, 0x025d0, 0x092d0, 0x0d2b2, 0x0a950, 0x0b557, 0x06ca0,
            0x0b550, 0x15355, 0x04da0, 0x0a5d0, 0x14573, 0x052d0, 0x0a9a8,
            0x0e950, 0x06aa0, 0x0aea6, 0x0ab50, 0x04b60, 0x0aae4, 0x0a570,
            0x05260, 0x0f263, 0x0d950, 0x05b57, 0x056a0, 0x096d0, 0x04dd5,
            0x04ad0, 0x0a4d0, 0x0d4d4, 0x0d250, 0x0d558, 0x0b540, 0x0b5a0,
            0x195a6, 0x095b0, 0x049b0, 0x0a974, 0x0a4b0, 0x0b27a, 0x06a50,
            0x06d40, 0x0af46, 0x0ab60, 0x09570, 0x04af5, 0x04970, 0x064b0,
            0x074a3, 0x0ea50, 0x06b58, 0x055c0, 0x0ab60, 0x096d5, 0x092e0,
            0x0c960, 0x0d954, 0x0d4a0, 0x0da50, 0x07552, 0x056a0, 0x0abb7,
            0x025d0, 0x092d0, 0x0cab5, 0x0a950, 0x0b4a0, 0x0baa4, 0x0ad50,
            0x055d9, 0x04ba0, 0x0a5b0, 0x15176, 0x052b0, 0x0a930, 0x07954,
            0x06aa0, 0x0ad50, 0x05b52, 0x04b60, 0x0a6e6, 0x0a4e0, 0x0d260,
            0x0ea65, 0x0d530, 0x05aa0, 0x076a3, 0x096d0, 0x04bd7, 0x04ad0,
            0x0a4d0, 0x1d0b6, 0x0d250, 0x0d520, 0x0dd45, 0x0b5a0, 0x056d0,
            0x055b2, 0x049b0, 0x0a577, 0x0a4b0, 0x0aa50, 0x1b255, 0x06d20,
            0x0ada0};

//final private static int[] year20 = new int[] { 1, 4, 1, 2, 1, 2, 1, 1, 2,
//		1, 2, 1 };


//final private static int[] year2000 = new int[] { 0, 3, 1, 2, 1, 2, 1, 1,
//		2, 1, 2, 1 };

    public final static String[] nStr1 = new String[]{"", "正", "二", "三", "四",
            "五", "六", "七", "八", "九", "十", "十一", "十二"};

    public final static String[] LeapMonthStr = new String[]{"", "一", "二", "三", "四",
            "五", "六", "七", "八", "九", "十", "十一", "十二"};

    private final static String[] Gan = new String[]{"甲", "乙", "丙", "丁", "戊",
            "己", "庚", "辛", "壬", "癸"};

    private final static String[] Zhi = new String[]{"子", "丑", "寅", "卯", "辰",
            "巳", "午", "未", "申", "酉", "戌", "亥"};

    private final static String[] Animals = new String[]{"鼠", "牛", "虎", "兔",
            "龙", "蛇", "马", "羊", "猴", "鸡", "狗", "猪"};

    private final static String[] solarTerm = new String[]{"小寒", "大寒", "立春",
            "雨水", "惊蛰", "春分", "清明", "谷雨", "立夏", "小满", "芒种", "夏至", "小暑", "大暑",
            "立秋", "处暑", "白露", "秋分", "寒露", "霜降", "立冬", "小雪", "大雪", "冬至"};

    //公历节日
    private final static String[] solarHoliday = new String[]{
            //需要特殊处理的节日，母亲节（5月第二个星期日）、父亲节（6月第三个星期日）、感恩节（11月第四个星期四）
            "0101 元旦节",
            "0214 情人节",
            "0303 爱耳日",
            "0308 妇女节",
            "0312 植树节",
            "0315 消权日",
            "0401 愚人节",
            "0422 地球日",
            "0501 劳动节",
            "0504 青年节",
            "0512 护士节",
            "0515 家庭日",
            "0531 无烟日",
            "0601 儿童节",
            "0605 环境日",
            "0606 爱眼日",
            "0701 建党节",
            "0707 七七事变",
            "0801 建军节",
            "0815 日本投降",
            "0910 教师节",
            "0920 爱牙日",
            "1001 国庆节",
            "1010 辛亥革命",
            "1031 万圣节",
            "1111 光棍节",
            "1201 艾滋病日",
            "1210 人权日",
            "1220 澳门回归",
            "1224 平安夜",
            "1225 圣诞节",
    };

    //农历节日
    private final static String[] lunarHoliday = new String[]{
            //需要特殊处理的节日，除夕（农历12月最后一天）
            "0101 春节",
            "0115 元宵节",
            "0505 端午节",
            "0707 七夕节",
            "0815 中秋节",
            "0715 中元节",
            "0909 重阳节",
            "1208 腊八节",
            "1223 小年(北)",
            "1224 小年(南)",
    };

    /**
     * 传回农历 y年的总天数
     *
     * @param y
     * @return
     */
    private static int lYearDays(int y) {
        int i, sum = 348;
        for (i = 0x8000; i > 0x8; i >>= 1) {
            if ((lunarInfo[y - 1900] & i) != 0)
                sum += 1;
        }
        return (sum + leapDays(y));
    }

    /**
     * 传回农历 y年闰月的天数
     *
     * @param y
     * @return
     */
    private static int leapDays(int y) {
        if (leapMonth(y) != 0) {
            if ((lunarInfo[y - 1900] & 0x10000) != 0)
                return 30;
            else
                return 29;
        } else {
            return 0;
        }
    }

    /**
     * 传回农历 y年闰哪个月 1-12 , 没闰传回 0
     *
     * @param y
     * @return
     */
    private static int leapMonth(int y) {
        return (int) (lunarInfo[y - 1900] & 0xf);
    }

    /**
     * 传回农历 y年m月的总天数(大月小月)
     *
     * @param y
     * @param m
     * @return
     */
    public static int monthDays(int y, int m) {
        if ((lunarInfo[y - 1900] & (0x10000 >> m)) == 0)
            return 29;
        else {
            return 30;
        }
    }

    /**
     * 传回农历 y年的生肖
     *
     * @param y
     * @return
     */
    public static String AnimalsYear(int y) {
        return Animals[(y - 4) % 12];
    }

    /**
     * 传入 月日的offset 传回干支,0=甲子
     *
     * @param num
     * @return
     */
    private static String cyclicalm(int num) {
        return (Gan[num % 10] + Zhi[num % 12]);
    }

    /**
     * 传入 offset 传回干支, 0=甲子
     *
     * @param y
     * @return
     */
    public static String cyclical(int y) {
        int num = y - 1900 + 36;
        return (cyclicalm(num));
    }

    /**
     * 传出y年m月d日对应的农历.year0 .month1 .day2 .yearCyl3 .monCyl4 .dayCyl5 .isLeap6
     *
     * @param y 公历年
     * @param m 公历月
     * @param d 公历日
     * @return 返回农历日期
     */
    public static long[] calElement(int y, int m, int d) {
        long[] nongDate = new long[7];
        int i = 0, temp = 0, leap = 0;
        Date baseDate = new GregorianCalendar(0 + 1900, 0, 31).getTime();
        Date objDate = new GregorianCalendar(y, m - 1, d).getTime();
        long offset = (objDate.getTime() - baseDate.getTime()) / 86400000L;
        nongDate[5] = offset + 40;
        nongDate[4] = 14;

        for (i = 1900; i < 2050 && offset > 0; i++) {
            temp = lYearDays(i);
            offset -= temp;
            nongDate[4] += 12;
        }
        if (offset < 0) {
            offset += temp;
            i--;
            nongDate[4] -= 12;
        }
        nongDate[0] = i;
        nongDate[3] = i - 1864;
        leap = leapMonth(i); // 闰哪个月,没闰返回0
        nongDate[6] = 0;

        for (i = 1; i < 13 && offset > 0; i++) {
            // 闰月
            if (leap > 0 && i == (leap + 1) && nongDate[6] == 0) {
                --i;
                nongDate[6] = 1;
                temp = leapDays((int) nongDate[0]);
            } else {
                temp = monthDays((int) nongDate[0], i);
            }

            // 解除闰月
            if (nongDate[6] == 1 && i == (leap + 1))
                nongDate[6] = 0;
            offset -= temp;
            if (nongDate[6] == 0)
                nongDate[4]++;
        }

        if (offset == 0 && leap > 0 && i == leap + 1) {
            if (nongDate[6] == 1) {
                nongDate[6] = 0;
            } else {
                nongDate[6] = 1;
                --i;
                --nongDate[4];
            }
        }
        if (offset < 0) {
            offset += temp;
            --i;
            --nongDate[4];
        }
        nongDate[1] = i;
        nongDate[2] = offset + 1;
        return nongDate;
    }

    /**
     * 传入天数可以 得到旧历的显示
     *
     * @param day
     * @return
     */
    public static String getChinaDate(int day) {
        String a = "";
        if (day == 10)
            return "初十";
        if (day == 20)
            return "二十";
        if (day == 30)
            return "三十";
        int two = (int) ((day) / 10);
        if (two == 0)
            a = "初";
        if (two == 1)
            a = "十";
        if (two == 2)
            a = "廿";
        if (two == 3)
            a = "卅";
        int one = (int) (day % 10);
        switch (one) {
            case 1:
                a += "一";
                break;
            case 2:
                a += "二";
                break;
            case 3:
                a += "三";
                break;
            case 4:
                a += "四";
                break;
            case 5:
                a += "五";
                break;
            case 6:
                a += "六";
                break;
            case 7:
                a += "七";
                break;
            case 8:
                a += "八";
                break;
            case 9:
                a += "九";
                break;
        }
        return a;
    }

    /**
     *  获取公历、农历节假日、24节气名称
     *  <P> 返回优先顺序：农历节日—公历节日—24节气 </P>
     * @param y 公历年
     *  @param m 公历月
     * @param d 公历日
     * @param l
     * */
    public static String getFestivalAndSolar(int y, int m, int d, long[] l) {
        //返回农历节假日名称
        String lunarHoliday = getLunarHoliday((int)l[0], (int)l[1], (int)l[2]);
        if (!TextUtils.isEmpty(lunarHoliday)) {
            return lunarHoliday;
        }

        //返回公历节假日名称
        String solarHoliday = getSolarHoliday(y, m, d);
        if (!TextUtils.isEmpty(solarHoliday)) {
            return solarHoliday;
        }

        //返回不是固定日期的节假日
        String specialFestivalDay = getSpecialFestivalDay(y, m, d);
        if (!TextUtils.isEmpty(specialFestivalDay)) {
            return specialFestivalDay;
        }

        //返回24节气
        if (m > 0 && m < 13) {
            String solarTermName =  SolarTermsUtil.getSolarTermName(y, m, d);
            if (!TextUtils.isEmpty(solarTermName)) {
                return solarTermName;
            }
        }

        //返回农历日期
        String chinaDate = ChinaDate.getChinaDate((int)l[2]);
        if (chinaDate.equals("初一")) {
            if (l[1] == 12) {
                //农历12月是腊月
                chinaDate = "腊月";
            } else if (0 != (int)l[3]){
                chinaDate = "闰" + getLeapMonthStr((int)l[1]) + "月";
            } else {
                chinaDate = nStr1[(int)l[1]] + "月";
            }
        }
        return chinaDate;
    }

    /**
     * 获取农历节日
     * @param y 农历年
     * @param m 农历月
     * @param d 农历日
     * */
    public static String getLunarHoliday(int y, int m, int d) {
        if (((m) == 1) && d == 1) {
            return  "春节";
        } else if (((m) == 1) && d == 15) {
            return "元宵节";
        } else if (((m) == 5) && d == 5) {
            return "端午节";
        } else if ((m == 7) && d == 7) {
            return "七夕节";
        } else if ((m == 7) && d == 15) {
            return "中元节";
        } else if (((m) == 8) && d == 15) {
            return "中秋节";
        } else if ((m == 9) && d == 9) {
            return "重阳节";
        } else if ((m == 12) && d == 8) {
            return "腊八节";
        } else if ((m == 12) && d == 23) {
            return "小年(北)";
        } else if ((m == 12) && d == 24) {
            return "小年(南)";
        } else {
            if (m == 12) {
                if ((((monthDays(y, m) == 29) && d == 29))
                        || ((((monthDays(y, m) == 30) && d == 30)))) {
                    return "除夕";
                } else {
                    return "";
                }
            } else {
                return "";
            }
        }
    }

    /**
     * 获取公历节日
     * @param y 公历年
     * @param m 公历月
     * @param d 公历日
     * */
    public static String getSolarHoliday(int y, int m, int d) {
        if (m == 1 && d == 1) {
            return "元旦节";
        } else if (m == 2 && d == 14) {
            return "情人节";
        } else if (m == 3 && d == 3) {
            return "爱耳日";
        } else if (m == 3 && d == 8) {
            return "妇女节";
        } else if (m == 3 && d == 12) {
            return "植树节";
        } else if (m == 3 && d == 15) {
            return "消权日";
        } else if (m == 4 && d == 1) {
            return "愚人节";
        } else if (m == 4 && d == 22) {
            return "地球日";
        } else if (m == 5 && d == 1) {
            return "劳动节";
        } else if (m == 5 && d == 4) {
            return "青年节";
        } else if (m == 5 && d == 12) {
            return "护士节";
        } else if (m == 5 && d == 15) {
            return "家庭日";
        } else if (m == 5 && d == 31) {
            return "无烟日";
        } else if (m == 6 && d == 1) {
            return "儿童节";
        } else if (m == 6 && d == 5) {
            return "环境日";
        } else if (m == 6 && d == 6) {
            return "爱眼日";
        } else if (m == 7 && d == 1) {
            return "建党节";
        } else if (m == 7 && d == 7) {
            return "七七事变";
        } else if (m == 8 && d == 1) {
            return "建军节";
        } else if (m == 8 && d == 15) {
            return "日本投降";
        } else if (m == 9 && d == 10) {
            return "教师节";
        } else if (m == 9 && d == 20) {
            return "爱牙日";
        } else if (m == 10 && d == 1) {
            return "国庆节";
        } else if (m == 10 && d == 10) {
            return "辛亥革命";
        } else if (m == 10 && d == 31) {
            return "万圣节";
        } else if (m == 11 && d == 11) {
            return "光棍节";
        } else if (m == 12 && d == 1) {
            return "艾滋病日";
        } else if (m == 12 && d == 10) {
            return "人权日";
        } else if (m == 12 && d == 20) {
            return "澳门回归";
        } else if (m == 12 && d == 24) {
            return "平安夜";
        } else if (m == 12 && d == 25) {
            return "圣诞节";
        } else {
            return "";
        }
    }


    /**
     *  获取公历、农历节假日、24节气名称
     *  <P> 返回优先顺序：农历节日—公历节日—24节气 </P>
     *  @param y 公历年
     *  @param m 公历月
     *  @param d 公历日
     * */
    public static String getChinaDay(int y, int m, int d) {
        //计算农历日期
        long[] l = ChinaDate.calElement(y, m, d);

        //返回农历节假日名称
        if (12 == l[1]) {
            //小月除夕是29号
            if (29 == monthDays((int)l[0], (int)l[1])){
                if (29 == l[2]) {
                    return "除夕";
                }
            } else {
                //大月除夕是30号
                if (30 == l[2]) {
                    return "除夕";
                }
            }
        }
        for (String aLunarHoliday : lunarHoliday) {
            String lDate = aLunarHoliday.split(" ")[0];   //节假日的日期
            String lName = aLunarHoliday.split(" ")[1];  //节假日的名称
            String lMonth = l[1] + "";
            String lDay = l[2] + "";
            if (l[1] < 10) {
                lMonth = "0" + l[1];
            }
            if (l[2] < 10) {
                lDay = "0" + l[2];
            }
            String lMonthDay = lMonth + lDay;
            if (lDate.trim().equals(lMonthDay.trim())) {
                return lName;
            }
        }

        //返回公历节假日名称
        for (String aSolarHoliday : solarHoliday) {
            String sDate = aSolarHoliday.split(" ")[0];  //节假日的日期
            String sName = aSolarHoliday.split(" ")[1]; //节假日的名称
            String sMonth = m + "";
            String sDay = d + "";
            if (m < 10) {
                sMonth = "0" + sMonth;
            }
            if (d < 10) {
                sDay = "0" + sDay;
            }
            String sMonthDay = sMonth + sDay;
            if (sDate.trim().equals(sMonthDay.trim())) {
                return sName;
            }
        }

        //返回不是固定日期的节假日
        String specialFestivalDay = getSpecialFestivalDay(y, m, d);
        if (!TextUtils.isEmpty(specialFestivalDay)) {
            return specialFestivalDay;
        }

        //返回24节气
        if (m > 0 && m < 13) {
            String solarTermName =  SolarTermsUtil.getSolarTermName(y, m, d);
            if (!TextUtils.isEmpty(solarTermName)) {
                return solarTermName;
            }
        }

        //返回农历日期
        String chinaDate = ChinaDate.getChinaDate((int) (l[2]));
        if (chinaDate.equals("初一")) {
            if (l[1] == 12) {
                //农历12月是腊月
                chinaDate = "腊月";
            } else if (0 != l[6]){
                chinaDate = "闰" + getLeapMonthStr((int)l[1]) + "月";
            } else {
                chinaDate = nStr1[(int) l[1]] + "月";
            }
        }
        return chinaDate;
    }

    /**
     * 将闰月转为字符串输出
     * @param leapMonth 要闰的月份
     * */
    private static String getLeapMonthStr(int leapMonth) {
        return LeapMonthStr[leapMonth];
    }

    /**
     * 获取不是固定日期的节假日
     * @param y 公历年
     * @param m 公历月
     * @param d 公历日
     * @return 返回节假日名字
     * */
    private static String getSpecialFestivalDay(int y, int m, int d) {
        String specialFestivalDay = "";
        switch (m - 1){
            case Calendar.MAY:
                //母亲节，公历5月的第二个周日
                Calendar motherDayCalendar = Calendar.getInstance();
                motherDayCalendar.set(Calendar.YEAR, y);
                motherDayCalendar.set(Calendar.MONTH, Calendar.MAY);
                motherDayCalendar.set(Calendar.DAY_OF_MONTH, 1);
                int weekDay1 = motherDayCalendar.get(Calendar.DAY_OF_WEEK);
                int count1 = 2;
                if (weekDay1 <= Calendar.SUNDAY) {
                    count1 = 1;
                }
                int motherDayOffset = weekDay1 - Calendar.SUNDAY;
                motherDayCalendar.set(Calendar.DAY_OF_MONTH, 1 + count1 * 7 - motherDayOffset);
                int motherDay = motherDayCalendar.get(Calendar.DAY_OF_MONTH);
                if (d == motherDay) {
                    specialFestivalDay = "母亲节";
                }
                break;
            case Calendar.JUNE:
                //父亲节，公历6月的第三个周日
                Calendar fatherDayCalendar = Calendar.getInstance();
                fatherDayCalendar.set(Calendar.YEAR, y);
                fatherDayCalendar.set(Calendar.MONTH, Calendar.JUNE);
                fatherDayCalendar.set(Calendar.DAY_OF_MONTH, 1);
                int weekDay2 = fatherDayCalendar.get(Calendar.DAY_OF_WEEK);
                int count2 = 3;
                if (weekDay2 <= Calendar.SUNDAY) {
                    count2 = 2;
                }
                int fatherDayOffset = weekDay2 - Calendar.SUNDAY;
                fatherDayCalendar.set(Calendar.DAY_OF_MONTH, 1 + count2 * 7 - fatherDayOffset);
                int fatherDay = fatherDayCalendar.get(Calendar.DAY_OF_MONTH);
                if (d == fatherDay) {
                    specialFestivalDay = "父亲节";
                }
                break;
            case Calendar.NOVEMBER:
                //感恩节，公历11月的第四个周四
                Calendar thanksDayCalendar = Calendar.getInstance();
                thanksDayCalendar.set(Calendar.YEAR, y) ;
                thanksDayCalendar.set(Calendar.MONTH, Calendar.NOVEMBER);
                thanksDayCalendar.set(Calendar.DAY_OF_MONTH, 1);
                int weekDay3 = thanksDayCalendar.get(Calendar.DAY_OF_WEEK);
                int count3 = 4;
                if (weekDay3 <= Calendar.THURSDAY) {
                    count3 = 3;
                }
                int thanksDayOffset = weekDay3 - Calendar.THURSDAY;
                thanksDayCalendar.set(Calendar.DAY_OF_MONTH, 1 + count3 * 7 - thanksDayOffset);
                int thanksDay = thanksDayCalendar.get(Calendar.DAY_OF_MONTH);
                if (d == thanksDay) {
                    specialFestivalDay = "感恩节";
                }
                break;
            default:
                break;
        }
        return specialFestivalDay;
    }

    public static String today() {
        Calendar today = Calendar.getInstance(Locale.SIMPLIFIED_CHINESE);
        int year = today.get(Calendar.YEAR);
        int month = today.get(Calendar.MONTH) + 1;
        int date = today.get(Calendar.DATE);
        long[] l = calElement(year, month, date);
        StringBuffer sToday = new StringBuffer();
        try {
            sToday.append(sdf.format(today.getTime()));
            sToday.append(" 农历");
            sToday.append(cyclical(year));
            sToday.append('(');
            sToday.append(AnimalsYear(year));
            sToday.append(")年");
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) (l[2])));
            return sToday.toString();
        } finally {
            sToday = null;
        }
    }

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日 EEEEE");

    /**
     * 农历日历工具使用演示
     *
     * @param
     */
    public static void test() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DATE, 1);
        long[] l;
        for (int i = 0; i < 100; i++) {
            l = calElement(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
            StringBuffer sToday = new StringBuffer();
            sToday.append(sdf.format(cal.getTime()));
            sToday.append(" 农历");
            sToday.append(cyclical(cal.get(Calendar.YEAR)));
            sToday.append('(');
            sToday.append(AnimalsYear(cal.get(Calendar.YEAR)));
            sToday.append(")年");
            sToday.append(nStr1[(int) l[1]]);
            sToday.append("月");
            sToday.append(getChinaDate((int) (l[2])));

            cal.add(Calendar.DATE, 1);
        }
    }

    public static ArrayList<HashMap<String, String>> getChinaDates(int currentyear, int month) {
        SimpleDateFormat myfmt = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();   //Locale.SIMPLIFIED_CHINESE
        cal.set(Calendar.YEAR, currentyear);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DATE, 1);
        long[] l;
        HashMap<String, String> map;
        while (cal.get(Calendar.MONTH) == month) {
            l = calElement(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DATE));
            map = new HashMap<>();
            map.put("date", myfmt.format(cal.getTime()));
            String chinadate = getChinaDate((int) (l[2]));
            if (chinadate.equals("初一")) {
                chinadate = nStr1[(int) l[1]] + "月";
            }
            map.put("chinadate", chinadate);
            list.add(map);
            cal.add(Calendar.DATE, 1);
        }
        return list;
    }

}
