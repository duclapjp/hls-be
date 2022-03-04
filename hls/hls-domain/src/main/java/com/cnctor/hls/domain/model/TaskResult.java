package com.cnctor.hls.domain.model;

import java.util.Date;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TaskResult {
  private Long taskId;
  private long categoryId;
  private String title;
  private String note;
  private String status;
  private String priority;
  private long assigneeId;
  private String assigneeName;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date startDate;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date dueDate;
  private long estTime;
  private long estPoint;
  
  private long actualTime;
  private Long directorId;
  private String directorName;
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date registerDate;
  
  private Long registerPersonId;
  private String registerPersonName;
  private Long storeId;
  private String storeName;
  
  @JsonIgnore
  private Long total;
  
  private Long parentTaskId;
  private boolean visible;
}
