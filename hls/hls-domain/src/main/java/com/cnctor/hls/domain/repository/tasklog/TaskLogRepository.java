package com.cnctor.hls.domain.repository.tasklog;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.AdminTaskLog;
import com.cnctor.hls.domain.model.TaskLog;

public interface TaskLogRepository {
  void insert(TaskLog taskLog);
  void update(TaskLog taskLog);
  TaskLog findOne(long taskLogId);
  long countTaskLog(@Param("taskId") long taskId);
  List<TaskLog> searchTaskLog(@Param("taskId") long taskId, @Param("criteria") TaskLogSearchCriteria searchCriteria);
  void batchUpdate(@Param("taskLogs") List<TaskLog> taskLogs);
  List<TaskLog> fetchTaskLogAfter(TaskLog taskLog);
  TaskLog getLatestByAccount(@Param("taskId") long taskId, @Param("accountId") long accountId);
  List<TaskLog> getBeforeByAccount(TaskLog taskLog);
  void deleteByTaskId(long taskId);
  Map<String, Long> sumTaskLogTime(@Param("taskIds") List<Long> taskIds);
  List<AdminTaskLog> getAdminTaskLogReport(@Param("criteria") AdminTaskLogSearchCriteria searchCriteria);
  void updateStopTaskLog(long taskLogId);
  
  List<TaskLog> findTaskNotStopYet();
}
