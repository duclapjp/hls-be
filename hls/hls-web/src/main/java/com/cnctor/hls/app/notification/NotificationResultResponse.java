package com.cnctor.hls.app.notification;

import java.util.List;
import com.cnctor.hls.domain.model.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationResultResponse {
  private List<Notification> notifications;
  private long total;
}
