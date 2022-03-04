package com.cnctor.hls.domain.service.tasklog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.common.utils.Constants;
import com.cnctor.hls.domain.model.AdminTaskLog;
import com.cnctor.hls.domain.model.TaskLog;
import com.cnctor.hls.domain.model.UserTaskSummary;
import com.cnctor.hls.domain.repository.tasklog.AdminTaskLogSearchCriteria;
import com.cnctor.hls.domain.repository.tasklog.TaskLogRepository;
import com.cnctor.hls.domain.repository.tasklog.TaskLogSearchCriteria;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummaryRepository;

@Service
public class TaskLogServiceImpl implements TaskLogService {

  @Inject
  TaskLogRepository repository;
  
  @Inject
  UserTaskSummaryRepository utsRepository;
  
  @Override
  public TaskLog createTaskLog(TaskLog taskLog) {
    repository.insert(taskLog);
    return taskLog;
  }

  @Override
  public long countTaskLog(long taskId) {
    return repository.countTaskLog(taskId);
  }

  @Override
  public List<TaskLog> searchTaskLog(long taskId, TaskLogSearchCriteria searchCriteria) {
    return repository.searchTaskLog(taskId, searchCriteria);
  }

  @Override
  public TaskLog findTaskLog(long taskLogId) {
    return repository.findOne(taskLogId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateTaskLog(TaskLog taskLog, Date editedTime) throws Exception {
    
    List<TaskLog> tobeUpdate = new ArrayList<TaskLog>();
    
    Date oldTime = taskLog.getTaskLogDate();
    // get different time of old time with edited time
    long diffTime = editedTime.getTime() - oldTime.getTime();
    
    // update all tasklog time related : accumulation_time
    List<TaskLog> taskLogAfters = repository.fetchTaskLogAfter(taskLog);
    
    // check editedTime < tasklog before startdate
    List<TaskLog> beforeTaskLogs  = repository.getBeforeByAccount(taskLog);
    if(editedTime.before(beforeTaskLogs.get(0).getTaskLogDate())) {
      throw new Exception("ERROR-UPDATETASKLOG-DATETIME-INVALIDRANGE");
    }
    
    if(taskLogAfters != null && taskLogAfters.size() > 0) {
      
      // check editedTime greater than next tasklog start date
      TaskLog nextTaskLog = taskLogAfters.get(0);
      
      if(editedTime.after(nextTaskLog.getTaskLogDate())) {
        throw new Exception("ERROR-UPDATETASKLOG-DATETIME-INVALIDRANGE");
      }
      
      for (TaskLog tLog : taskLogAfters) {
        tLog.setAccumulationTime(tLog.getAccumulationTime() + diffTime);
        tobeUpdate.add(tLog);
      }
    }
    
    // update current tasklog : date time, execute_time, accumulation_time
    taskLog.setTaskLogDate(editedTime);
    
    if(Constants.TASKLOG_TYPE_CONF.equals(taskLog.getType())) {
      taskLog.setConfirmTime(taskLog.getConfirmTime() + diffTime);
    } else {
      taskLog.setExecuteTime(taskLog.getExecuteTime() + diffTime);
    }
    
    taskLog.setAccumulationTime(taskLog.getAccumulationTime() + diffTime);
    
    tobeUpdate.add(taskLog);
    
    repository.batchUpdate(tobeUpdate);
    
    // update summary tasklog time , latest action_time if need 
    UserTaskSummary uts = utsRepository.findOne(taskLog.getTaskId(), taskLog.getAccountId());
    uts.setSummaryTime(uts.getSummaryTime() + diffTime);
    
    if(oldTime.equals(uts.getLatestActionTime())) {
      uts.setLatestActionTime(editedTime);
    }
    
    utsRepository.update(uts);
  }

  @Override
  public TaskLog getLatestByAccount(long taskId, long accountId) {
    return repository.getLatestByAccount(taskId, accountId);
  }

  @Override
  public Map<String, Long> sumTaskLogTime(List<Long> taskIds) {
    return repository.sumTaskLogTime(taskIds);
  }

  @Override
  public List<AdminTaskLog> getAdminTaskLogReport(AdminTaskLogSearchCriteria searchCriteria) {
    return repository.getAdminTaskLogReport(searchCriteria);
  }

  @Override
  public void updateStopTaskLog(long taskLogId) {
    repository.updateStopTaskLog(taskLogId);
  }

  @Override
  public List<TaskLog> findTaskNotStopYet() {
    return repository.findTaskNotStopYet();
  }
}
