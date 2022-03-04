package com.cnctor.hls.app.notification;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.domain.common.mail.Mail;
import com.cnctor.hls.domain.common.mail.MailService;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.notification.NotificationService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.task.TaskService;
import lombok.extern.slf4j.Slf4j;

@EnableAsync
@Component
@Slf4j
public class NotificationHelper {

  @Value("${mail.template.hlsmailfrom}")
  private String hlsMailFrom;

  @Value("${mail.template.baseurl}")
  private String hlsBaseUrl;

  @Value("${mail.template.notification.mention.subject}")
  private String mentionMailSubject;

  @Value("${mail.template.notification.mention.name}")
  private String mentionMailTemplate;


  @Value("${mail.template.notification.changedpass.subject}")
  private String changedPassMailSubject;

  @Value("${mail.template.notification.changedpass.name}")
  private String changedPassMailTemplate;


  @Value("${mail.template.notification.taskstatus.subject}")
  private String taskStatusMailSubject;

  @Value("${mail.template.notification.taskstatus.name}")
  private String taskStatusMailTemplate;

  @Inject
  Mapper beanMapper;

  @Inject
  NotificationService notificationService;

  @Inject
  AccountService accountService;

  @Inject
  MailService mailService;

  @Inject
  StoreService storeService;

  @Inject
  TaskService taskService;

  public Notification insert(Long creatorId, Long recipientId, Long actionId, String title) {
    Notification notification = new Notification();
    notification.setCreatorId(creatorId);
    notification.setRecipientId(recipientId);
    notification.setActionId(actionId);
    notification.setTitle(title);
    notificationService.createNotification(notification);
    return notification;
  }


  /*
   * send async notification to another system
   */
  @Async
  public void asyncSendNotification(Notification notif) {

    addNotification(notif);
    
    try {
      mailNotification(notif);
    } catch (Exception e) {
      log.error("Send mail notification has error : {}", e.getMessage());
      e.printStackTrace();
    }
  }

  @Async
  public void addNotification(Notification notification) {
    log.info("Execute addNotification asynchronously. " + Thread.currentThread().getName());
    notificationService.createNotification(notification);
  }

  @Async
  public void mailNotification(Notification notification)
      throws Exception {
    
    log.info("Execute mailNotification asynchronously. " + Thread.currentThread().getName());
    // get email from user
    Account account = accountService.findById(notification.getRecipientId());

    // get mail type
    Mail mail = new Mail();
    mail.setFrom(hlsMailFrom);
    
    String userEmail = account.getMailSetting();
    if(StringUtils.isBlank(userEmail)) {
      userEmail = account.getMail();
    }
    
    mail.setTo(userEmail);

    Map<String, Object> m = new HashMap<>();

    if (Constants.NOTIFICATION_TYPE_CHANGE_PASS.equals(notification.getTitle())) {

      mail.setSubject(changedPassMailSubject);
      mail.setTemplate(changedPassMailTemplate);

      long storeId = notification.getActionId();
      Store store = storeService.findStore(storeId);

      m.put("storeName", store.getName());
      m.put("storeDetailUrl", hlsBaseUrl + "dashboard/stores/edit/" + storeId);

    } else if (Constants.NOTIFICATION_TYPE_CHANGE_TASK_STATUS.equals(notification.getTitle())) {

      mail.setSubject(taskStatusMailSubject);
      mail.setTemplate(taskStatusMailTemplate);

      long taskId = notification.getActionId();
      Task task = taskService.findTask(taskId);

      m.put("taskName", task.getTitle());
      m.put("taskDetailUrl", hlsBaseUrl + "dashboard/tasks/edit/" + taskId);

    } else if (Constants.NOTIFICATION_TYPE_MENTION.equals(notification.getTitle())) {

      mail.setSubject(mentionMailSubject);
      mail.setTemplate(mentionMailTemplate);

      long taskId = notification.getActionId();

      m.put("taskDetailUrl", hlsBaseUrl + "dashboard/tasks/edit/" + taskId);
    }

    // set variable
    mail.setModel(m);

    // send mail
    mailService.sendMail(mail);

  }
}
