package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class GanttChart implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private long taskId;
  private String title;
  private String status;
  private Long assigneeId;
  private String assigneeName;
  
  private Date startDate;
  private Date dueDate;
  
  private List<GanttChartChild> childs;
  
}
