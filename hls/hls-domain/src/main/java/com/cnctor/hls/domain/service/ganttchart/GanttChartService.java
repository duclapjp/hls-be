package com.cnctor.hls.domain.service.ganttchart;

import java.util.List;
import com.cnctor.hls.domain.model.GanttChart;
import com.cnctor.hls.domain.repository.ganttchart.GanttChartSearchCriteria;

public interface GanttChartService {

  List<GanttChart> getDataForGanttChart(GanttChartSearchCriteria criteria);
  
}
