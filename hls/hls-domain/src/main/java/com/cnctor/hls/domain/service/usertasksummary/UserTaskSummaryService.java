package com.cnctor.hls.domain.service.usertasksummary;

import java.util.List;
import com.cnctor.hls.domain.model.UserTaskSummary;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummarySearchCriteria;

public interface UserTaskSummaryService {
  UserTaskSummary findUserTaskSummary(long taskId, long accountId);
  UserTaskSummary upsert(UserTaskSummary uts);
  long countUserTaskSummary(long taskId, UserTaskSummarySearchCriteria searchCriteria);
  List<UserTaskSummary> searchUserTaskSummary(long taskId,
      UserTaskSummarySearchCriteria searchCriteria);
}
