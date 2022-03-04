package com.cnctor.hls.domain.repository.dashboard;

import java.io.Serializable;
import lombok.Data;

@Data
public class PendingTaskSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String status;
  private String priority;
  private Long storeId;
  private Long chainId;

  private Long assigneeId;
  private Long directorId;

  private long userRole;
  
  private String sortBy;
  private long sortByType;
  
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
