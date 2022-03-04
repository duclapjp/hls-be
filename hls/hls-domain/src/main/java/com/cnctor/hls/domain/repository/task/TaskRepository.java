package com.cnctor.hls.domain.repository.task;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.model.TaskResult;
import com.cnctor.hls.domain.model.TaskStore;

public interface TaskRepository {
  List<TaskResult> getTask();
  long countBySearchCriteria(@Param("criteria") TaskSearchCriteria searchCriteria);
  List<TaskResult> searchCriteria(@Param("criteria") TaskSearchCriteria searchCriteria);
  Task findOne(long taskId);
  void update(Task task);
  void insert(Task task);
  void insertTaskStore(TaskStore taskStore);
  List<TaskResult> findChildTasks(@Param("taskId")long taskId, @Param("criteria") TaskSearchCriteria searchCriteria);
  List<Task> getTaskByParentId(long parentId);
  void delete(long taskId);
  long countByTaskLog( @Param("criteria") TaskSearchCriteria searchCriteria);
  List<TaskResult> searchByTaskLog( @Param("criteria") TaskSearchCriteria searchCriteria);
}
