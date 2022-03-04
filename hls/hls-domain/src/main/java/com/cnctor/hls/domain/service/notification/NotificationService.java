package com.cnctor.hls.domain.service.notification;

import java.util.List;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.repository.notification.NotificationSearchCriteria;

public interface NotificationService {
  List<Notification> getNotifications(NotificationSearchCriteria notifiationSearchCriteria);
  long countNotification(NotificationSearchCriteria searchCriteria);
  Notification createNotification(Notification notification);

}
