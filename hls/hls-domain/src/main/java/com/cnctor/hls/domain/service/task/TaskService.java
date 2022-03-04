package com.cnctor.hls.domain.service.task;

import java.util.List;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.model.TaskResult;
import com.cnctor.hls.domain.model.TaskStore;
import com.cnctor.hls.domain.repository.task.TaskSearchCriteria;

public interface TaskService {
  List<TaskResult> getTasks();
  long countBySearchCriteria(TaskSearchCriteria searchCriteria);
  List<TaskResult> searchCriteria(TaskSearchCriteria searchCriteria);
  Task findTask(long taskId);
  Task createTask(Task task);
  Task updateTask(Task task);
  TaskStore createTaskStore(TaskStore ts);
  List<TaskResult> findChildTasks(long taskId, TaskSearchCriteria searchCriteria);
  List<Task> getTaskByParentId(long parentId);
  Task updateWithParentInfo(Task childTask, Task parentTask);
  void delete(Task task);
  Task duplicateToChild(Task parentTask, long storeId) throws Exception;
  Task duplicateToParent(Task srcTask, Long storeId) throws Exception;
  long countByTaskLog(TaskSearchCriteria searchCriteria);
  List<TaskResult> searchByTaskLog(TaskSearchCriteria searchCriteria);
}
