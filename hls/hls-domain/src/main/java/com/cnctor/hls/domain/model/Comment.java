package com.cnctor.hls.domain.model;

import java.util.Date;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class Comment {
  
  private long commentId;
  private long taskId;
  // accoutnId1, accountId2 .. .
  private String status;
  private Long assigneeId;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date startDate;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date dueDate;
  
  private String commentText;
  private Long estTime;
  private Long estPoint;
  
  private long creatorId;
  private String creatorName;
  private String type;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd HH:mm:ss", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date createdDate;
  
  private String notifyTo;
}
