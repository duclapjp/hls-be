package com.cnctor.hls.domain.repository.ganttchart;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.GanttChart;

public interface GanttChartRepository {

  List<GanttChart> getDataForGanttChart(@Param("criteria") GanttChartSearchCriteria criteria);
 
}
