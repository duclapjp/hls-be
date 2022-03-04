package com.cnctor.hls.app.utils;

import java.util.Date;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class Commons {
  public static boolean isNumeric(String strNum) {
    if (strNum == null) {
      return false;
    } else {
      try {
        @SuppressWarnings("unused")
        int val = Integer.parseInt(strNum);

      } catch (NumberFormatException nfe) {
        return false;
      }
      return true;
    }
  }

  public static boolean isEmailValid(String email) {
    if (email == null || email.length() < 6 )
      return false;
    
    String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
    Pattern pattern = Pattern.compile(regex);
    return pattern.matcher(email).matches();
  }
  
  public static boolean statusIsValid(String status) {
    if(StringUtils.isBlank(status))
      return false;
    
    status = StringUtils.trim(status);
    
    if (status.equals(Constants.CONTRACT_STATUS_ACTIVE) 
        || status.equals(Constants.CONTRACT_STATUS_CANCEL)
        || status.equals(Constants.ACCOUNT_STATUS_INACTIVE)) {
      return true;
    }
    
    return false;
  }
  
  
  public static boolean taskStatusIsValid(String status) {
    if(StringUtils.isBlank(status))
      return false;
    
    status = StringUtils.trim(status);
    
    if (status.equals(Constants.TASK_STATUS_CHECKING_STORE) 
        || status.equals(Constants.TASK_STATUS_COMPLETED)
        || status.equals(Constants.TASK_STATUS_CONFIRM_PIC)
        || status.equals(Constants.TASK_STATUS_CONFIRM_STORE)
        || status.equals(Constants.TASK_STATUS_NOTSTARTED)
        || status.equals(Constants.TASK_STATUS_PENDING)
        || status.equals(Constants.TASK_STATUS_PROCESSED)
        || status.equals(Constants.TASK_STATUS_WORKING)
        ) {
      return true;
    }
    
    return false;
  }
  
  public static boolean compareLong(Long long1, Long long2) {
    return (long1 == null ? long2 == null : long1.equals(long2));
  }
  
  public static boolean compareDate(Date date1, Date date2) {
    return (date1 == null ? date2 == null : date1.equals(date2));
  }
  
  public static boolean stopWatchActionIsValid(String action) {
    if(StringUtils.isBlank(action))
      return false;
    
    action = StringUtils.trim(action);
    
    if (action.equals(Constants.STOPWATCH_ACTION_START) 
        || action.equals(Constants.STOPWATCH_ACTION_STOP)) {
      return true;
    }
    return false;
  }
  
  public static boolean taskLogTypeIsValid(String type) {
    if(StringUtils.isBlank(type))
      return false;
    
    type = StringUtils.trim(type);
    
    if (type.equals(Constants.TASKLOG_TYPE_CONF) 
        || type.equals(Constants.TASKLOG_TYPE_EXEC)) {
      return true;
    }
    return false;
  }
  
}
