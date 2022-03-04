package com.cnctor.hls.app.task;

import java.util.List;
import com.cnctor.hls.domain.model.TaskResult;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResultResponse {
  private List<TaskResult> tasks;
  private long total;
}
