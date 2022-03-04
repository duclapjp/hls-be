package com.cnctor.hls.domain.service.usertasksummary;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.UserTaskSummary;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummaryRepository;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummarySearchCriteria;

@Service
public class UserTaskSummaryServiceImpl implements UserTaskSummaryService {
  
  @Inject
  UserTaskSummaryRepository repository;
  
  @Override
  public UserTaskSummary findUserTaskSummary(long taskId, long accountId) {
    return repository.findOne(taskId, accountId);
  }

  @Override
  public UserTaskSummary upsert(UserTaskSummary uts) {
    repository.upsert(uts);
    return uts;
  }

  @Override
  public long countUserTaskSummary(long taskId, UserTaskSummarySearchCriteria searchCriteria) {
    return repository.countUserTaskSummary(taskId);
  }

  @Override
  public List<UserTaskSummary> searchUserTaskSummary(long taskId,
      UserTaskSummarySearchCriteria searchCriteria) {
    return repository.searchUserTaskSummary(taskId, searchCriteria);
  }
}
