package com.cnctor.hls.app.ganttchart;

import java.util.List;
import com.cnctor.hls.domain.model.GanttChartData;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GanttChartResultResponse {
  private List<GanttChartData> data;
  private long total;
}
