package com.cnctor.hls.domain.repository.task;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class TaskSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String status;
  private String priority;
  private Long storeId;
  private Long chainId;
  private Long accountId;
  private Long registerPersonId;
  private Long assigneeId;
  private Long directorId;
  private String searchKeyword;
  
  private Date startDate;
  private Date endDate;
  
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
