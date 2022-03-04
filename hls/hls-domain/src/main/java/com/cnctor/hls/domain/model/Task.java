package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task implements Serializable {
  
  private static final long serialVersionUID = 1L;
  private long taskId;
  private long categoryId;
  private String title;
  private String note;
  private String status;
  private String priority;
  private Long assigneeId;
  private Long directorId;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date startDate;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date dueDate;
  
  private Long estTime;
  private Long estPoint;
  
  private Long actualTime;
  private Long registerPersonId;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date registerDate;
  
  private Long storeId;
  private Long parentTaskId;
  private boolean visible;
  
  private List<Comment> comments;
  private List<TaskAttach> attachs;
  
  private List<TaskResult> childs;
  
  private Long planId;
  private List<TaskItem> items;
}
