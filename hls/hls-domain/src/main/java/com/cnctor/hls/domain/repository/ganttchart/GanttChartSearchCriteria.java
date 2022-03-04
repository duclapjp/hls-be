package com.cnctor.hls.domain.repository.ganttchart;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GanttChartSearchCriteria implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private String status;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date startDate;
  private Long assigneeId;
  
  private String sortBy;
  private long sortByType;

}
