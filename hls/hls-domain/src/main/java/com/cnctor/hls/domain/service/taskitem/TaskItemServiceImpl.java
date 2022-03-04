package com.cnctor.hls.domain.service.taskitem;

import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.repository.taskitem.TaskItemRepository;

@Service
public class TaskItemServiceImpl implements TaskItemService {
  
  @Inject
  TaskItemRepository repository;

  @Override
  public void upsert(long taskId, long itemId, String itemJsonValue) {
    repository.upsert(taskId, itemId, itemJsonValue);
  }
}
