package com.hcrcms.core.web.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleDateFormat sff = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat sfm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static SimpleDateFormat sfz = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");	//带时区
	public static SimpleDateFormat sfymd = new SimpleDateFormat("yyyy/MM/dd");
	public static SimpleDateFormat sfym = new SimpleDateFormat("yyyy/MM");
	public static SimpleDateFormat sfimg = new SimpleDateFormat("yyyyMM");				//创建图片目录时用
	
	public static String convertString(Date date){
		return sf.format(date);
	}
	
	public static Date convertDate(String date) throws ParseException{
		return sf.parse(date);
	}
	
	public static String beforeDate(Date date) throws Exception{
		date.setMonth(date.getMonth()-2);
		return sf.format(date);
	}
	
	public static String convertMinuteString(Date date){
		return sfm.format(date);
	}
	
	public static String convertYMD(Date date){
		return sfymd.format(date);
	}
	
	public static String convertYM(Date date){
		return sfym.format(date);
	}
	
	public static String convertImagePathYM(){
		Date date = new Date();
		return sfimg.format(date);
	}
	
	/**
	 * 获取当前时间十分钟前时间
	 * @return
	 */
	public static Date getBeforeTenMinute(){
		Date date = new Date();
		date.setMinutes(date.getMinutes()-10);
		return date;
	}
	
	/**
	 * 获取当天时间
	 * @param args
	 */
	public static String[] getCurrentDayTime(){
		Date date = new Date();
		String day = sff.format(date);
		String[] results = new String[2];
		results[0] = day + " 00:00:00";
		results[1] = day + " 23:59:59";
		return results;
	}
	
	/**
	 * 获取昨天的时间
	 * @param args
	 */
	public static String[] getYestodayTime(int day){
		Date date = new Date();
		date.setDate(date.getDate()-day);
		String[] results = new String[2];
		results[0] = sff.format(date) + " 00:00:00";
		results[1] = sff.format(date) + " 23:59:59";
		return results;
	}
	
	/**
	 *	获取指定时间年月日 
	 */
	public static String getDateYMD(Date date){
		return sff.format(date);
	}
	
	/**
	 * 获取当前时间24小时前
	 * @param args
	 */
	public static String getBeforeHours(int hours){
		Calendar calendar = Calendar.getInstance();  
        /* HOUR_OF_DAY 指示一天中的小时 */  
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - hours);  
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
        return df.format(calendar.getTime());
	}
	
	/**
	 * 返回指定时间日期格式
	 * @param args
	 */
	public static Date getDateFormat(String date){
		try {
			return sf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 转换指定时间日期格式
	 */
	public static String getDateFormatYMD(String date,int type){
		String result = "";
		try {
			date = date.replaceAll("/","-");
			if (type == 0) {
				result = date +  " 00:00:00";
			} else {
				result = date +  " 23:59:59";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 返回带时区时间
	 * @param args
	 */
	public static String getTimeZone(Date date){
		String tzone = "";
		tzone = sfz.format(date);
		return tzone;
	}
	
	public static void main(String[] args){
		try {
			//System.out.println(beforeDate(new Date()));
			//System.out.println(sf.format(getBeforeTenMinute()));
			/*for (String str : getYestodayTime(3)){
				System.out.println(str);
			}*/
			//System.out.println(DateUtils.getBeforeHours(24));
			//System.out.println(getTimeZone(new Date()));
			//System.out.println(getBeforeHours(24));
			System.out.println(getDateFormatYMD("2016/08/18",1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
