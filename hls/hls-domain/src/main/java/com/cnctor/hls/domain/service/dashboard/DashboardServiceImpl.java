package com.cnctor.hls.domain.service.dashboard;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.PendingTask;
import com.cnctor.hls.domain.repository.dashboard.DashboardRepository;
import com.cnctor.hls.domain.repository.dashboard.PendingTaskSearchCriteria;
import com.cnctor.hls.domain.repository.usertasksummary.StopwatchSearchCriteria;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummaryRepository;

@Service
public class DashboardServiceImpl implements DashboardService {

  @Inject
  DashboardRepository dashboardRepository;
  
  @Inject
  Mapper beanMapper;

  @Inject
  UserTaskSummaryRepository utsRepository;
  
  @Override
  public List<PendingTask> getPendingTasks(PendingTaskSearchCriteria criteria) {
    return dashboardRepository.getPendingTasks(criteria);
  }

  @Override
  public long countPendingTask(PendingTaskSearchCriteria criteria) {
    return dashboardRepository.countPendingTask(criteria);
  }

  @Override
  public List<Map<String, String>> filterStartStopwatch(StopwatchSearchCriteria criteria) {
    return utsRepository.filter(criteria);
  }

  @Override
  public long countStartStopwatch(StopwatchSearchCriteria criteria) {
    return utsRepository.countStartStopwatch(criteria);
  }

  @Override
  public List<PendingTask> findChildPendingTasks(long taskId, PendingTaskSearchCriteria criteria) {
    return dashboardRepository.findChildPendingTasks(taskId, criteria);
  }
}
