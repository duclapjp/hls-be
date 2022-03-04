package com.cnctor.hls.domain.service.tasklog;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.AdminTaskLog;
import com.cnctor.hls.domain.model.TaskLog;
import com.cnctor.hls.domain.repository.tasklog.AdminTaskLogSearchCriteria;
import com.cnctor.hls.domain.repository.tasklog.TaskLogSearchCriteria;

public interface TaskLogService {
  TaskLog createTaskLog(TaskLog taskLog);
  long countTaskLog(long taskId);
  List<TaskLog> searchTaskLog(long taskId, TaskLogSearchCriteria searchCriteria);
  TaskLog findTaskLog(long taskLogId);
  void updateTaskLog(TaskLog taskLog, Date editedTime) throws Exception;
  TaskLog getLatestByAccount(@Param("taskId") long taskId, @Param("accountId") long accountId);
  Map<String, Long> sumTaskLogTime(List<Long> taskIds);
  List<AdminTaskLog> getAdminTaskLogReport(AdminTaskLogSearchCriteria searchCriteria);
  void updateStopTaskLog(long taskLogId);
  List<TaskLog> findTaskNotStopYet();
}
