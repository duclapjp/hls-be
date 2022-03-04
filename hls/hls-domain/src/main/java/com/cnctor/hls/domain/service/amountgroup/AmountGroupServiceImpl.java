package com.cnctor.hls.domain.service.amountgroup;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.AmountGroup;
import com.cnctor.hls.domain.model.AmountGroupRank;
import com.cnctor.hls.domain.repository.amountgroup.AmountGroupRepository;

@Service
public class AmountGroupServiceImpl implements AmountGroupService {

  @Inject
  AmountGroupRepository amountGroupRepository;
  
  @Override
  public List<AmountGroup> findAmountGroups(Long storeId) {
    return amountGroupRepository.findAmountGroups(storeId);
  }

  @Override
  public AmountGroup doCreate(AmountGroup amountGroup) {
    amountGroupRepository.insert(amountGroup);
    return amountGroup;
  }

  @Override
  public AmountGroup doUpdate(AmountGroup amountGroup) {
    amountGroupRepository.update(amountGroup);
    return amountGroup;
    
  }

  @Override
  public void doDelete(Long storeId, List<Long> amountGroupIds) {
    amountGroupRepository.delete(storeId, amountGroupIds);
  }

  @Override
  public AmountGroup getById(Long amountGroupId) {
    return amountGroupRepository.getById(amountGroupId);
  }

  @Override
  public AmountGroupRank saveAmountGroupRank(AmountGroupRank agr) {
    amountGroupRepository.upsertAmountGroupRank(agr);
    return agr;
  }

}
