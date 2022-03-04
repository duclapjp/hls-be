package com.cnctor.hls.app.chain;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ChainForm {
  
  private String contractStatus;
  private String name;
  private Long directorId1;
  private Long directorId2;
  private Long directorId3;
  private String managerMail;
  private String managerName;
  private String managerPhone;
  private String note;
  private long[] storeIds;
  private long[] delStoreIds;
  private long[] addStoreIds;
  private Long chainId;
  private String searchKeyword;
  private long page;
  private long size;
}
