package com.cnctor.hls.domain.model;

import java.util.Date;
import lombok.Data;

@Data
public class Notification {
  private long notificationId;
  private Date createdDate;
  private String title;  
  private Long creatorId;
  private String creatorName;
  private Long recipientId;
  private String recipientName;
  private Long actionId;
  private String actionValue;
}
