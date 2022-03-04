package com.cnctor.hls.domain.service.ganttchart;

import java.util.List;
import javax.inject.Inject;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.GanttChart;
import com.cnctor.hls.domain.repository.ganttchart.GanttChartRepository;
import com.cnctor.hls.domain.repository.ganttchart.GanttChartSearchCriteria;

@Service
public class GanttChartServiceImpl implements GanttChartService {

  @Inject
  GanttChartRepository ganttChartRepository;
  
  @Inject
  Mapper beanMapper;

  @Override
  public List<GanttChart> getDataForGanttChart(GanttChartSearchCriteria criteria) {
    return ganttChartRepository.getDataForGanttChart(criteria);
  }

 
}
