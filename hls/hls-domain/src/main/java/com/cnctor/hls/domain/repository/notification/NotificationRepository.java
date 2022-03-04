package com.cnctor.hls.domain.repository.notification;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Notification;

public interface NotificationRepository {
  void insert(Notification notification);

  List<Notification> getNotificatons(@Param("criteria") NotificationSearchCriteria criteria);

  long countNotificatons(@Param("criteria") NotificationSearchCriteria criteria);
}
