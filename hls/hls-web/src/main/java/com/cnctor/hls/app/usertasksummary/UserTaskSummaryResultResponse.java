package com.cnctor.hls.app.usertasksummary;

import java.util.List;
import com.cnctor.hls.domain.model.UserTaskSummary;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserTaskSummaryResultResponse {
  private List<UserTaskSummary> userTaskSummaries;
  private long total;
}
