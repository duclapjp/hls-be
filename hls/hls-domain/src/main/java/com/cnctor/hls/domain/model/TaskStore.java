package com.cnctor.hls.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskStore {
  private long taskStoreId;
  private long taskId;
  private long storeId;

  public TaskStore(long taskId, long storeId) {
    this.taskId = taskId;
    this.storeId = storeId;
  }
}
