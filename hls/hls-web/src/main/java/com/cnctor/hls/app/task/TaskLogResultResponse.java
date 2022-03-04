package com.cnctor.hls.app.task;

import java.util.List;
import com.cnctor.hls.domain.model.TaskLog;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskLogResultResponse {
  private List<TaskLog> taskLogs;
  private long total;
}
