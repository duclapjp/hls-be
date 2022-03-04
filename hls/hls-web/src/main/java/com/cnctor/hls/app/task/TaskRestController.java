package com.cnctor.hls.app.task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.dozer.Mapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cnctor.hls.app.account.AccountDisplayResultResponse;
import com.cnctor.hls.app.account.AccountHelper;
import com.cnctor.hls.app.comment.CommentForm;
import com.cnctor.hls.app.notification.NotificationHelper;
import com.cnctor.hls.app.utils.Commons;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AccountDisplay;
import com.cnctor.hls.domain.model.Comment;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.model.TaskAttach;
import com.cnctor.hls.domain.model.TaskLog;
import com.cnctor.hls.domain.model.TaskResult;
import com.cnctor.hls.domain.repository.task.TaskSearchCriteria;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.task.TaskService;
import com.cnctor.hls.domain.service.taskattach.TaskAttachService;
import com.cnctor.hls.domain.service.tasklog.TaskLogService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.usertasksummary.UserTaskSummaryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TaskRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  TaskService taskService;
  
  @Inject
  AccountHelper accountHelper;

  @Inject
  TaskHelper taskHelper;

  @Inject
  TaskAttachService taskAttachService;

  @Inject
  FileResponseHelper fileHelper;
  
  @Inject
  UserTaskSummaryService utsService;
  
  @Inject
  TaskLogService taskLogService;
  
  @Inject
  StoreService storeService;
  
  @Inject
  AccountService accountService;
  
  @Inject
  NotificationHelper ntfHelper;
  
  
  @GetMapping("/tasks")
  public @ResponseBody HlsResponse getTasks(HttpServletRequest request) {
    log.info("[DEBUG API GetListTask] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER)) {

      List<TaskResult> tasks = taskService.getTasks();
      int total = tasks == null ? 0 : tasks.size();
      TaskResultResponse response = new TaskResultResponse(tasks, total);
      return HlsResponse.SUCCESS(response);

    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PostMapping("/tasks/search")
  public @ResponseBody HlsResponse searchTasks(HttpServletRequest request,
      @RequestBody TaskSearchForm taskRequest, @RequestParam(defaultValue = "register_date") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    log.info("[DEBUG API Filter List Task] : {}", request.isUserInRole("ROLE_ADMIN"));

    TaskSearchCriteria searchCriteria = beanMapper.map(taskRequest, TaskSearchCriteria.class);
    if (sortBy != null)
      searchCriteria.setSortBy(sortBy);
    searchCriteria.setSortByType(sortByType);
    if (searchCriteria.getStatus() == null || searchCriteria.getStatus().length() == 0)
      searchCriteria.setStatus(null);
    
    String userRole = accountHelper.getRole(request);
    searchCriteria.setUserRole(accountHelper.getRoleValue(userRole));
    
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER)) {
      searchCriteria.setChainId(null);
    } else if (request.isUserInRole(Constants.ROLE_CHAIN)
        || request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      
      if(account == null) {
        return HlsResponse.NOTFOUND();
      }
      
      if(request.isUserInRole(Constants.ROLE_CHAIN) && account.getChainId() == null) {
        return HlsResponse.NOTFOUND();
      }
      
      if(request.isUserInRole(Constants.ROLE_STORE) && account.getStoreId() == null) {
        return HlsResponse.NOTFOUND();
      }
      
      searchCriteria.setAccountId(account.getAccountId());
      
      if(request.isUserInRole(Constants.ROLE_CHAIN)) {
        searchCriteria.setChainId(account.getChainId());
        searchCriteria.setStoreId(searchCriteria.getStoreId());
      } else {
        searchCriteria.setChainId(null);
        searchCriteria.setStoreId(account.getStoreId());
      }
      
      /*
      
      if (account != null && account.getChainId() != null) {
        if (request.isUserInRole(Constants.ROLE_CHAIN)) {
          searchCriteria.setChainId(account.getChainId());
          searchCriteria.setStoreId(searchCriteria.getStoreId());
        } else {
          searchCriteria.setChainId(null);
          searchCriteria.setStoreId(account.getStoreId());
        }
      }
      
      */
    } else {
      return HlsResponse.FORBIDDEN();
    }
    
    long total = taskService.countBySearchCriteria(searchCriteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<TaskResult> tasks = taskService.searchCriteria(searchCriteria);
      tasks = taskHelper.filterResultByRole(tasks, searchCriteria);
      TaskResultResponse response = new TaskResultResponse(tasks, total);
      return HlsResponse.SUCCESS(response);
    }
  }
        
  @GetMapping("/tasks/{id}")
  public @ResponseBody HlsResponse getTaskDetail(HttpServletRequest request,
      @PathVariable("id") long taskId) {

    if (taskHelper.hasViewTaskRole(request, taskId)) {

      log.info("[DEBUG getTaskDetail] - {}", taskId);
      Task retTask = taskService.findTask(taskId);
      if (retTask == null) {
        return HlsResponse.NOTFOUND();
      }
      
      // get child tasks
      String userRole = accountHelper.getRole(request);
      TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
      searchCriteria.setSortBy("register_date");
      searchCriteria.setUserRole(accountHelper.getRoleValue(userRole));
      
      if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
          || request.isUserInRole(Constants.ROLE_USER)) {
        searchCriteria.setChainId(null);
      } else if (request.isUserInRole(Constants.ROLE_CHAIN)
          || request.isUserInRole(Constants.ROLE_STORE)) {
        AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        Account account = userDetails.getAccount();
        searchCriteria.setAccountId(account.getAccountId());
        if (account != null && account.getChainId() != null) {
          if (request.isUserInRole(Constants.ROLE_CHAIN)) {
            searchCriteria.setChainId(account.getChainId());
            searchCriteria.setStoreId(null);
          } else {
            searchCriteria.setChainId(null);
            searchCriteria.setStoreId(account.getStoreId());
          }
        }
      }
      List<TaskResult> childTasks = taskService.findChildTasks(taskId, searchCriteria);
      retTask.setChilds(childTasks);
      return HlsResponse.SUCCESS(retTask);
      
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PostMapping("/task")
  public @ResponseBody HlsResponse createTask(HttpServletRequest request,
      @RequestBody(required = false) TaskForm taskForm) {
    log.info("[DEBUG API Create Task] : {}", request.isUserInRole("ROLE_ADMIN"));
    String userRole = accountHelper.getRole(request);
   
    String msg = taskHelper.checkHasPermission(userRole);
    if (msg != null) {
      return HlsResponse.FORBIDDEN(null, msg);
    }
    
    if (taskForm == null)
      return HlsResponse.BADREQUEST();
    
    taskForm = taskHelper.filterByRole(taskForm, userRole);

    msg = taskHelper.doValidate(taskForm, true);
    
    if (msg != null) {
      return HlsResponse.BADREQUEST(null, msg);
    }

    //Task task = beanMapper.map(taskForm, Task.class);
    List<Task> tasks = new ArrayList<Task>();
    try {
      tasks = taskHelper.doCreate(taskForm);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("[DEBUG API Create/Edit Task] : Cannot create Task with exception : {}", e);
      return HlsResponse.SERVER_ERROR();
    }

    return HlsResponse.SUCCESS(tasks);

  }
  
  @GetMapping("/tasks/{id}/childs")
  public @ResponseBody HlsResponse getChildTasks(HttpServletRequest request,
      @PathVariable("id") long taskId, @RequestParam(defaultValue = "task_id") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    TaskSearchCriteria searchCriteria = beanMapper.map(request, TaskSearchCriteria.class);
    if (sortBy != null)
      searchCriteria.setSortBy(sortBy);
    searchCriteria.setSortByType(sortByType);
    
    String userRole = accountHelper.getRole(request);
    searchCriteria.setUserRole(accountHelper.getRoleValue(userRole));
    
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER)) {
      searchCriteria.setChainId(null);
    } else if (request.isUserInRole(Constants.ROLE_CHAIN)
        || request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      searchCriteria.setAccountId(account.getAccountId());
      if (account != null && account.getChainId() != null) {
        if (request.isUserInRole(Constants.ROLE_CHAIN)) {
          searchCriteria.setChainId(account.getChainId());
          searchCriteria.setStoreId(null);
        } else {
          searchCriteria.setChainId(null);
          searchCriteria.setStoreId(account.getStoreId());
        }
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
    
    log.info("[DEBUG getChildTask] - {}", taskId);
    List<TaskResult> childTasks = taskService.findChildTasks(taskId, searchCriteria);
    if (childTasks != null) {
      TaskResultResponse response = new TaskResultResponse(childTasks, childTasks.size());
      return HlsResponse.SUCCESS(response);
    } else {
      return HlsResponse.NOTFOUND();
    }
  }

  /**
   * Upload attach for task : 
   * 
   * create task -> taskId null
   * update task --> taskId not null
   * 
   * @param request
   * @param attachFile
   * @param taskId
   * @return
   */
  @PostMapping(value = "/tasks/attach/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public @ResponseBody HlsResponse upload(HttpServletRequest request, @RequestParam("attachFile") MultipartFile attachFile,
      @RequestParam(value =  "taskId", required = false) Long taskId) {
    
    // if hasViewTask and not is USER_ROLE
    //String userRole = accountHelper.getRole(request);
    
    log.info("[DEBUG getTaskDetail] - upload file {}  -- taskId {}", attachFile.getOriginalFilename(), taskId);
    try {
      TaskAttach attached = taskHelper.doUpload(taskId, attachFile.getOriginalFilename(), attachFile.getBytes());
      return HlsResponse.SUCCESS(attached);
    } catch (Exception e) {
      e.printStackTrace();
      return HlsResponse.SERVER_ERROR();
    }
    //} else {
      //return HlsResponse.FORBIDDEN();
    //}
  }
  
  @GetMapping(value = "/tasks/{id}/attach/download")
  public void download(@PathVariable("id") long taskId, @RequestParam(value = "attachId") long attachId, 
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    log.info("[DEBUG download] -  {} -- {} ", taskId, attachId);
    
    // has permission in task to download file 
    if (taskHelper.hasViewTaskRole(request, taskId)) {
      
      TaskAttach taskAttach = taskAttachService.findTaskAttach(attachId);
      
      // attach is deleted -> not found
      if(taskAttach == null) {
        response.setStatus(HttpStatus.SC_NOT_FOUND);
      } else {
        fileHelper.retrieveFile(taskAttach.getAttachName(), taskAttach.getAttachUrl(), response, request);
      }
    } else {
      response.setStatus(HttpStatus.SC_FORBIDDEN);
    }
  }
  
  @DeleteMapping(value = "/tasks/{id}/attach/{attachId}")
  public @ResponseBody HlsResponse delete(@PathVariable("id") long taskId, @PathVariable(value = "attachId") long attachId, 
      HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    log.info("[DEBUG delete] -  {} -- {} ", taskId, attachId);
    
    // if hasViewTask and not is USER_ROLE
    String userRole = accountHelper.getRole(request);
    if(taskHelper.hasViewTaskRole(request, taskId)  && !Constants.ROLE_USER.equals(userRole)) {
      try {
        
        taskHelper.doDelete(attachId);
        return HlsResponse.SUCCESS();
      } catch (Exception e) {
        return HlsResponse.SERVER_ERROR();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PostMapping(value = "/tasks/{id}/stopwatch")
  public @ResponseBody HlsResponse doStopWatch(HttpServletRequest request, @PathVariable("id") long taskId, 
      @RequestBody HashMap<String, String> dataHashMap) {
    
    // check permission : ADMIN, SUBADMIN, USER
    if(!(request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN) 
      || request.isUserInRole(Constants.ROLE_USER))) {
      return HlsResponse.FORBIDDEN();
    }
    
    // get current account
    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    
    long accountId = account.getAccountId();
    String action = dataHashMap.get("action");
    String type = dataHashMap.get("type");
    
    
    log.info("[DEBUG doStopWatch] -  accountId {} -- taskId {} --- action : {}", accountId, taskId, action);
    
    // action invalid
    if(!Commons.stopWatchActionIsValid(action)) {
      return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-STOPWATCH-ACTION-INVALID");
    }
    
    // type invalid
    if(!Commons.taskLogTypeIsValid(type)) {
      return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-STOPWATCH-TYPE-INVALID");
    }
    
    // check valid action + type of stopwatch
    TaskLog latestTaskLog = taskLogService.getLatestByAccount(taskId, accountId);
    
    // not has any taskLog : START - EXECUTE is valid
    if(latestTaskLog == null) {
      if(!(Constants.STOPWATCH_ACTION_START.equals(action) && Constants.TASKLOG_TYPE_EXEC.equals(type))) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-STOPWATCH-ACTIONTYPE-INVALID");
      }
    } else {

      // latest is START - EXECUTE  -->   STOP - EXECUTE
      if(Constants.STOPWATCH_ACTION_START.equals(latestTaskLog.getAction()) &&
          Constants.TASKLOG_TYPE_EXEC.equals(latestTaskLog.getType())) {
        
        if(! (Constants.STOPWATCH_ACTION_STOP.equals(action) &&
          Constants.TASKLOG_TYPE_EXEC.equals(type))) {
          return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-STOPWATCH-ACTIONTYPE-INVALID");
        }
        
      } 
      
      else if(Constants.STOPWATCH_ACTION_START.equals(latestTaskLog.getAction()) &&
          Constants.TASKLOG_TYPE_CONF.equals(latestTaskLog.getType())) {  

        // latest is START - CONFIRM --> STOP - CONFIRM
        
        if(! (Constants.STOPWATCH_ACTION_STOP.equals(action) &&
          Constants.TASKLOG_TYPE_CONF.equals(type))) {
          return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-STOPWATCH-ACTIONTYPE-INVALID");
        }
        
      } else {
      
      // latest is STOP - EXECUTE  -->   START - EXECUTE || START - CONFIRM
      // latest is STOP - CONFIRM --> START - EXECUTE || START - CONFIRM
        
        if(! ( (Constants.STOPWATCH_ACTION_START.equals(action) &&
            Constants.TASKLOG_TYPE_EXEC.equals(type))
            
            ||
            
            (Constants.STOPWATCH_ACTION_START.equals(action) &&
                Constants.TASKLOG_TYPE_CONF.equals(type))
            
            )) {
          return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-STOPWATCH-ACTIONTYPE-INVALID");
        }
      }
      
      if(Constants.STOPWATCH_ACTION_START.equals(latestTaskLog.getAction())) {
        latestTaskLog.setStopped(true);
        taskLogService.updateStopTaskLog(latestTaskLog.getTaskLogId());
      }
      
    }
    
    TaskLog taskLog = taskHelper.doStopWatch(taskId, accountId, action, type, account);
    return HlsResponse.SUCCESS(taskLog);
  }
  
  @PutMapping("/tasks/{id}")
  public @ResponseBody HlsResponse updateTask(HttpServletRequest request,
      @PathVariable("id") long taskId, @RequestBody TaskForm taskForm) {
    
    log.info("[DEBUG API updateTask] : {}  ---  {}", taskId, taskForm);

    // not has permission
    if( !taskHelper.hasViewTaskRole(request, taskId)) {
      return HlsResponse.FORBIDDEN();
    }
    
    if (taskForm == null)
      return HlsResponse.BADREQUEST();
    
    String msg = taskHelper.doValidate(taskForm, false);
    if (msg != null) {
      return HlsResponse.BADREQUEST(null, msg);
    }
    
    Task task = taskService.findTask(taskId);
    
    // set update fields by role
    String userRole = accountHelper.getRole(request);
    
    task.setCategoryId(taskForm.getCategoryId());
    task.setTitle(taskForm.getTitle());
    task.setNote(taskForm.getNote());
    
    boolean needSendNotification = false;
    if (Constants.ROLE_ADMIN.equals(userRole) || Constants.ROLE_SUBADMIN.equals(userRole)) {
      
      // only ADMIN, SUBADMIN can change status
      if(!StringUtils.equalsIgnoreCase(task.getStatus(), taskForm.getStatus())) {
        needSendNotification = true;
      }
      task.setStatus(taskForm.getStatus());
      
      task.setPriority(taskForm.getPriority());
      task.setAssigneeId(taskForm.getAssigneeId());
      task.setStartDate(taskForm.getStartDate());
      task.setDueDate(taskForm.getDueDate());
      task.setEstTime(taskForm.getEstTime());
      task.setEstPoint(taskForm.getEstPoint());
      task.setVisible(taskForm.isVisible());
      
    }
    
    CommentForm commentForm = taskForm.getCommentForm();
    Comment comment = null;
    // commentForm != null --> create comment
    if(commentForm != null) {
      /*
      String errMsg = taskHelper.doValidateComment(commentForm);
      if(StringUtils.isNotBlank(errMsg)) {
        return new HlsResponse(HlsResponse.BADREQUEST, errMsg);
      }
      */
      
      if(commentForm.getType() == null || !Arrays.asList(Constants.COMMENT_TYPE_LIST).contains(commentForm.getType()) ) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-CREATECOMMENT-COMMENTTYPE-INVALID");
      }
      /*
      if( StringUtils.isBlank(commentForm.getCommentText()) 
          && ArrayUtils.isEmpty(commentForm.getNotifyToAccIds()) ) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-CREATECOMMENT-EMPTYCOMMENT-INVALID");
      }
      */
      
      comment = beanMapper.map(commentForm, Comment.class);
      comment.setTaskId(taskId);
      
      comment = taskHelper.setCommentCreatorInfo(comment, request, commentForm.getNotifyToAccIds());
      
      /*
      // only ADMIN, SUBADMIN, USER create comment --> update to task
      // update parent task if has any change of fields :
      // status, assigneeId, startDate, dueDate, estTime, estPoint
      if(Constants.ROLE_ADMIN.equals(userRole) || Constants.ROLE_SUBADMIN.equals(userRole) 
          || Constants.ROLE_USER.equals(userRole)) {
        
        if(!StringUtils.equalsIgnoreCase(task.getStatus(), comment.getStatus())) {
          task.setStatus(comment.getStatus());
        }
        
        if(!Commons.compareLong(task.getAssigneeId(), comment.getAssigneeId())) {
          task.setAssigneeId(comment.getAssigneeId());
        }
        
        if(!Commons.compareLong(task.getEstTime(), comment.getEstTime())) {
          task.setEstTime(comment.getEstTime());
        }
        
        if(!Commons.compareLong(task.getEstPoint(), comment.getEstPoint())) {
          task.setEstPoint(comment.getEstPoint());
        }

        if(!Commons.compareDate(task.getStartDate(), comment.getStartDate())) {
          task.setStartDate(comment.getStartDate());
        }
        
        if(!Commons.compareDate(task.getDueDate(), comment.getDueDate())) {
          task.setDueDate(comment.getDueDate());
        }
      }
      */
    }
    
    try {
      
      taskHelper.doUpdate(task, userRole, taskForm.getStoreIds(), comment, taskForm.getItems());
      
      // send notification to : assignee, registerPersonId, chain users, store users, current user
      if(needSendNotification) {
        taskHelper.doSendNotification(task, request);
      }
      
      return HlsResponse.SUCCESS(task);
    } catch (Exception e) {
      e.printStackTrace();
      return HlsResponse.SERVER_ERROR();
    }
  }
  
  @GetMapping("/tasks/{id}/totaltimes")
  public @ResponseBody HlsResponse totalTimes(HttpServletRequest request, @PathVariable("id") long taskId) {
    
    // not has permission
    if( !taskHelper.hasViewTaskRole(request, taskId)) {
      return HlsResponse.FORBIDDEN();
    }
    
    Task task = taskService.findTask(taskId);
    
    List<Long> taskIds = new ArrayList<Long>();
    
    // if is parent --> count all child tasklog time
    if (task.getParentTaskId() == null && task.getStoreId() == null ) {
      List<Task> childs = taskService.getTaskByParentId(taskId);
      for (Task child : childs) {
        taskIds.add(child.getTaskId());
      }
    } else {
      taskIds.add(taskId);
    }
    
    if(CollectionUtils.isNotEmpty(taskIds)) {
      Map<String, Long> retData = taskLogService.sumTaskLogTime(taskIds);
      
      if(retData == null) {
        return HlsResponse.NOTFOUND();
      }
      
      if(retData.get("sum_execute_time") == null) {
        retData.put("sum_execute_time", 0L);
      }
      
      if(retData.get("sum_confirm_time") == null) {
        retData.put("sum_confirm_time", 0L);
      }
      
      return HlsResponse.SUCCESS(retData);
    } else {
      return HlsResponse.NOTFOUND();
    }
  }
  
  @GetMapping("/tasklogs/tasks")
  public @ResponseBody HlsResponse searchTasksByTaskLog(HttpServletRequest request,
      @RequestParam (required = false) @DateTimeFormat(pattern="yyyy/MM/dd") @Valid Date startDate,
      @RequestParam (required = false) @DateTimeFormat(pattern="yyyy/MM/dd") @Valid Date endDate,
      @RequestParam (required = false) Long accountId,
      @RequestParam (defaultValue = "10") int size, @RequestParam(defaultValue = "1") int page,
      @RequestParam (defaultValue = "task_id") String sortBy,
      @RequestParam(defaultValue = "1") Integer sortByType) {
    log.info("[DEBUG API Filter List Task from Task Log] : {}", request.isUserInRole("ROLE_ADMIN"));
    
    if (accountId == null)
      return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-ACCOUNTID-INVALID");

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      TaskSearchCriteria searchCriteria = new TaskSearchCriteria();
      searchCriteria.setSortBy(sortBy);
      searchCriteria.setSortByType(sortByType);
      searchCriteria.setStartDate(startDate);
      searchCriteria.setEndDate(endDate);
      searchCriteria.setAccountId(accountId);
      
      long total = taskService.countByTaskLog(searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<TaskResult> tasks = taskService.searchByTaskLog(searchCriteria);
        tasks = taskHelper.filterResultByRole(tasks, searchCriteria);
        TaskResultResponse response = new TaskResultResponse(tasks, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping("/tasks/{id}/usermentions")
  public @ResponseBody HlsResponse getUserMentions(HttpServletRequest request,
      @PathVariable("id") Long taskId,
      @RequestParam (defaultValue = "account_id") String sortBy,
      @RequestParam (defaultValue = "1") Long type) {
    if (taskId == null)
      return HlsResponse.BADREQUEST();
    Task task = taskService.findTask(taskId);
      
    if (task == null)
      return HlsResponse.BADREQUEST();
    
    List<AccountDisplay> users = accountService.getUserMentions(taskId, task.getStoreId(), sortBy, type);
    int total = users == null ? 0 : users.size();

    AccountDisplayResultResponse response = new AccountDisplayResultResponse(users, total);
    return HlsResponse.SUCCESS(response);
  }
  
  
  @PostMapping(value = "/tasks/{id}/status")
  public @ResponseBody HlsResponse changeStatus(HttpServletRequest request, @PathVariable("id") long taskId, 
      @RequestBody HashMap<String, String> dataHashMap) {
    
    // get current account
    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    
    long accountId = account.getAccountId();
    String status = StringUtils.trim(dataHashMap.get("status"));
    
    log.info("[DEBUG changeStatus] -  accountId {} -- taskId {} --- status : {}", accountId, taskId, status);
    
    // status invalid
    if (status != null && !Arrays.asList(Constants.TASK_STATUS_LIST).contains(status)) {
      return HlsResponse.BADREQUEST(null, "ERROR-TASK-STATUS-INVALID");
    }
    
    // find task
    Task task = taskService.findTask(taskId);
    
    // task not found
    if(task == null) {
      return HlsResponse.NOTFOUND();
    }
    
    // change task status, and update childs if need
    if(!StringUtils.equalsIgnoreCase(status, task.getStatus())) {
      taskHelper.doChangeStatus(task, status);
      taskHelper.doSendNotification(task, request);
    }
    
    return HlsResponse.SUCCESS(task);
  }
}
