package com.cnctor.hls.app.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.app.account.AccountHelper;
import com.cnctor.hls.app.comment.CommentForm;
import com.cnctor.hls.app.notification.NotificationHelper;
import com.cnctor.hls.app.plan.ItemForm;
import com.cnctor.hls.app.utils.Commons;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AccountDisplay;
import com.cnctor.hls.domain.model.Comment;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.model.TaskAttach;
import com.cnctor.hls.domain.model.TaskLog;
import com.cnctor.hls.domain.model.TaskResult;
import com.cnctor.hls.domain.model.UserTaskSummary;
import com.cnctor.hls.domain.repository.task.TaskSearchCriteria;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.category.CategoryService;
import com.cnctor.hls.domain.service.comment.CommentService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.task.TaskService;
import com.cnctor.hls.domain.service.taskattach.TaskAttachService;
import com.cnctor.hls.domain.service.taskitem.TaskItemService;
import com.cnctor.hls.domain.service.tasklog.TaskLogService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.usertasksummary.UserTaskSummaryService;

@Component
public class TaskHelper {

  @Inject
  TaskService taskService;

  @Inject
  StoreService storeService;

  @Inject
  CommentService commentService;

  @Inject
  CategoryService categoryService;

  @Inject
  TaskAttachService taskAttachService;

  @Inject
  Mapper beanMapper;

  @Inject
  UserTaskSummaryService utsService;

  @Inject
  TaskLogService taskLogService;

  @Inject
  AccountService accountService;

  @Inject
  NotificationHelper notificationHelper;

  @Inject
  AccountHelper accountHelper;
  
  @Inject
  TaskItemService tiService;

  @Transactional(rollbackFor = Exception.class)
  public void createComment(Comment comment, Task task, String userRole, long[] notifyToAccIds,
      HttpServletRequest request) {

    commentService.createComment(comment);

    // only ADMIN, SUBADMIN, USER create comment --> update to task
    // update parent task if has any change of fields :
    // status, assigneeId, startDate, dueDate, estTime, estPoint
    if (Constants.ROLE_ADMIN.equals(userRole) || Constants.ROLE_SUBADMIN.equals(userRole)
        || Constants.ROLE_USER.equals(userRole)) {

      String oldStatus = task.getStatus();
      if (!StringUtils.equalsIgnoreCase(task.getStatus(), comment.getStatus())
          || !Commons.compareLong(task.getAssigneeId(), comment.getAssigneeId())
          || !Commons.compareLong(task.getEstTime(), comment.getEstTime())
          || !Commons.compareLong(task.getEstPoint(), comment.getEstPoint())
          || !Commons.compareDate(task.getStartDate(), comment.getStartDate())
          || !Commons.compareDate(task.getDueDate(), comment.getDueDate())) {

        task.setAssigneeId(comment.getAssigneeId());
        task.setStatus(comment.getStatus());
        task.setStartDate(comment.getStartDate());
        task.setDueDate(comment.getDueDate());
        task.setEstPoint(comment.getEstPoint());
        task.setEstTime(comment.getEstTime());

        taskService.updateTask(task);

        // if parent task --> update child
        if (task.getParentTaskId() == null && task.getStoreId() == null) {
          List<Task> childs = taskService.getTaskByParentId(task.getTaskId());
          if (CollectionUtils.isNotEmpty(childs)) {
            for (Task child : childs) {
              taskService.updateWithParentInfo(child, task);
            }
          }
        }

        // change status --> send notification
        if (!StringUtils.equalsIgnoreCase(oldStatus, task.getStatus())) {
          doSendNotification(task, request);
        }
      }
    }
  }

