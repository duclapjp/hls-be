package com.cnctor.hls.domain.repository.dashboard;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.PendingTask;

public interface DashboardRepository {
  long countPendingTask(@Param("criteria") PendingTaskSearchCriteria criteria);
  List<PendingTask> getPendingTasks(@Param("criteria") PendingTaskSearchCriteria criteria);
  List<PendingTask> findChildPendingTasks(@Param("taskId") long taskId, @Param("criteria") PendingTaskSearchCriteria criteria);

}
