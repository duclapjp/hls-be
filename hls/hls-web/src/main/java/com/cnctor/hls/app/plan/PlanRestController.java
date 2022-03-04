package com.cnctor.hls.app.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
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
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.app.utils.SearchResultResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Plan;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.repository.plan.PlanSearchCriteria;
import com.cnctor.hls.domain.service.plan.PlanService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class PlanRestController {
  
  @Inject
  PlanService planService;
  
  @Inject
  PlanHelper helper;
  
  @Inject
  StoreService storeService;
  
  @GetMapping("/plans")
  public @ResponseBody HlsResponse getListPlan(HttpServletRequest request,
      @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "c_order") String sortBy, @RequestParam(defaultValue = "0") Long sortByType,
      @RequestParam(required = false, name="name") String name, @RequestParam(required = false, name="status") String status
      ) {
    
    
    log.info("[DEBUG API getListPlan]");
    /*
    if(!helper.hasPlanPermission(request, null)) {
      return HlsResponse.FORBIDDEN();
    }
    */
    
    PlanSearchCriteria criteria = new PlanSearchCriteria();
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    
    criteria.setSortByType(sortByType);
    criteria.setSize(size);
    criteria.setPage(page);
    
    criteria.setName(name);
    criteria.setStatus(status);
    
    
    AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    
    List<Long> storeIds = new ArrayList<Long>();
    
    if(request.isUserInRole(Constants.ROLE_STORE)){
      storeIds.add(account.getStoreId());
    } else if(request.isUserInRole(Constants.ROLE_CHAIN)){
      List<Store> stores = storeService.findByChainId(account.getChainId());
      for (Store s : stores) {
        storeIds.add(s.getStoreId());
      }
      
    }
    criteria.setStoreIds(storeIds);
    
    
    long total = planService.countBySearchCriteria(criteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<Plan> plans = planService.searchCriteria(criteria);
      SearchResultResponse response = new SearchResultResponse(plans, total);
      return HlsResponse.SUCCESS(response);
    }
  }
  
  /**
   * show select box in create, edit Task
   * ADMIN, SUBADMIN : show all 13 default plan
   * CHAIN, STORE : show only 7
   * @param request
   * @return
   */
  
  @GetMapping("/categories")
  public @ResponseBody HlsResponse getListCategory(HttpServletRequest request) {
    
    log.info("[DEBUG API getListCategory]");

    PlanSearchCriteria criteria = new PlanSearchCriteria();
    
    // get default plan
    criteria.setDefaultPlan(true);
    
    if(request.isUserInRole(Constants.ROLE_STORE) || request.isUserInRole(Constants.ROLE_CHAIN)) {
      criteria.setAvailableFor(Constants.CHAIN_STORE_PLAN);
    }
    
    criteria.setSortBy("plan_id");
    criteria.setSortByType(0);
    
    long total = planService.countBySearchCriteria(criteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<Plan> plans = planService.searchCriteria(criteria);
      SearchResultResponse response = new SearchResultResponse(plans, total);
      return HlsResponse.SUCCESS(response);
    }
  }
  
  
  @PutMapping("/plans/{id}/status")
  public @ResponseBody HlsResponse updateStatus(HttpServletRequest request, @PathVariable("id") long planId, 
      @RequestBody Map<String, String> reqMap) {
    
    if(!helper.hasPlanPermission(request, null)) {
      return HlsResponse.FORBIDDEN();
    }
    
    String updateStatus = reqMap.get("status");
    log.info("[DEBUG update plan status ] - planId : {} - status : {}", planId, updateStatus);
    
    if(! Arrays.asList(Constants.PLAN_STATUS_LIST).contains(updateStatus)) {
      return HlsResponse.BADREQUEST(null, "ERROR-PLAN-STATUS-INVALID");
    }
    
    // get exist plan
    Plan plan = planService.findOne(planId);
    if (plan == null)
      return HlsResponse.NOTFOUND();
    
    try {
      plan.setStatus(updateStatus);
      planService.updateStatus(plan);
      return HlsResponse.SUCCESS(plan);
    } catch (Exception e) {
      log.error("Update ota error : {}", e.getMessage());
      return HlsResponse.SERVER_ERROR();
    }
  }
  
  
  @PostMapping("/plan")
  public @ResponseBody HlsResponse createPlan(HttpServletRequest request,
      @RequestBody(required = false) PlanForm planForm) {
    
    log.info("[DEBUG API Create Plan] : {}", planForm);
    
    if(!helper.hasPlanPermission(request, null)) {
      return HlsResponse.FORBIDDEN();
    }
    
    String errMsg = helper.doValidate(planForm);
    if(StringUtils.isNotBlank(errMsg)) {
      return HlsResponse.BADREQUEST(null, errMsg);
    }
    
    if (planForm == null)
      return HlsResponse.BADREQUEST();

    try {
      Plan plan = helper.doCreate(planForm);
      return HlsResponse.SUCCESS(plan);
      
    } catch (Exception e) {
      e.printStackTrace();
      log.error("[DEBUG API Create Plan] : Cannot create Plan with exception : {}", e);
      return HlsResponse.SERVER_ERROR();
    }
  }
  
  @PostMapping("/plans/reorder")
  public @ResponseBody HlsResponse reOrder(HttpServletRequest request,
      @RequestBody PlanReorderRequest reorderRequest) {
    
    log.info("[DEBUG API Re-order Plan] : {}", reorderRequest);
    
    if(!helper.hasPlanPermission(request, null)) {
      return HlsResponse.FORBIDDEN();
    }
    
    PlanReorderForm[] planForms = reorderRequest.getPlans();
    if(planForms == null || planForms.length == 0) {
      return HlsResponse.BADREQUEST();
    }
    
    helper.doReorder(planForms);
    
    return HlsResponse.SUCCESS(planForms);
  }
  
  @PutMapping("/plans/{id}")
  public @ResponseBody HlsResponse updatePlan(HttpServletRequest request,
      @PathVariable("id") long planId, @RequestBody PlanForm planForm) {
    
    log.info("[DEBUG API updatePlan] : {}  ---  {}", planId, planForm);

    if(!helper.hasPlanPermission(request, planId)) {
      return HlsResponse.FORBIDDEN();
    }
    
    String errMsg = helper.doValidate(planForm);
    if(StringUtils.isNotBlank(errMsg)) {
      return HlsResponse.BADREQUEST(null, errMsg);
    }
    
    if (planForm == null)
      return HlsResponse.BADREQUEST();

    
    Plan plan = planService.findOne(planId);
    
    if (plan == null)
      return HlsResponse.NOTFOUND();
    
    plan = helper.doUpdate(plan, planForm);
    
    return HlsResponse.SUCCESS(plan);
  }
  
  @GetMapping("/plans/{id}")
  public @ResponseBody HlsResponse getPlanDetail(HttpServletRequest request,
      @PathVariable("id") long planId) {

    if (helper.hasPlanPermission(request, planId)) {

      log.info("[DEBUG getPlanDetail] - {}", planId);
      
      Plan retPlan = planService.findOne(planId);
      if (retPlan == null) {
        return HlsResponse.NOTFOUND();
      }
     
      return HlsResponse.SUCCESS(retPlan);
      
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
