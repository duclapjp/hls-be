package com.cnctor.hls.app.ganttchart;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class GanttChartSearchForm {
  
  private String status;
  private Long assigneeId;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date startDate;
}
