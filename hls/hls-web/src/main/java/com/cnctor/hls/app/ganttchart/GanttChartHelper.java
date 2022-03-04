package com.cnctor.hls.app.ganttchart;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;
import com.cnctor.hls.domain.model.GanttChart;
import com.cnctor.hls.domain.model.GanttChartChild;
import com.cnctor.hls.domain.model.GanttChartData;

@Component
public class GanttChartHelper {

  public List<GanttChartData> addChilds(List<GanttChart> data) {
    List<GanttChartData> result = new ArrayList<GanttChartData>();
    for (GanttChart ganttChart : data) {
      result.add(convert(ganttChart));
      List<GanttChartChild> childs = ganttChart.getChilds();
      if (childs != null && childs.size() > 0) {
        for (GanttChartChild child : childs) {
          result.add(convertChild(child));
        }
      }
    }
    return result;
  }

  private GanttChartData convert(GanttChart g) {
    GanttChartData data = new GanttChartData();
    data.setId(g.getTaskId());
    data.setTitle(g.getTitle());
    data.setStatus(g.getStatus());
    data.setAssigneeId(g.getAssigneeId());
    data.setAssigneeName(g.getAssigneeName());
    data.setStartDate(g.getStartDate());
    data.setDueDate(g.getDueDate());
    data.setParent(null);
    return data;
  }
  
  private GanttChartData convertChild(GanttChartChild g) {
    GanttChartData data = new GanttChartData();
    data.setId(g.getTaskId());
    data.setTitle(g.getTitle());
    data.setStatus(g.getStatus());
    data.setAssigneeId(g.getAssigneeId());
    data.setAssigneeName(g.getAssigneeName());
    data.setStartDate(g.getStartDate());
    data.setDueDate(g.getDueDate());
    data.setParent(g.getParentTaskId());
    
    return data;
  }
}
