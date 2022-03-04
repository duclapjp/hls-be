package com.cnctor.hls.domain.service.notification;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.repository.notification.NotificationSearchCriteria;
import com.cnctor.hls.domain.repository.notification.NotificationRepository;

@Service
public class NotificationServiceImpl implements NotificationService {

  @Inject
  NotificationRepository notificationRepository;
  
  @Override
  public List<Notification> getNotifications(NotificationSearchCriteria searchCriteria) {
    return notificationRepository.getNotificatons(searchCriteria);
  }
  
  @Override
  public long countNotification(NotificationSearchCriteria searchCriteria) {
    return notificationRepository.countNotificatons(searchCriteria);

  }
  
  @Override
  public Notification createNotification(Notification notification) {
    notificationRepository.insert(notification);
    return notification;
  }

}
