package com.cnctor.hls.app.amountrank;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.dozer.Mapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AmountRank;
import com.cnctor.hls.domain.service.amountrank.AmountRankService;

@Component
public class AmountRankHelper {

  @Inject
  Mapper beanMapper;

  @Inject
  AmountRankService amountRankService;


  @Transactional(rollbackFor = Exception.class)
  public AmountRank[] doSave(AmountRankForm form, Account account) {
    AmountRank[] amountRanks = form.getAmountRanks();
    if (amountRanks != null && amountRanks.length > 0) {
      List<Long> amountRankIds = new ArrayList<Long>();
      int amountRankNo = 1;
      for (int i=0; i< amountRanks.length; i++) {
        AmountRank amountRank = amountRanks[i];
        amountRank.setAmountRankNo(amountRankNo++);
        amountRank.setStoreId(account.getStoreId());
        amountRank.setAccountId(account.getAccountId());
        if (amountRank.getAmountRankId() != null) {
          amountRankService.doUpdate(amountRank);
        } else {
          amountRankService.doCreate(amountRank);
        }
        amountRankIds.add(amountRank.getAmountRankId());
        amountRanks[i] = amountRank;
      }
      amountRankService.doDelete(account.getStoreId(), amountRankIds);
    } else {
      amountRankService.doDelete(account.getStoreId(), null);
    }
    return amountRanks;
  }


}
