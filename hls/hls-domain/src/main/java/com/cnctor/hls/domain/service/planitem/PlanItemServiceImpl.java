package com.cnctor.hls.domain.service.planitem;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.repository.planitem.PlanItemRepository;

@Service
public class PlanItemServiceImpl implements PlanItemService{

  @Inject
  PlanItemRepository repository;
  
  @Override
  public void insert(long planId, long itemId) {
    repository.insert(planId, itemId);
  }

  @Override
  public void delete(List<Long> itemIds, long planId) {
    repository.delete(itemIds, planId);
  }

  @Override
  public void upsert(long planId, long itemId, String itemJsonValue, String tab, Integer itemOrder) {
    repository.upsert(planId, itemId, itemJsonValue, tab, itemOrder);    
  }
}
