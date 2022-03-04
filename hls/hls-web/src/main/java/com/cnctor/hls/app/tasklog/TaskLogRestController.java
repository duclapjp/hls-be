package com.cnctor.hls.app.tasklog;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.dozer.Mapper;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.task.TaskHelper;
import com.cnctor.hls.app.task.TaskLogResultResponse;
import com.cnctor.hls.app.task.TaskLogSearchForm;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.TaskLog;
import com.cnctor.hls.domain.repository.tasklog.AdminTaskLogSearchCriteria;
import com.cnctor.hls.domain.repository.tasklog.TaskLogSearchCriteria;
import com.cnctor.hls.domain.service.tasklog.TaskLogService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TaskLogRestController {
  
  @Inject
  Mapper beanMapper;
  
  @Inject
  TaskLogService taskLogService;
  
  @Inject
  TaskHelper taskHelper;
  
  @PostMapping("/tasks/{id}/tasklogs")
  public @ResponseBody HlsResponse getTaskLogs(HttpServletRequest request,
      @PathVariable("id") long taskId, @RequestBody TaskLogSearchForm taskLogRequest) {

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      TaskLogSearchCriteria searchCriteria = beanMapper.map(taskLogRequest, TaskLogSearchCriteria.class);

      long total = taskLogService.countTaskLog(taskId);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<TaskLog> tasks = taskLogService.searchTaskLog(taskId, searchCriteria);
        TaskLogResultResponse response = new TaskLogResultResponse(tasks, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  
  @GetMapping("/tasks/{id}/tasklogs/{taskLogId}")
  public @ResponseBody HlsResponse getTaskLogDetail(HttpServletRequest request, @PathVariable("id") long taskId, 
      @PathVariable("taskLogId") long taskLogId) {
    
    // only admin, subadmin can view task log detail
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      
      return HlsResponse.SUCCESS(taskLogService.findTaskLog(taskLogId));
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PutMapping("/tasks/{id}/tasklogs/{taskLogId}")
  public @ResponseBody HlsResponse updateTaskLogDetail(HttpServletRequest request, @PathVariable("id") long taskId, 
      @PathVariable("taskLogId") long taskLogId, @RequestBody Map<String, String> reqMap) {
    
    log.info("[DEBUG API updateTaskLogDetail] : {} ", reqMap.get("datetime"));
    
    // admin subadmin 
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      
      String dateTime = reqMap.get("datetime");
      DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
      
      TaskLog taskLog = taskLogService.findTaskLog(taskLogId);
      
      // not found 
      if(taskLog == null) {
        return HlsResponse.NOTFOUND();
      }
      
      // only edit tasklog STOP 
      if(! Constants.STOPWATCH_ACTION_STOP.equals(taskLog.getAction())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPDATETASKLOG-ONLYUPDATESTOPTASKLOG");
      }
      
      try {
        Date editedTime = df.parse(dateTime);
        taskLogService.updateTaskLog(taskLog, editedTime);
        return HlsResponse.SUCCESS(taskLog);
        
      } catch (ParseException e) {  // date invalid
        e.printStackTrace();
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPDATETASKLOG-DATE-INVALID"); 
      } catch (Exception e) {
        e.printStackTrace();
        return new HlsResponse(HlsResponse.BADREQUEST, e.getMessage()); 
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  
  @GetMapping("/tasks/{id}/tasklogs/latest")
  public @ResponseBody HlsResponse getTaskLogLatest(HttpServletRequest request, @PathVariable("id") long taskId) {
    
    if (taskHelper.hasViewTaskRole(request, taskId)) {
      
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      
      return HlsResponse.SUCCESS(taskLogService.getLatestByAccount(taskId, account.getAccountId()));
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping("/admin/tasklogs")
  public @ResponseBody HlsResponse getTaskLogReport(HttpServletRequest request,
      @RequestParam (required = false) @DateTimeFormat(pattern="yyyy/MM/dd") @Valid Date startDate,
      @RequestParam (required = false) @DateTimeFormat(pattern="yyyy/MM/dd") @Valid Date endDate,
      @RequestParam (required = false) String username,
      @RequestParam (required = false) String sortBy,
      @RequestParam(defaultValue = "1") Integer sortByType) {
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      if (sortBy != null && !sortBy.equals("display_name"))
        sortBy = null;
      
      AdminTaskLogSearchCriteria searchCriteria = new  AdminTaskLogSearchCriteria();
      searchCriteria.setStartDate(startDate);
      searchCriteria.setEndDate(endDate);
      searchCriteria.setUsername(username);
      searchCriteria.setSortBy(sortBy);
      searchCriteria.setSortByType(sortByType);

      return HlsResponse.SUCCESS(taskLogService.getAdminTaskLogReport(searchCriteria));
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
