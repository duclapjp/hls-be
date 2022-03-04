package com.cnctor.hls.domain.service.amountgroup;

import java.util.List;
import com.cnctor.hls.domain.model.AmountGroup;
import com.cnctor.hls.domain.model.AmountGroupRank;

public interface AmountGroupService {

  List<AmountGroup> findAmountGroups(Long storeId);

  AmountGroup doUpdate(AmountGroup amountGroup);
  
  AmountGroup doCreate(AmountGroup amountGroup);

  void doDelete(Long storeId, List<Long> amountGroupIds);

  AmountGroup getById(Long amountGroupId);

  AmountGroupRank saveAmountGroupRank(AmountGroupRank agr);
 
}
