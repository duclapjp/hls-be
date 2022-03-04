package com.cnctor.hls.domain.service.task;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.dozer.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.model.TaskAttach;
import com.cnctor.hls.domain.model.TaskResult;
import com.cnctor.hls.domain.model.TaskStore;
import com.cnctor.hls.domain.repository.comment.CommentRepository;
import com.cnctor.hls.domain.repository.task.TaskRepository;
import com.cnctor.hls.domain.repository.task.TaskSearchCriteria;
import com.cnctor.hls.domain.repository.tasklog.TaskLogRepository;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummaryRepository;
import com.cnctor.hls.domain.service.taskattach.TaskAttachService;

@Service
public class TaskServiceImpl implements TaskService {

  @Inject
  TaskRepository taskRepository;
  
  @Inject
  Mapper beanMapper;
  
  @Inject
  CommentRepository commentRepository;
  
  @Inject
  TaskAttachService taskAttachService;
  
  @Inject
  TaskLogRepository taskLogRepository;
  
  @Inject
  UserTaskSummaryRepository utsRepository;
  
  @Override
  public List<TaskResult> getTasks() {
    return taskRepository.getTask();
  }

  @Override
  public long countBySearchCriteria(TaskSearchCriteria searchCriteria) {
    return taskRepository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<TaskResult> searchCriteria(TaskSearchCriteria searchCriteria) {
    return taskRepository.searchCriteria(searchCriteria);
  }
  
  public Task findTask(long taskId) {
    return taskRepository.findOne(taskId);
  }

  @Override
  public Task updateTask(Task task) {
    taskRepository.update(task);
    return task;
  }

  @Override
  public Task createTask(Task task) {
    taskRepository.insert(task);
    return task;
  }

  @Override
  public TaskStore createTaskStore(TaskStore ts) {
    taskRepository.insertTaskStore(ts);
    return ts;
  }

  @Override
  public List<TaskResult> findChildTasks(long taskId, TaskSearchCriteria searchCriteria) {
    return taskRepository.findChildTasks(taskId, searchCriteria);
  }
  
  @Override
  public List<Task> getTaskByParentId(long parentId) {
    return taskRepository.getTaskByParentId(parentId);
  }

  /**
   * update child task with parent info
   */
  
  @Override
  public Task updateWithParentInfo(Task childTask, Task parentTask) {
    
    long childTaskId = childTask.getTaskId();
    Long storeId = childTask.getStoreId();
    
    long parentTaskId = parentTask.getTaskId();
    
    // clone parent 
    childTask = SerializationUtils.clone(parentTask);
    
    childTask.setTaskId(childTaskId);
    childTask.setStoreId(storeId);
    childTask.setParentTaskId(parentTaskId);
    
    taskRepository.update(childTask);
    
    return childTask;
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void delete(Task task) {
    
    long taskId = task.getTaskId();
    // delete comment 
    commentRepository.deleteByTaskId(taskId);
    
    // delete attach 
    List<TaskAttach> attachs = task.getAttachs();
    if(CollectionUtils.isNotEmpty(attachs)) {
      for (TaskAttach atch : attachs) {
        taskAttachService.delete(atch);
      }
    }
    
    // delete task log 
    taskLogRepository.deleteByTaskId(taskId);
    
    // delete task log summary
    utsRepository.deleteByTaskId(taskId);
    
    // delete task
    taskRepository.delete(taskId);
  }

  @Override
  public Task duplicateToChild(Task parentTask, long storeId) throws Exception {

    Task childTask = SerializationUtils.clone(parentTask);
    
    childTask.setParentTaskId(parentTask.getTaskId());
    childTask.setStoreId(storeId);
    
    taskRepository.insert(childTask);
    
    List<Long> attachIds = new ArrayList<Long>();
    
    
    
    if(CollectionUtils.isNotEmpty(parentTask.getAttachs())) {
      for (TaskAttach tAttach : parentTask.getAttachs()) {
        attachIds.add(tAttach.getTaskAttachId());
      }
    }
    
    if(CollectionUtils.isNotEmpty(attachIds)) {
      taskAttachService.createAttachFile(childTask.getTaskId(), attachIds.toArray(new Long[0]));
    }    
    return childTask;
  }

  @Override
  public Task duplicateToParent(Task childTask, Long storeId) throws Exception {
    Task parentTask = SerializationUtils.clone(childTask);
    parentTask.setParentTaskId(null);
    parentTask.setStoreId(storeId);
    
    taskRepository.insert(parentTask);
    
    // srcTask has attach --> duplicate attach to parent 
    List<Long> attachIds = new ArrayList<Long>();
    if(CollectionUtils.isNotEmpty(childTask.getAttachs())) {
      for (TaskAttach tAttach : childTask.getAttachs()) {
        attachIds.add(tAttach.getTaskAttachId());
      }
    }
    
    if(CollectionUtils.isNotEmpty(attachIds)) {
      List<TaskAttach> attachs = taskAttachService.createParentAttachFile(parentTask.getTaskId(), attachIds.toArray(new Long[0]));
      parentTask.setAttachs(attachs);
    }
    
    return parentTask;
  }

  @Override
  public long countByTaskLog(TaskSearchCriteria searchCriteria) {
    return taskRepository.countByTaskLog(searchCriteria);
  }

  @Override
  public List<TaskResult> searchByTaskLog(TaskSearchCriteria searchCriteria) {
    return taskRepository.searchByTaskLog(searchCriteria);

  }
}
