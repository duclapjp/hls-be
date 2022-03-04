package com.cnctor.hls.app.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PendingTaskResultResponse {
  private Object rows;
  private long total;
}
