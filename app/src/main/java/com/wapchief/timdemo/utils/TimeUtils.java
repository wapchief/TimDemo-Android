package com.wapchief.timdemo.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by wapchief on 2017/8/1.
 * 日期毫秒互换辅助类
 * dataForamt:yyyy-MM-dd //格式
 * yyyy-MM-dd HH:mm:ss
 */

public class TimeUtils {

    //时间转化毫秒
    public static long date2ms(String dateForamt, String time) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat(dateForamt).parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
       return calendar.getTimeInMillis();
    }

    //毫秒转化成日期
    public static String ms2date(String dateForamt, long ms){
        Date date = new Date(ms);
        SimpleDateFormat format = new SimpleDateFormat(dateForamt);
        return format.format(date);
    }

    //Unix时间戳转换成指定格式日期字符串
    public static String TimeStamp2Date(String timestampString, String formats) {
        if (TextUtils.isEmpty(formats))
            formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = Long.parseLong(timestampString) * 1000;
        String date = new SimpleDateFormat(formats, Locale.CHINA).format(new Date(timestamp));
        return date;
    }
}
