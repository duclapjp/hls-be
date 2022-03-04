package com.cnctor.hls.app.task;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TaskSearchForm {
  
  private String status;
  private String priority;
  private Long storeId;
  private Long accountId;
  private Long registerPersonId;
  private Long assigneeId;
  private Long directorId;
  private String searchKeyword;
  private long page;
  private long size;
}
