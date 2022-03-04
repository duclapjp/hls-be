package com.cnctor.hls.app.dashboard;

import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.account.AccountHelper;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.PendingTask;
import com.cnctor.hls.domain.repository.dashboard.PendingTaskSearchCriteria;
import com.cnctor.hls.domain.repository.usertasksummary.StopwatchSearchCriteria;
import com.cnctor.hls.domain.service.dashboard.DashboardService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.usertasksummary.UserTaskSummaryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class DashboardRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  DashboardService dashboardService;

  @Inject
  AccountHelper accountHelper;
  
  @Inject
  UserTaskSummaryService utsService;
  
  @GetMapping("/dashboard/pendingtasks")
  public @ResponseBody HlsResponse getPendingTasks(HttpServletRequest request,
      @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "due_date") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    log.info("[DEBUG API GetPendingTasks] : {}", request.isUserInRole("ROLE_ADMIN"));


    PendingTaskSearchCriteria criteria = new PendingTaskSearchCriteria();
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    criteria.setSortByType(sortByType);
    criteria.setSize(size);
    criteria.setPage(page);
    criteria.setStatus(Constants.TASK_STATUS_COMPLETED);
    
    String userRole = accountHelper.getRole(request);
    criteria.setUserRole(accountHelper.getRoleValue(userRole));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      criteria.setChainId(null);
    } else if (request.isUserInRole(Constants.ROLE_CHAIN)
        || request.isUserInRole(Constants.ROLE_STORE) || request.isUserInRole(Constants.ROLE_USER)) {
      
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      
      /*
      if (account != null && account.getChainId() != null) {
        if (request.isUserInRole(Constants.ROLE_CHAIN)) {
          criteria.setChainId(account.getChainId());
          criteria.setStoreId(null);
        } else if (request.isUserInRole(Constants.ROLE_STORE)){
          criteria.setChainId(null);
          criteria.setStoreId(account.getStoreId());
        } else {
          criteria.setAssigneeId(account.getAccountId());
        }
      }
      */
      
      if(account == null) {
        return HlsResponse.NOTFOUND();
      }
      
      if(request.isUserInRole(Constants.ROLE_CHAIN) && account.getChainId() == null) {
        return HlsResponse.NOTFOUND();
      }
      
      if(request.isUserInRole(Constants.ROLE_STORE) && account.getStoreId() == null) {
        return HlsResponse.NOTFOUND();
      }
      
      if(request.isUserInRole(Constants.ROLE_CHAIN)) {
        criteria.setChainId(account.getChainId());
        criteria.setStoreId(null);
      } else if (request.isUserInRole(Constants.ROLE_STORE)){
        criteria.setChainId(null);
        criteria.setStoreId(account.getStoreId());
      } else {
        criteria.setAssigneeId(account.getAccountId());
      }
      
    } else {
      return HlsResponse.FORBIDDEN();
    }

    long total = dashboardService.countPendingTask(criteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<PendingTask> pendingTasks = dashboardService.getPendingTasks(criteria);
      PendingTaskResultResponse response = new PendingTaskResultResponse(pendingTasks, total);
      return HlsResponse.SUCCESS(response);
    }
  }
  
  @GetMapping("/dashboard/pendingtasks/{id}/childs")
  public @ResponseBody HlsResponse getChildPendingTasks(HttpServletRequest request,
      @PathVariable("id") long taskId, @RequestParam(defaultValue = "due_date") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    PendingTaskSearchCriteria criteria = new PendingTaskSearchCriteria();
    
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    criteria.setSortByType(sortByType);
    
    String userRole = accountHelper.getRole(request);
    criteria.setUserRole(accountHelper.getRoleValue(userRole));
    
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      criteria.setChainId(null);
    } else if (request.isUserInRole(Constants.ROLE_CHAIN)
        || request.isUserInRole(Constants.ROLE_STORE) || request.isUserInRole(Constants.ROLE_USER)) {
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account != null && account.getChainId() != null) {
        if (request.isUserInRole(Constants.ROLE_CHAIN)) {
          criteria.setChainId(account.getChainId());
          criteria.setStoreId(null);
        } else if (request.isUserInRole(Constants.ROLE_STORE)){
          criteria.setChainId(null);
          criteria.setStoreId(account.getStoreId());
        } else {
          criteria.setAssigneeId(account.getAccountId());
        }
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
    
    log.info("[DEBUG getChildPendingTask] - {}", taskId);
    List<PendingTask> childPendingTasks = dashboardService.findChildPendingTasks(taskId, criteria);
    if (childPendingTasks != null) {
      PendingTaskResultResponse response = new PendingTaskResultResponse(childPendingTasks, childPendingTasks.size());
      return HlsResponse.SUCCESS(response);
    } else {
      return HlsResponse.NOTFOUND();
    }
  }
  
  @GetMapping("/dashboard/stopwatchs")
  public @ResponseBody HlsResponse getStopwatchList(HttpServletRequest request,
      @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "due_date") String sortBy, 
      @RequestParam(defaultValue = "1") Long sortByType) {
    
    // check permission to view
    if(!(request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER))) {
      return HlsResponse.FORBIDDEN();
    }
    
    StopwatchSearchCriteria criteria = new StopwatchSearchCriteria();
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    criteria.setSortByType(sortByType);
    criteria.setSize(size);
    criteria.setPage(page);
    
    // user only can see it's task log
    if (Constants.ROLE_USER.equals(accountHelper.getRole(request))) {
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      
      criteria.setAccountId(account.getAccountId());
      
    } 
    
    long total = dashboardService.countStartStopwatch(criteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<Map<String, String>> stopwatchs = dashboardService.filterStartStopwatch(criteria);
      PendingTaskResultResponse response = new PendingTaskResultResponse(stopwatchs, total);
      return HlsResponse.SUCCESS(response);
    }
  }
}
