package com.cnctor.hls.app.task;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TaskLogSearchForm {
  private String sortBy;
  private long sortByType;
  private long page;
  private long size;
}
