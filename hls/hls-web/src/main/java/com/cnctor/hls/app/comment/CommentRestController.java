package com.cnctor.hls.app.comment;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.account.AccountHelper;
import com.cnctor.hls.app.notification.NotificationHelper;
import com.cnctor.hls.app.task.TaskHelper;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Comment;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.repository.comment.CommentSearchCriteria;
import com.cnctor.hls.domain.service.comment.CommentService;
import com.cnctor.hls.domain.service.task.TaskService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class CommentRestController {
  
  @Inject
  Mapper beanMapper;
  
  @Inject
  CommentService commentService;
  
  @Inject
  TaskService taskService;
  
  @Inject
  TaskHelper taskHelper;
  
  @Inject
  AccountHelper accountHelper;
  
  @Inject
  NotificationHelper notificationHelper;
  
  @PostMapping("/comment")
  public @ResponseBody HlsResponse createComment(HttpServletRequest request, @RequestBody CommentForm commentForm) {
    
    long taskId = commentForm.getTaskId();
    // permission to add comment
    if(taskHelper.hasViewTaskRole(request, taskId)) {
      log.info("[DEBUG createComment] - {}", commentForm);
      
      Task task = taskService.findTask(taskId);
      
      // task not found
      if(task == null) {
        return HlsResponse.NOTFOUND();
      }
      
      String errMsg = taskHelper.doValidateComment(commentForm);
      if(StringUtils.isNotBlank(errMsg)) {
        return HlsResponse.BADREQUEST(null, errMsg);
      }
      
      // create comment
      Comment comment = beanMapper.map(commentForm, Comment.class);
      comment = taskHelper.setCommentCreatorInfo(comment, request, commentForm.getNotifyToAccIds());
      
      taskHelper.createComment(comment, task, accountHelper.getRole(request), commentForm.getNotifyToAccIds(), request);
      
      // push notification for mention user
      for (long recipientId :  commentForm.getNotifyToAccIds()) {
        Notification notif = new Notification();
        notif.setCreatorId(comment.getCreatorId());
        notif.setRecipientId(recipientId);
        notif.setActionId(task.getTaskId());
        notif.setTitle(Constants.NOTIFICATION_TYPE_MENTION);
        
        notificationHelper.asyncSendNotification(notif);
      }
      
      return HlsResponse.SUCCESS(comment);
      
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  /**
   * Get List Comment By TaskId with Role
   * @param request
   * @param taskId
   * @param commentRequest
   * @return
   */
  @PostMapping("/tasks/{id}/comments")
  public @ResponseBody HlsResponse getComments(HttpServletRequest request,
      @PathVariable("id") long taskId, @RequestBody CommentSearchForm commentRequest) {
    if(taskHelper.hasViewTaskRole(request, taskId)) {
      Task task = taskService.findTask(taskId);
      if(task == null) {
        return HlsResponse.NOTFOUND();
      }
      
      CommentSearchCriteria searchCriteria = beanMapper.map(commentRequest, CommentSearchCriteria.class);

      if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
          || request.isUserInRole(Constants.ROLE_USER)) {
      } else if (request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)) {
        searchCriteria.setType(Constants.COMMENT_TYPE_STORE);
      } else {
        return HlsResponse.FORBIDDEN();
      } 
        
      long total = commentService.countComment(taskId, searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<Comment> comments = commentService.searchComment(taskId, searchCriteria);
        CommentResultResponse response = new CommentResultResponse(comments, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
