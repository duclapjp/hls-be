package com.cnctor.hls.domain.service.amountrank;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.AmountRank;
import com.cnctor.hls.domain.repository.amountrank.AmountRankRepository;

@Service
public class AmountRankServiceImpl implements AmountRankService {

  @Inject
  AmountRankRepository amountRankRepository;
  
  @Override
  public List<AmountRank> findAmountRanks(Long storeId, boolean enable) {
    return amountRankRepository.findAmountRanks(storeId, enable);
  }

  @Override
  public AmountRank doCreate(AmountRank amountRank) {
    amountRankRepository.insert(amountRank);
    return amountRank;
  }

  @Override
  public AmountRank doUpdate(AmountRank amountRank) {
    amountRankRepository.update(amountRank);
    return amountRank;
    
  }

  @Override
  public void doDelete(Long storeId, List<Long> amountRankIds) {
    amountRankRepository.delete(storeId, amountRankIds);
  }

}
