package com.cnctor.hls.domain.model;

import java.io.Serializable;
import lombok.Data;

@Data
public class TaskItem implements Serializable {
  private static final long serialVersionUID = 1L;
  
  private long taskId;
  private long itemId;
  private String itemJsonValue;
  
  // extra fields
  private String itemCode;
}
