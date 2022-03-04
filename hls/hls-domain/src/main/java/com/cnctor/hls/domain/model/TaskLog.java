package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

@Data
public class TaskLog implements Serializable{

private static final long serialVersionUID = 1L;
  
  private long taskLogId;
  private long taskId;
  private long accountId;
  private String displayName;
  private String action;
  private Long executeTime;
  private Long accumulationTime;
  private boolean stopped;
  
  //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date taskLogDate;
  
  private Long confirmTime;
  private String type;
}
