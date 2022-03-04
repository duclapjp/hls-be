package com.cnctor.hls.app.amountgroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.dozer.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.app.amountrank.AmountForm;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AmountGroup;
import com.cnctor.hls.domain.model.AmountGroupRank;
import com.cnctor.hls.domain.model.AmountRank;
import com.cnctor.hls.domain.service.amountgroup.AmountGroupService;
import com.cnctor.hls.domain.service.amountrank.AmountRankService;

@Component
public class AmountGroupHelper {

  @Inject
  Mapper beanMapper;

  @Inject
  AmountRankService amountRankService;
  
  @Inject
  AmountGroupService amountGroupService;


  @Transactional(rollbackFor = Exception.class)
  public AmountGroup[] doSaveListAmountGroup(AmountGroupForm form, Account account) {
    AmountGroup[] amountGroups = form.getAmountGroups();
    if (amountGroups != null && amountGroups.length > 0) {
      List<Long> amountGroupIds = new ArrayList<Long>();
      int amountGroupNo = 1;
      for (int i=0; i< amountGroups.length; i++) {
        AmountGroup amountGroup = amountGroups[i];
        amountGroup.setAmountGroupNo(amountGroupNo++);
        amountGroup.setStoreId(account.getStoreId());
        amountGroup.setAccountId(account.getAccountId());
        if (amountGroup.getAmountGroupId() != null) {
          amountGroupService.doUpdate(amountGroup);
        } else {
          amountGroupService.doCreate(amountGroup);
        }
        amountGroupIds.add(amountGroup.getAmountGroupId());
        amountGroups[i] = amountGroup;
      }
      amountGroupService.doDelete(account.getStoreId(), amountGroupIds);
    } else {
      amountGroupService.doDelete(account.getStoreId(), null);
    }
    return amountGroups;
  }

  @Transactional(rollbackFor = Exception.class)
  public AmountGroup doSave(AmountGroupRankForm form, Account account) {
    AmountGroup amountGroup = beanMapper.map(form, AmountGroup.class);
    
    amountGroup.setStoreId(account.getStoreId());
    amountGroup.setAccountId(account.getAccountId());
    if (amountGroup.getAmountGroupId() != null) {
      amountGroup.setAmountGroupNo(null);
      amountGroupService.doUpdate(amountGroup);
    } else {
      amountGroupService.doCreate(amountGroup);
    }
    // Map
    List<AmountGroupRank> amountGroupRanks = new ArrayList<AmountGroupRank>();
    
    //amountGroup.setAmountGroupRanks(amountGroupRanks );
    List<AmountForm> lst = form.getAmountGroupRanks();
    for (int i =0; i< lst.size(); i++) {
      AmountForm amountForm = lst.get(i);
      AmountGroupRank agr = new AmountGroupRank();
      agr.setAmountGroupId(amountGroup.getAmountGroupId());
      agr.setAmountRankId(amountForm.getAmountRankId());
      agr.setAmounts(Arrays.toString(amountForm.getAmounts()));
      amountGroupRanks.add(agr);
    }
    for (AmountGroupRank agr : amountGroupRanks) {
      amountGroupService.saveAmountGroupRank(agr);
    }
    
    
    return amountGroup;
  }

  public AmountGroupRankForm convert(AmountGroup amountGroup) {
    AmountGroupRankForm result = beanMapper.map(amountGroup, AmountGroupRankForm.class);
    
    List<AmountGroupRank> lst = amountGroup.getAmountGroupRanks();
    List<AmountForm> amountForms = new ArrayList<AmountForm>();

    for (int i =0; i< lst.size(); i++) {
      AmountGroupRank agr = lst.get(i);
      
      AmountForm amountForm = new AmountForm();
      amountForm.setAmountGroupId(agr.getAmountGroupId());
      amountForm.setAmountRankId(agr.getAmountRankId());
      amountForm.setAmountRankName(agr.getAmountRankName());
      
      String amounts = agr.getAmounts();
      long[] arr = Arrays.stream(amounts.substring(1, amounts.length()-1).split(","))
          .map(String::trim).mapToLong(Long::parseLong).toArray();

      amountForm.setAmounts(arr);
      amountForms.add(amountForm);
    }
    result.setAmountGroupRanks(amountForms);
    
    return result;
  }

  public String doValidate(AmountGroupRankForm form, Account account) {
    if (form == null || account == null) {
      return "ERROR";
    }
    int totalPeople = 0;
    if (form.getTotalPeople() != null) {
      totalPeople = form.getTotalPeople();
      if (totalPeople < MIN_TOTAL_PEOPLE || totalPeople > MAX_TOTAL_PEOPLE) {
        return  "ERROR-TOTAL-PEOPLE";
      }
    }
    List<AmountRank> amountRanks = amountRankService.findAmountRanks(account.getStoreId(), true);

    List<AmountForm> amountForms = form.getAmountGroupRanks();
    if (amountForms == null || amountRanks == null || amountForms.size() != amountRanks.size()) {
      return "ERROR-AMOUNT-GROUP-RANK";
    }
    
    // Check total amount for each     
    for (AmountForm amountForm : amountForms) {
      if (amountForm.getAmounts().length != totalPeople)
        return "ERROR-AMOUNT";
      if (!amountRanks.stream().anyMatch(c -> (c.getAmountRankId() != null && c.getAmountRankId().equals(amountForm.getAmountRankId()))))
        return "ERROR-AMOUNT-RANK-NOT-EXIST";
    }

    return null;
  }
  
  private final int MIN_TOTAL_PEOPLE = 1; 
  private final int MAX_TOTAL_PEOPLE = 10;

}
