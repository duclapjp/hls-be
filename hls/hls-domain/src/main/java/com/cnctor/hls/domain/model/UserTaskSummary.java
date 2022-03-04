package com.cnctor.hls.domain.model;

import java.util.Date;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class UserTaskSummary {
  
  private long accountId;
  private long taskId;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date createdDate;
  private long summaryTime;
  private String latestAction;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date latestActionTime;
  private String displayName;
 
}
