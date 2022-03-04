package com.cnctor.hls.domain.repository.taskattach;

import java.util.List;
import com.cnctor.hls.domain.model.TaskAttach;

public interface TaskAttachRepository {
  void insert(TaskAttach taskAttach);
  void update(TaskAttach taskAttach);
  TaskAttach findOne(long taskAttachId);
  void updateTaskId(TaskAttach taskAttach);
  void delete(TaskAttach taskAttach);
  
  List<TaskAttach> getByParentId(long parentId);
}
