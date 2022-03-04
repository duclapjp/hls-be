package com.cnctor.hls.domain.service.dashboard;

import java.util.List;
import java.util.Map;
import com.cnctor.hls.domain.model.PendingTask;
import com.cnctor.hls.domain.repository.dashboard.PendingTaskSearchCriteria;
import com.cnctor.hls.domain.repository.usertasksummary.StopwatchSearchCriteria;

public interface DashboardService {
  List<PendingTask> getPendingTasks(PendingTaskSearchCriteria criteria);
  long countPendingTask(PendingTaskSearchCriteria criteria);
  long countStartStopwatch(StopwatchSearchCriteria criteria);
  List<Map<String, String>> filterStartStopwatch(StopwatchSearchCriteria criteria);
  List<PendingTask> findChildPendingTasks(long taskId, PendingTaskSearchCriteria criteria);
}
