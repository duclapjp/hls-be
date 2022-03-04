package com.cnctor.hls.app.notification;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.repository.notification.NotificationSearchCriteria;
import com.cnctor.hls.domain.service.notification.NotificationService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class NotificationRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  NotificationService notificationService;

  @Inject
  NotificationHelper notificationHelper;

  @GetMapping("/notifications")
  public @ResponseBody HlsResponse getListNotification(HttpServletRequest request,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "created_date") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    log.info("[DEBUG API getListNotification] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)
        || request.isUserInRole(Constants.ROLE_USER)) {
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account == null || account.getAccountId() == 0)
        return HlsResponse.NOTFOUND();
      
      NotificationSearchCriteria searchCriteria = new NotificationSearchCriteria();
      searchCriteria.setRecipientId(account.getAccountId());
      searchCriteria.setSize(size);
      searchCriteria.setPage(page);
      searchCriteria.setSortBy(sortBy);
      searchCriteria.setSortByType(sortByType);
      
      long total = notificationService.countNotification(searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<Notification> notifications = notificationService.getNotifications(searchCriteria);
        NotificationResultResponse response = new NotificationResultResponse(notifications, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PostMapping("/notification")
  public @ResponseBody HlsResponse createNotification(HttpServletRequest request, @RequestBody NotificationForm notificationForm) {
    
    log.info("[DEBUG create store - {} ]", notificationForm );
    
    Notification notification = beanMapper.map(notificationForm, Notification.class);
    notificationService.createNotification(notification);
    
    return HlsResponse.SUCCESS(notification);
  }

}
