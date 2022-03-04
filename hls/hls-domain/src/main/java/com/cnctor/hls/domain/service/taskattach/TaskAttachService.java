package com.cnctor.hls.domain.service.taskattach;

import java.io.IOException;
import java.util.List;
import com.cnctor.hls.domain.model.TaskAttach;

public interface TaskAttachService {
  TaskAttach attachFile(Long taskId, String attachFileName, byte[] fileDatas) throws IOException ;
  TaskAttach findTaskAttach(long taskAttachId);
  void updateTaskId(TaskAttach ta);
  TaskAttach duplicateFile(TaskAttach attach) throws Exception;
  boolean delete(TaskAttach taskAttach);
  List<TaskAttach> getByParentId(long parentId);
  void createAttachFile(Long taskId, Long[] taskAttachIds) throws Exception;
  List<TaskAttach> createParentAttachFile(Long taskId, Long[] taskAttachIds) throws Exception;
}
