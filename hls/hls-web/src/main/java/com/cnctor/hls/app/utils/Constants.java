package com.cnctor.hls.app.utils;

public class Constants {
  public static String ROLE_ADMIN = "ROLE_ADMIN";
  public static String ROLE_SUBADMIN = "ROLE_SUBADMIN";
  public static String ROLE_CHAIN = "ROLE_CHAIN";
  public static String ROLE_STORE = "ROLE_STORE";
  public static String ROLE_USER = "ROLE_USER";

  public static String ACCOUNT_STATUS_ACTIVE = "利用中";
  public static String ACCOUNT_STATUS_INACTIVE = "停止";

  public static String CONTRACT_STATUS_ACTIVE = "契約中";
  public static String CONTRACT_STATUS_INACTIVE = "休止中";
  public static String CONTRACT_STATUS_CANCEL = "解約";

  public static String ACCOUNT_ROLE_EDIT_FORBIDDEN = "You have not permission";

  public static String TASK_STATUS_NOTSTARTED = "未着手";
  public static String TASK_STATUS_WORKING = "作業中";
  public static String TASK_STATUS_CONFIRM_PIC = "担当者確認中";
  public static String TASK_STATUS_CONFIRM_STORE = "施設確認中";
  public static String TASK_STATUS_PENDING = "保留";
  public static String TASK_STATUS_PROCESSED = "処理済";
  public static String TASK_STATUS_CHECKING_STORE = "施設チェック中";
  public static String TASK_STATUS_COMPLETED = "完了";
  public static String TASK_STATUS_NOT_COMPLETED ="完了以外";

  public static String[] TASK_STATUS_LIST = {TASK_STATUS_NOTSTARTED, TASK_STATUS_CONFIRM_PIC,
      TASK_STATUS_WORKING, TASK_STATUS_CONFIRM_STORE, TASK_STATUS_PENDING, 
      TASK_STATUS_PROCESSED, TASK_STATUS_CHECKING_STORE, TASK_STATUS_COMPLETED};

  public static String TASK_PRIORITY_HIGH = "高";
  public static String TASK_PRIORITY_MEDIUM = "中";
  public static String TASK_PRIORITY_LOW = "低";
  public static String[] TASK_PRIORITY_LIST = {TASK_PRIORITY_HIGH, TASK_PRIORITY_MEDIUM, TASK_PRIORITY_LOW};

  public static String STOPWATCH_ACTION_START = "START";
  public static String STOPWATCH_ACTION_STOP = "STOP";
  
  public static String COMMENT_TYPE_USER = "USER";
  public static String COMMENT_TYPE_STORE = "STORE";
  public static String[] COMMENT_TYPE_LIST = {COMMENT_TYPE_USER, COMMENT_TYPE_STORE};
  
  public static String TASKLOG_TYPE_EXEC = "EXECUTE";
  public static String TASKLOG_TYPE_CONF = "CONFIRM";
  
  public static String NOTIFICATION_TYPE_MENTION = "メンションが自分がお知らせ";
  public static String NOTIFICATION_TYPE_CHANGE_PASS = "パスワード変更しました";
  public static String NOTIFICATION_TYPE_CHANGE_TASK_STATUS = "タスクのステータス変更";
  
  public static String OTA_STATUS_ENABLED = "ENABLED";
  public static String OTA_STATUS_DISABLED = "DISABLED";
  public static String[] OTA_STATUS_LIST = {OTA_STATUS_ENABLED, OTA_STATUS_DISABLED};
  
  public static String PLAN_STATUS_ENABLED = "ENABLED";
  public static String PLAN_STATUS_DISABLED = "DISABLED";
  public static String[] PLAN_STATUS_LIST = {PLAN_STATUS_ENABLED, PLAN_STATUS_DISABLED};
  
  public static String CHAIN_STORE_PLAN = "CHAIN_STORE";
}
