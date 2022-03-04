package com.cnctor.hls.domain.repository.taskitem;

import org.apache.ibatis.annotations.Param;

public interface TaskItemRepository {
  void upsert(@Param("taskId") long taskId, @Param("itemId") long itemId, @Param("itemJsonValue") String itemJsonValue);
}
