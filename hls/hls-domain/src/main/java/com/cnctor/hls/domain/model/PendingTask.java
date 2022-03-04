package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class PendingTask implements Serializable {
  private static final long serialVersionUID = 1L;
  private long taskId;
  private String title;
  private String status;
  
  private Long assigneeId;
  private String assigneeName;
  
  private Date dueDate;
    
  private Long storeId;
  private String storeName;
  private Long dayRemain;

}
