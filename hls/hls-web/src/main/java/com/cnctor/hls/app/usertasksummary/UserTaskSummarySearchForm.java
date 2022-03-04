package com.cnctor.hls.app.usertasksummary;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserTaskSummarySearchForm {
  private String sortBy;
  private long sortByType;
  private long page;
  private long size;
}