  /*
   * admin, subadmin, user can view all chain, store view task of it's chain
   */
  public boolean hasViewTaskRole(HttpServletRequest request, long taskId) {
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER)) {
      return true;
    } else {
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();

      Task task = taskService.findTask(taskId);
      if (account == null || task == null)
        return false;
      Long storeId = task.getStoreId();
      if (storeId == null) {
        if (request.isUserInRole(Constants.ROLE_CHAIN)) {
          if (task.getRegisterPersonId() != null) {
            if (task.getRegisterPersonId().equals(account.getAccountId()))
              return true;
            List<Account> accounts = accountService.findByChainId(account.getChainId());
            if (accounts != null)
              return accounts.stream()
                  .anyMatch(acc -> task.getRegisterPersonId().equals(acc.getAccountId()));
          }
        } else if (request.isUserInRole(Constants.ROLE_STORE)) {
          if (task.getRegisterPersonId().equals(account.getAccountId()))
            return true;
        }
      } else {
        if (request.isUserInRole(Constants.ROLE_CHAIN)) {
          if (account.getChainId() != null) {
            List<Store> stores = storeService.findByChainId(account.getChainId());
            if (stores != null)
              return stores.stream().anyMatch(store -> storeId.equals(store.getStoreId()));
          }
        } else if (request.isUserInRole(Constants.ROLE_STORE)) {
          if (storeId.equals(account.getStoreId()))
            return true;
        }
      }
    }
    return false;
  }


  /**
   * Validate Task before insert
   * 
   * @param taskForm
   * @return
   */
  public String doValidate(TaskForm taskForm, boolean isCreate) {
    /*
    if (taskForm.getCategoryId() == null) {
      return "ERROR-TASK-CATEGORY-INVALID";
    }
    Category category = categoryService.findCategory(taskForm.getCategoryId());
    if (category == null) {
      return "ERROR-TASK-CATEGORY-NOTFOUND";
    }
    */
    if (StringUtils.isBlank(taskForm.getTitle())) {
      return "ERROR-TASK-TITLE-INVALID";
    }

    // create task required storeIds
    if (isCreate) {
      Long[] storeIds = taskForm.getStoreIds();
      if (storeIds == null || storeIds.length == 0) {
        return "ERROR-TASK-STORE-INVALID";
      }
    }

    if (taskForm.getEstTime() != null && taskForm.getEstTime() < 0) {
      return "ERROR-TASK-ESTTIME-INVALID";
    }
    if (taskForm.getEstPoint() != null && taskForm.getEstPoint() < 0) {
      return "ERROR-TASK-ESTPOINT-INVALID";
    }
    if (taskForm.getStatus() != null
        && !Arrays.asList(Constants.TASK_STATUS_LIST).contains(taskForm.getStatus())) {
      return "ERROR-TASK-STATUS-INVALID";
    }
    if (taskForm.getPriority() != null
        && !Arrays.asList(Constants.TASK_PRIORITY_LIST).contains(taskForm.getPriority())) {
      return "ERROR-TASK-PRIORITY-INVALID";
    }
    /*
    if (taskForm.getStartDate() != null && taskForm.getDueDate() != null
        && taskForm.getStartDate().after(taskForm.getDueDate())) {
      return "ERROR-TASK-DATE-INVALID";
    }
    */
    return null;
  }

  /**
   * Check Permisson Of User
   * 
   * @param userRole
   * @return
   */
  public String checkHasPermission(String userRole) {
    if (userRole == null || Constants.ROLE_USER.equals(userRole)) {
      return "ERROR-TASK-ROLE-INVALID";
    }
    return null;
  }

  /**
   * Create Task
   * 
   * @param taskForm
   * @return
   * @throws Exception
   */
  @Transactional(rollbackFor = Exception.class)
  public List<Task> doCreate(TaskForm taskForm) throws Exception {

    List<Task> tasks = new ArrayList<Task>();
    Long[] taskAttachIds = taskForm.getTaskAttachIds();
    // create parent
    Task task = beanMapper.map(taskForm, Task.class);
    task.setStoreId(taskForm.getStoreId());

    task.setParentTaskId(null);
    Task saved = taskService.createTask(task);
    Long parentTaskId = saved.getTaskId();
    tasks.add(saved);
    
    List<ItemForm> items = taskForm.getItems();
    
    // create task items for parent task
    if(items != null && items.size() > 0) {
      for (ItemForm iForm : items) {
        tiService.upsert(parentTaskId, iForm.getItemId(), iForm.getItemJsonValue());
      }
    }
    
    // create attach for parent task
    if (taskForm.getTaskAttachIds() != null && taskAttachIds.length > 0) {
      taskAttachService.createAttachFile(parentTaskId, taskAttachIds);
    }

    // create task for each store
    if (taskForm.getStoreId() == null && parentTaskId != null && taskForm.getStoreIds() != null
        && taskForm.getStoreIds().length > 0) {

      for (long storeId : taskForm.getStoreIds()) {

        Task childTask = beanMapper.map(taskForm, Task.class);
        childTask.setStoreId(storeId);
        childTask.setParentTaskId(parentTaskId);

        Task childTaskSaved = taskService.createTask(childTask);
        Long childTaskId = childTaskSaved.getTaskId();

        // create task item for each child task
        if(items != null && items.size() > 0) {
          for (ItemForm iForm : items) {
            tiService.upsert(childTaskId, iForm.getItemId(), iForm.getItemJsonValue());
          }
        }
        
        // create attachment for each child task
        if (childTaskId != null) {
          if (taskForm.getTaskAttachIds() != null && taskAttachIds.length > 0) {
            taskAttachService.createAttachFile(childTaskId, taskAttachIds);
          }
        }
        tasks.add(childTaskSaved);
      }
    }
    return tasks;
  }

  public void createAttachFile(Long taskId, Long[] taskAttachIds) throws Exception {
    for (Long taskAttachId : taskAttachIds) {

      TaskAttach attach = taskAttachService.findTaskAttach(taskAttachId);

      // if attachment : taskId is null --> update
      if (attach.getTaskId() == null) {
        attach.setTaskId(taskId);
        taskAttachService.updateTaskId(attach);
      } else { // else duplicate attachment
        TaskAttach dupAttach = taskAttachService.duplicateFile(attach);
        dupAttach.setTaskId(taskId);
        taskAttachService.updateTaskId(dupAttach);
      }
    }
  }


  @Transactional(rollbackFor = Exception.class)
  public Task doUpdate(Task task, String userRole, Long[] storeIds, Comment comment, List<ItemForm> items)
      throws Exception {

    // insert comment
    if (comment != null) {
      commentService.createComment(comment);
    }

    long taskId = task.getTaskId();
    // update parent task
    taskService.updateTask(task);

    // update task items for parent task
    if(items != null && items.size() > 0) {
      for (ItemForm iForm : items) {
        tiService.upsert(taskId, iForm.getItemId(), iForm.getItemJsonValue());
      }
    }
    
    
    // delete task child if not in storeIds
    // add child task
    // update all child task with parent info
    if (task.getParentTaskId() == null && ArrayUtils.isNotEmpty(storeIds)) {


      // task created by store has storeId != null
      Long storeId = task.getStoreId();
      if (storeId != null) {

        if (storeIds.length == 1 && storeIds[0].longValue() == storeId) {
          // do nothing
        } else if (storeIds.length == 1 && storeIds[0].longValue() != storeId) {

          // create new task
          taskService.duplicateToParent(task, storeIds[0]);

          // delete old task
          taskService.delete(task);
        }

        // storeIds length >= 2 -> create parent task
        else {

          // create parentTask
          Task parentTask = taskService.duplicateToParent(task, null);

          // storeId not in storeIds --> delete
          // else update taskParentId
          if (!ArrayUtils.contains(storeIds, storeId)) {
            taskService.delete(task);
          } else {
            taskService.updateWithParentInfo(task, parentTask);
          }

          // create task
          for (Long sId : storeIds) {
            if (sId.longValue() != storeId.longValue()) {
              taskService.duplicateToChild(parentTask, sId);
            }
          }
        }

      } else {
        List<Task> childTasks = taskService.getTaskByParentId(taskId);

        if (Constants.ROLE_ADMIN.equals(userRole) || Constants.ROLE_SUBADMIN.equals(userRole)
            || Constants.ROLE_CHAIN.equals(userRole)) {

          for (Task child : childTasks) {

            // new storeIds contain -> update
            if (ArrayUtils.contains(storeIds, child.getStoreId())) {
              taskService.updateWithParentInfo(child, task);
            } else { // delete child
              taskService.delete(child);
            }
          }

          // if storeId not in childTask --> create new
          for (Long sId : storeIds) {
            boolean exist = false;
            for (Task child : childTasks) {
              if (sId.longValue() == child.getStoreId().longValue()) {
                exist = true;
              }
            }

            if (!exist) {
              // duplicate parent task to child
              taskService.duplicateToChild(task, sId);
            }

          }
        }
      }
    }

    return task;
  }

  /**
   * Filter field by User Role (ADMIN, CHAIN, STORE)
   * 
   * @param taskForm
   * @param userRole
   * @return
   */
  public TaskForm filterByRole(TaskForm taskForm, String userRole) {
    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    if (account != null) {
      taskForm.setRegisterPersonId(account.getAccountId());
    }
    if (Constants.ROLE_STORE.equals(userRole)) {
      taskForm.setStoreId(account.getStoreId());
    } else {
      taskForm.setStoreId(null);
    }

    if (Constants.ROLE_CHAIN.equals(userRole) || Constants.ROLE_STORE.equals(userRole)) {
      taskForm.setStatus(Constants.TASK_STATUS_WORKING);
      taskForm.setPriority(Constants.TASK_PRIORITY_MEDIUM);
      taskForm.setAssigneeId(null);
      taskForm.setStartDate(null);
      taskForm.setEstTime(null);
      taskForm.setEstPoint(null);
      taskForm.setVisible(true);
    }

    if (Constants.ROLE_STORE.equals(userRole)) {
      if (account != null && account.getStoreId() != null) {
        Long[] storeIds = {account.getStoreId()};
        taskForm.setStoreIds(storeIds);
      }
    }
    if (Constants.ROLE_ADMIN.equals(userRole) || Constants.ROLE_SUBADMIN.equals(userRole)) {
      if (account != null) {
        taskForm.setDirectorId(account.getAccountId());
      }
    }
    return taskForm;
  }

  @Transactional(rollbackFor = Exception.class)
  public TaskLog doStopWatch(long taskId, long accountId, String action, String type,
      Account account) {

    // get userTaskSummary uts if not exist -> insert else update
    UserTaskSummary uts = utsService.findUserTaskSummary(taskId, accountId);

    long duration = 0L;
    Date currTime = new Date();
    if (uts == null) {
      uts = new UserTaskSummary();
      uts.setTaskId(taskId);
      uts.setAccountId(accountId);
      uts.setCreatedDate(currTime);
      uts.setSummaryTime(0L);
      uts.setLatestAction(Constants.STOPWATCH_ACTION_START); // first time always is START action
      uts.setLatestActionTime(currTime);

    } else {
      // update summary time , latest action, latest action time
      // if STOP -> update summary time
      if (action.equals(Constants.STOPWATCH_ACTION_STOP)) {
        duration = currTime.getTime() - uts.getLatestActionTime().getTime();
        uts.setSummaryTime(uts.getSummaryTime() + duration);
      }
      uts.setLatestAction(action);
      uts.setLatestActionTime(currTime);

    }

    utsService.upsert(uts);

    // insert TaskLog
    TaskLog taskLog = new TaskLog();

    taskLog.setTaskId(taskId);
    taskLog.setAccountId(account.getAccountId());
    taskLog.setAction(action);
    taskLog.setTaskLogDate(currTime);
    taskLog.setType(type);

    // if stopwatch is START -> set executeTime, accumulationTime is NULL
    // if stopwatch is STOP -> set executeTime, accumulationTime
    if (action.equals(Constants.STOPWATCH_ACTION_STOP)) {

      if (Constants.TASKLOG_TYPE_CONF.equals(type)) {
        taskLog.setConfirmTime(duration);
      }

      else if (Constants.TASKLOG_TYPE_EXEC.equals(type)) {
        taskLog.setExecuteTime(duration);
      }

      taskLog.setAccumulationTime(uts.getSummaryTime());
    }

    taskLogService.createTaskLog(taskLog);

    return taskLog;
  }

  public List<TaskResult> filterResultByRole(List<TaskResult> tasks,
      TaskSearchCriteria searchCriteria) {
    if (tasks != null && tasks.size() > 0) {

      if (searchCriteria.getUserRole() == 3) {
        for (int i = 0; i < tasks.size(); i++) {
          TaskResult task = tasks.get(i);
          if (!task.getRegisterPersonId().equals(searchCriteria.getAccountId())
              && task.getTotal() != null && task.getTotal() <= 0) {
            TaskResult t = new TaskResult();
            t.setTaskId(task.getTaskId());
            t.setTitle(task.getTitle());
            t.setStatus(task.getStatus());
            t.setStoreId(task.getStoreId());
            t.setRegisterPersonName(task.getRegisterPersonName());
            tasks.set(i, t);
          }
        }
      } else if (searchCriteria.getUserRole() == 2) {
        for (int i = 0; i < tasks.size(); i++) {
          TaskResult task = tasks.get(i);
          if (!task.getRegisterPersonId().equals(searchCriteria.getAccountId())) {
            TaskResult t = new TaskResult();
            t.setTaskId(task.getTaskId());
            t.setTitle(task.getTitle());
            t.setStatus(task.getStatus());
            t.setStoreId(task.getStoreId());
            t.setRegisterPersonName(task.getRegisterPersonName());
            tasks.set(i, t);
          }
        }
      }
    }
    return tasks;
  }

  @Transactional(rollbackFor = Exception.class)
  public TaskAttach doUpload(Long taskId, String originFileName, byte[] fileInBytes)
      throws Exception {

    TaskAttach attached = taskAttachService.attachFile(taskId, originFileName, fileInBytes);
    Long[] attachedIds = new Long[] {attached.getTaskAttachId()};

    // if is parent task and has child task --> duplicate attachment
    if (taskId != null) {
      List<Task> childTasks = taskService.getTaskByParentId(taskId);
      if (childTasks != null && childTasks.size() > 0) {
        for (Task cTask : childTasks) {
          taskAttachService.createAttachFile(cTask.getTaskId(), attachedIds);
        }
      }
    }
    return attached;
  }

  @Transactional(rollbackFor = Exception.class)
  public void doDelete(long attachId) {

    TaskAttach taskAttach = taskAttachService.findTaskAttach(attachId);
    taskAttachService.delete(taskAttach);

    // delete child attach in db + s3 if exist
    List<TaskAttach> childs = taskAttachService.getByParentId(attachId);
    if (CollectionUtils.isNotEmpty(childs)) {
      for (TaskAttach child : childs) {
        taskAttachService.delete(child);
      }
    }
  }


  public String doValidateComment(CommentForm commentForm) {

    // not has taskId
    if (commentForm.getTaskId() == null) {
      return "ERROR-CREATECOMMENT-TASKID-INVALID";
    }

    // status is not valid
    if (commentForm.getStatus() != null && !Commons.taskStatusIsValid(commentForm.getStatus())) {
      return "ERROR-CREATECOMMENT-TASKSTATUS-INVALID";
    }
    /*
    // start date < due date
    if (commentForm.getStartDate() != null && commentForm.getDueDate() != null
        && commentForm.getStartDate().after(commentForm.getDueDate())) {
      return "ERROR-CREATECOMMENT-DATE-INVALID";
    }
    */
    // estTime, estPoint < 0
    if (commentForm.getEstTime() != null && commentForm.getEstTime() < 0) {
      return "ERROR-CREATECOMMENT-ESTTIME-INVALID";
    }

    if (commentForm.getEstPoint() != null && commentForm.getEstPoint() < 0) {
      return "ERROR-CREATECOMMENT-ESTPOINT-INVALID";
    }

    if (commentForm.getType() == null
        || !Arrays.asList(Constants.COMMENT_TYPE_LIST).contains(commentForm.getType())) {
      return "ERROR-CREATECOMMENT-COMMENTTYPE-INVALID";
    }
    /*
    if (StringUtils.isBlank(commentForm.getCommentText())) {
      return "ERROR-CREATECOMMENT-EMPTYCOMMENT-INVALID";
    }
    */
    return StringUtils.EMPTY;
  }

  public Comment setCommentCreatorInfo(Comment comment, HttpServletRequest request,
      long[] notifyToAccIds) {
    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    comment.setCreatorId(account.getAccountId());
    comment.setCreatorName(account.getDisplayName());

    if (StringUtils.isBlank(comment.getType())) {
      String type = Constants.COMMENT_TYPE_USER;
      if (request.isUserInRole(Constants.ROLE_CHAIN)
          || request.isUserInRole(Constants.ROLE_STORE)) {
        type = Constants.COMMENT_TYPE_STORE;
      }
      comment.setType(type);
    }

    comment.setCreatedDate(new Date());

    // set notify to user
    if (notifyToAccIds != null && notifyToAccIds.length > 0) {
      String notifyTo = StringUtils.EMPTY;

      List<AccountDisplay> notifyAccs = accountService.getByIds(notifyToAccIds);
      if (notifyAccs != null && notifyAccs.size() > 0) {
        for (int i = 0; i < notifyAccs.size(); i++) {
          if (i > 0) {
            notifyTo += ",";
          }
          notifyTo += notifyAccs.get(i).getDisplayName();
        }

        comment.setNotifyTo(notifyTo);
      }
    }
    return comment;
  }

  public void doSendNotification(Task task, HttpServletRequest request) {
    Set<Long> accIds = new HashSet<Long>();

    accIds.add(task.getAssigneeId());
    accIds.add(task.getRegisterPersonId());

    long currentAccId = accountHelper.getCurrentAccount(request).getAccountId();
    accIds.add(currentAccId);

    // get store users, chain users
    Long storeId = task.getStoreId();
    if (storeId != null) {
      Store store = storeService.findStore(storeId);
      if (store != null) {
        Long chainId = store.getChainId();
        List<Long> storeChainAccIds = accountService.getUserIdsByChainOrStore(chainId, storeId);
        accIds.addAll(storeChainAccIds);

      }
    }

    for (Long accId : accIds) {
      if (accId != null) {
        Notification notification = new Notification();
        notification.setCreatorId(currentAccId);
        notification.setRecipientId(accId);
        notification.setActionId(task.getTaskId());
        notification.setTitle(Constants.NOTIFICATION_TYPE_CHANGE_TASK_STATUS);
        notification.setActionValue(task.getStatus());
        
        notificationHelper.asyncSendNotification(notification);
      }
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void doChangeStatus(Task task, String status) {

    task.setStatus(status);
    taskService.updateTask(task);

    if (task.getParentTaskId() == null) {
      List<Task> childTasks = taskService.getTaskByParentId(task.getTaskId());
      for (Task child : childTasks) {
        taskService.updateWithParentInfo(child, task);
      }
    }
  }
}
