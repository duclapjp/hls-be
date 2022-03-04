package com.cnctor.hls.domain.model;

import java.util.Date;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserToken {
  private long userTokenId;
  private String token;
  private Date createdDate;
  private Date expiredDate;
  private String action;
  private long userId;
  private boolean isActive;
  
  private String actionValue;
  
  public static final String RESET_PASS = "RESET_PASS";
  public static final String CHANGE_MAIL = "CHANGE_MAIL";
}
