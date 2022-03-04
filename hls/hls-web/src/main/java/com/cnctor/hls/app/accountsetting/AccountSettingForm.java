package com.cnctor.hls.app.accountsetting;

import lombok.Data;

@Data
public class AccountSettingForm {
  private String mail;
  private String phone;
  private String password;
  private String notiDest;
  private String status;
  private String displayName;
  private String mailSetting;
  private String slackSetting;
  private String chatworkSetting;
  private String lineSetting; 
  private String viberRakutenSetting;
}
