package com.cnctor.hls.domain.repository.usertasksummary;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.UserTaskSummary;

public interface UserTaskSummaryRepository {
  void insert(UserTaskSummary uts);
  void upsert(UserTaskSummary uts);
  void update(UserTaskSummary uts);
  UserTaskSummary findOne(@Param("taskId") long taskId, @Param("accountId") long accountId);
  long countUserTaskSummary(@Param("taskId")long taskId);
  List<UserTaskSummary> searchUserTaskSummary(@Param("taskId")long taskId,
      @Param("criteria") UserTaskSummarySearchCriteria searchCriteria);
  
  void deleteByTaskId(long taskId);
  
  List<Map<String, String>> filter(@Param("criteria") StopwatchSearchCriteria criteria);
  long countStartStopwatch(@Param("criteria") StopwatchSearchCriteria criteria);
}
