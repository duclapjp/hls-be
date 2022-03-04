package com.cnctor.hls.domain.common.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH時mm分ss秒");

  public static String formatDate(Date dt) {
    return sdf.format(dt);
  }
  
  /**
   * @param dateTime
   * @return end of day at : 23:59:59
   */
  public static Date getEndTimeOfDay(Date dateTime) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateTime);
    
    // set time to end of day : 23:59:59
    cal.set(Calendar.HOUR_OF_DAY, 23);
    cal.set(Calendar.MINUTE, 59);
    cal.set(Calendar.SECOND, 59);
    
    return cal.getTime();
  }
  
  /**
   * @param dateTime
   * @return begin time of new day at : 00:00:01
   */
  public static Date getBeginTimeOfNewDay(Date dateTime) {
    
    Calendar cal = Calendar.getInstance();
    cal.setTime(dateTime);
    
    // set time to end of day : 23:59:59
    cal.add(Calendar.DATE, 1);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 1);
    
    return cal.getTime();
  }
  
  public static Date currentDate() {
    Calendar cal = Calendar.getInstance();
    return cal.getTime();
  }
}