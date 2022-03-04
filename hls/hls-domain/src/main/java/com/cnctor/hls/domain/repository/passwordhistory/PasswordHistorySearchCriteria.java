package com.cnctor.hls.domain.repository.passwordhistory;

import java.io.Serializable;
import lombok.Data;

@Data
public class PasswordHistorySearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private long otaId;
  private long accountId;
  private long storeId;

  
  private int size;
  private int page;

  public int getSize() {
    if (size <= 0 || size >= 20)
      return 10;
    return size;
  }
  public int getPage() {
    if (page <= 0 )
      return 1;
    return page;
  }
  
}
