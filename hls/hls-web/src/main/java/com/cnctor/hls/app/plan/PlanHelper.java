package com.cnctor.hls.app.plan;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.domain.common.utils.DateUtils;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Plan;
import com.cnctor.hls.domain.service.plan.PlanService;
import com.cnctor.hls.domain.service.planitem.PlanItemService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;

@Component
public class PlanHelper {
  
  @Inject
  PlanService pService;
  
  @Inject
  PlanItemService piService;
  
  @Inject
  Mapper beanMapper;

  @Transactional(rollbackFor = Exception.class)
  public Plan doCreate(PlanForm planForm){
    
    Plan plan = beanMapper.map(planForm, Plan.class);
    plan.setCreatedDate(Calendar.getInstance().getTime());
    
    AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    plan.setStoreId(account.getStoreId());
    plan.setCOrder(getNewCOrder(account.getStoreId()));
    plan.setCreatedAccountId(account.getAccountId());
    
    plan = pService.createPlan(plan);
    
    // create plan item
    List<ItemForm> items = planForm.getItems();
    long planId = plan.getPlanId();
    
    if(items != null && items.size() > 0) {
      int itemOrder = 1;
      for (ItemForm iForm : items) {
        piService.upsert(planId, iForm.getItemId(), iForm.getItemJsonValue(), StringUtils.EMPTY, itemOrder);
        itemOrder++;
      }
    }
    
    return plan;
  }
  
  @Transactional(rollbackFor = Exception.class)
  public Plan doUpdate(Plan plan, PlanForm planForm){
    
    plan.setName(planForm.getName());
    plan.setStatus(planForm.getStatus());
    
    plan.setUpdatedDate(DateUtils.currentDate());
    
    pService.update(plan);
    
    // update plan_item
    long planId = plan.getPlanId();

    // update plan item
    List<ItemForm> items = planForm.getItems();
    
    if(items != null && items.size() > 0) {
      int itemOrder = 1;
      for (ItemForm iForm : items) {
        piService.upsert(planId, iForm.getItemId(), iForm.getItemJsonValue(), StringUtils.EMPTY, itemOrder);
        itemOrder++;
      }
    }
    
    /*
    List<PlanItem> oldItems = plan.getItems();
    long[] newItemIds = planForm.getItemIds();
    
    // add new item : itemId in newItemIds not in oldItems --> insert
    for (long newId : newItemIds) {
      
      boolean isExist = false;
      for (PlanItem planItem : oldItems) {
        if(newId == planItem.getItemId()) {
          isExist = true;
        }
      }
      
      if(!isExist) {
        piService.insert(planId, newId);
      }
    }
    
    // delete item no in newItemIds
    List<Long> delItemIds = new ArrayList<Long>();
    for (PlanItem planItem : oldItems) {
      if(!ArrayUtils.contains(newItemIds, planItem.getItemId())) {
        delItemIds.add(planItem.getItemId());
      }
    }
    
    if(delItemIds.size() >0 ) {
      piService.delete(delItemIds, planId);
    }
    
    pService.update(plan);
    
    */
    
    //plan = pService.findOne(planId);
    return plan;
  }
  
  /**
   * do validate user input
   * @param planForm
   * @return
   */
  public String doValidate(PlanForm planForm) {
    String msg = StringUtils.EMPTY;
    if(! Arrays.asList(Constants.PLAN_STATUS_LIST).contains(planForm.getStatus())) {
      msg =  "ERROR-PLAN-STATUS-INVALID";
    }
    
    return msg;
  }
  
  /**
   * Store user only can has permission for its Plan
   * planId null --> create Plan
   * planId not null --> edit / update status
   * @param request
   * @return
   */
  public boolean hasPlanPermission(HttpServletRequest request, Long planId) {
    
    if(!request.isUserInRole(Constants.ROLE_STORE)) {
      return false;
    }
    
    AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    
    if(planId == null) {
      return true;
    } else {
      Plan plan = pService.findOne(planId);
      if(plan.isDefaultPlan()) {
        return true;
      }
        
      if(plan != null && plan.getStoreId().equals(account.getStoreId())) {
        return true;
      }
    }
    return false;
  }
  
  
  /**
   * get max c_order and increase by C_ORDER_INC
   * @return
   */
  public long getNewCOrder(long storeId) {
    long maxCOrder = pService.getMaxCOrder(storeId);
    return maxCOrder + C_ORDER_INCR;
  }
  
  private final int C_ORDER_INCR = 1;
  
  public void doReorder(PlanReorderForm[] planForms) {
    
    int cOrderIdx = 1;
    for (PlanReorderForm pForm : planForms) {
      Plan plan = pService.findOne(pForm.getPlanId());
      plan.setCOrder(Long.valueOf(cOrderIdx));
      
      // change status --> set updated time
      if(!StringUtils.equalsIgnoreCase(plan.getStatus(), pForm.getStatus())) {
        plan.setUpdatedDate(DateUtils.currentDate());
      }
      plan.setStatus(pForm.getStatus());
      
      pService.update(plan);
      
      cOrderIdx ++;
    }
  }
}
