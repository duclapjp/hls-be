package com.cnctor.hls.domain.model;

import java.io.Serializable;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(value = {"password"})
public class Account implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private long accountId;
  private String role;
  private String status;
  private Long chainId;
  private Long storeId;
  private String mail;
  private String phone;
  private String note;
  private String username;
  private String password;
  private String displayName;
  private String notiDest;
  private String mailSetting;
  private String slackSetting;
  private String chatworkSetting;
  private String lineSetting;
  private String viberRakutenSetting;
  private String chainName;
  private String storeName;
  private boolean isFirstLogin;
  
  // 4 role : ROLE_ADMIN, ROLE_SUBADMIN, ROLE_CHAIN, ROLE_STORE, ROLE_USER
  public boolean isAdmin() {
    return StringUtils.equalsIgnoreCase(role, "ROLE_ADMIN");
  }
  
  public boolean isSubAdmin() {
    return StringUtils.equalsIgnoreCase(role, "ROLE_SUBADMIN");
  }
  
  public boolean isChain() {
    return StringUtils.equalsIgnoreCase(role, "ROLE_CHAIN");
  }
  
  public boolean isStore() {
    return StringUtils.equalsIgnoreCase(role, "ROLE_STORE");
  }
  
  public boolean isUser() {
    return StringUtils.equalsIgnoreCase(role, "ROLE_USER");
  }
}
