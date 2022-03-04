package com.cnctor.hls.app.comment;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class CommentForm {
  private long[] notifyToAccIds;
  private String status;
  private Long assigneeId;
  private String commentText;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date startDate;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date dueDate;
  
  private Long estTime;
  private Long estPoint;
  private Long taskId;
  
  private String type;
}
