package com.telecom.ecloudframework.sys.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DateUtil {


    /**
     * 返回当前时间
     */
    public static String getCurrentTime(){
        return convertDate(new Date());
    }


    /**
     * 计算时间差几天、几时
     */
    public static Map<String,String> getDiff(Date start, Date end){
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = end.getTime() - start.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        Map<String,String> diffMap = new HashMap<>();
        diffMap.put("day",day+"");
        diffMap.put("hour",hour+"");
        return diffMap;
    }


    /**
     * 获取当前时间 + 晚n小时时间
     */

    public static String getDelay(int delayMin) {
        long currentTime = System.currentTimeMillis();
        currentTime += delayMin * 60 * 1000;
        Date date = new Date(currentTime);
        return convertDate(date);
    }


    /**
     * 转换Date为字符串
     */
    public static String convertDate(Date date){
        if(date == null || "".equals(date)){
            return null;
        }
        SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        String dateStr = date.toString();
        String formatStr = "";
        try{
            formatStr = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format((Date) formatter.parse(dateStr));
            String hour = dateStr.substring(11,13);
            formatStr = formatStr.substring(0,11) + hour + formatStr.substring(13,formatStr.length());
        }catch(Exception e){

        }finally {
            return formatStr;
        }
    }


    /**
     * 转换字符串为Date
     */
    public static Date convertStr(String str){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

}
