package com.cnctor.hls.app.task;

import java.util.Date;
import java.util.List;
import com.cnctor.hls.app.comment.CommentForm;
import com.cnctor.hls.app.plan.ItemForm;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TaskForm {
  private Long categoryId;
  private String title;
  private String attachName;
  private String note;
  private String status;
  private String priority;
  
  private Long assigneeId;
  private Long directorId;
  private Long registerPersonId;

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date startDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date dueDate;
  private Long estTime;
  private Long estPoint;
  private boolean visible;
  private Long storeId;
  
  private Long[] storeIds;
  private Long[] taskAttachIds;
  
  private CommentForm commentForm;

  private Long planId;
  private List<ItemForm> items;
}
