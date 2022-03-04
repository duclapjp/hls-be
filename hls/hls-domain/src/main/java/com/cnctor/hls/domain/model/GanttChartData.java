package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class GanttChartData implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private long id;
  private String title;
  private String status;
  private Long assigneeId;
  private String assigneeName;
  
  private Date startDate;
  private Date dueDate;
  private Long parent;
  
}
