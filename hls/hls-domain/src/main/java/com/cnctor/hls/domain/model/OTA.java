package com.cnctor.hls.domain.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class OTA implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private long otaId;
  private String name;
  
  private long otaTypeId;
  private String loginUrlFixed1;
  private String loginUrlFixed2;
  
  // temporary disabled fields 
  private Long storeId;
  private String loginId;
  private String password;
  
  private boolean isDisplayStoreId;
  private Long passwordUpdateDeadline;
  private String note;
  private String status;
 
  // extra fields
  private String otaTypeName;
}
