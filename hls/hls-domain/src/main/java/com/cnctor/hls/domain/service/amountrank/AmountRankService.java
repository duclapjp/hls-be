package com.cnctor.hls.domain.service.amountrank;

import java.util.List;
import com.cnctor.hls.domain.model.AmountRank;

public interface AmountRankService {

  List<AmountRank> findAmountRanks(Long storeId, boolean enable);

  AmountRank doUpdate(AmountRank amountRank);
  
  AmountRank doCreate(AmountRank amountRank);

  void doDelete(Long storeId, List<Long> amountRankIds);
 
}
