package com.cnctor.hls.domain.service.taskitem;

public interface TaskItemService {
  void upsert(long taskId, long itemId, String itemJsonValue);
}
