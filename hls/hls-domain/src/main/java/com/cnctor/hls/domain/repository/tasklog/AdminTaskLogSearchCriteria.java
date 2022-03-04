package com.cnctor.hls.domain.repository.tasklog;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class AdminTaskLogSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  
  private Date startDate;
  private Date endDate;
  private String username;
  
  private String sortBy;
  private int sortByType;
  
}
