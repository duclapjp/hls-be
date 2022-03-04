package com.cnctor.hls.app.store;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreSearchForm {
  
  private String contractStatus;
  private Long directorId;
  private Long chainId;
  private Long storeId;
  private Long Id;
  private String searchKeyword;
  
  private long page;
  private long size;
  private String filter;
}
