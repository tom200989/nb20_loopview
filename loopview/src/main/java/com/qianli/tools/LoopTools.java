package com.qianli.tools;

import android.support.annotation.IntRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Created by qianli.ma on 2018/12/3 0003.
 */
public class LoopTools {

    /*
     *  本类封装一些常用的方法可以直接使用
     * */

    /**
     * 获取英文的月份
     *
     * @return 月份英文字符集合
     */
    public static List<String> getMonthsEnglish() {
        List<String> months = new ArrayList<>();
        months.add("January");
        months.add("February");
        months.add("March");
        months.add("April");
        months.add("May");
        months.add("June");
        months.add("July");
        months.add("August");
        months.add("September");
        months.add("October");
        months.add("November");
        months.add("December");
        return months;
    }

    /**
     * 根据月份取出对应的当月日数
     *
     * @param month 当前第几月份
     */
    public static List<String> getDaysFromMonths(@IntRange(from = 1, to = 12) int month, int year) {
        Integer[] bigMonth = {1, 3, 5, 7, 8, 10, 12};// 大月
        Integer[] smallMonth = {2, 4, 6, 9, 11};// 小月
        List<Integer> bigMonthList = Arrays.asList(bigMonth);
        List<Integer> smallMonthList = Arrays.asList(smallMonth);
        if (bigMonthList.contains(month)) {
            return getDaysTxt(31);
        } else {
            if (month == 2) {
                return getDaysTxt(year % 4 == 0 ? 29 : 28);
            } else {
                return getDaysTxt(30);
            }
        }
    }

    /**
     * 根据天数转换成集合
     *
     * @param dayNum 天数
     * @return 集合(" 0 ", " 1 ", " 2 " ...)
     */
    private static List<String> getDaysTxt(int dayNum) {
        List<String> days = new ArrayList<>();
        for (int i = 1; i <= dayNum; i++) {
            days.add(String.valueOf(i));
        }
        return days;
    }
}
