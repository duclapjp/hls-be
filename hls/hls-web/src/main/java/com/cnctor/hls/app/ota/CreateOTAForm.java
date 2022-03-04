package com.cnctor.hls.app.ota;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateOTAForm {
  
  private String name;
  private Long otaTypeId;
  private String loginUrlFixed1;
  private String loginUrlFixed2;
  private Long storeId;
  private String loginId;
  private String password;
  private long passwordUpdateDeadline;
  private String note;
  private boolean isDisplayStoreId;
}
