package com.cnctor.hls.domain.repository.store;

import java.io.Serializable;
import lombok.Data;

@Data
public class StoreSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String contractStatus;
  private Long directorId;
  private Long chainId;
  private boolean isChainRole;
  private Long storeId;

  private String searchKeyword;
  
  private int size;
  private int page;

  public int getSize() {
    if (size < 0 || size >= 20)
      return 10;
    return size;
  }
  public int getPage() {
    if (page <= 0 )
      return 1;
    return page;
  }
}
