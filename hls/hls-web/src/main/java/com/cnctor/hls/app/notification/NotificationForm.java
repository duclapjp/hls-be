package com.cnctor.hls.app.notification;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NotificationForm {
  
  private String title;
  private Long creatorId;
  private Long recipientId;
  private String content;

  
  private long page;
  private long size;
}
