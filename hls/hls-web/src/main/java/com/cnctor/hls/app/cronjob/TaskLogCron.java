package com.cnctor.hls.app.cronjob;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.domain.common.utils.DateUtils;
import com.cnctor.hls.domain.model.TaskLog;
import com.cnctor.hls.domain.model.UserTaskSummary;
import com.cnctor.hls.domain.service.tasklog.TaskLogService;
import com.cnctor.hls.domain.service.usertasksummary.UserTaskSummaryService;
import lombok.extern.slf4j.Slf4j;

/**
 * Cron job run in 0h:00 JST to break tasklog 
 * @author Admin
 */

@EnableScheduling
@Component
@Slf4j
public class TaskLogCron {
  
  @Inject
  TaskLogService taskLogService;
  
  @Inject
  UserTaskSummaryService utsService;
  
  @Scheduled(cron = "0 1 0 * * ?")
  public void breakTaskLog() {
    
    Date now = new Date();
    log.info("[TaskLogCron] execute cron in time : {}", now);
    
    
    // get list taskLog START + not stopped yet
    List<TaskLog> taskLogs = taskLogService.findTaskNotStopYet();
    if(CollectionUtils.isNotEmpty(taskLogs)) {
    
      for (TaskLog taskLog : taskLogs) {
        Date tlDate = taskLog.getTaskLogDate();
        
        UserTaskSummary uts = utsService.findUserTaskSummary(taskLog.getTaskId(), taskLog.getAccountId());
        if(uts != null) {
          
          // create task log action = STOP in 23:59:59
          TaskLog stopTaskLog = SerializationUtils.clone(taskLog);
          Date endOfDay = DateUtils.getEndTimeOfDay(tlDate);
          stopTaskLog.setTaskLogDate(endOfDay);
          stopTaskLog.setAction(Constants.STOPWATCH_ACTION_STOP);
          
          // set new summary time of usertasksummary
          long duration = endOfDay.getTime() - uts.getLatestActionTime().getTime();
          uts.setSummaryTime(uts.getSummaryTime() + duration);
          
          // set execute or confirm time
          if(Constants.TASKLOG_TYPE_EXEC.equals(taskLog.getType())) {
            stopTaskLog.setExecuteTime(duration);
          } else {
            stopTaskLog.setConfirmTime(duration);
          }
          
          stopTaskLog.setAccumulationTime(uts.getSummaryTime());
          
          taskLogService.createTaskLog(stopTaskLog);
          
          // update task log to stopped
          taskLog.setStopped(true);
          taskLogService.updateStopTaskLog(taskLog.getTaskLogId());
          
          // create task log action = START in 00:00:01
          TaskLog startTaskLog = SerializationUtils.clone(taskLog);
          Date beginOfNewDay = DateUtils.getBeginTimeOfNewDay(tlDate);
          startTaskLog.setTaskLogDate(beginOfNewDay);
          startTaskLog.setAction(Constants.STOPWATCH_ACTION_START);
          
          taskLogService.createTaskLog(startTaskLog);
          
          // set new latest action, latest action time of usertasksummary
          uts.setLatestAction(Constants.STOPWATCH_ACTION_START);
          uts.setLatestActionTime(beginOfNewDay);
          
          // update UserTaskSummary
          utsService.upsert(uts);
        }
      }
    }
  }
}
