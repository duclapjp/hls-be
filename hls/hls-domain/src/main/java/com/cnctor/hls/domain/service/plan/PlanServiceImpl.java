package com.cnctor.hls.domain.service.plan;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.Plan;
import com.cnctor.hls.domain.repository.plan.PlanRepository;
import com.cnctor.hls.domain.repository.plan.PlanSearchCriteria;

@Service
public class PlanServiceImpl implements PlanService {

  @Inject
  PlanRepository repository;
  
  @Override
  public long countBySearchCriteria(PlanSearchCriteria searchCriteria) {
    return repository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<Plan> searchCriteria(PlanSearchCriteria searchCriteria) {
    return repository.filter(searchCriteria);
  }

  @Override
  public Plan findOne(long planId) {
    return repository.findOne(planId);
  }

  @Override
  public Plan createPlan(Plan plan) {
    repository.insert(plan);
    return plan;
  }

  @Override
  public void updateStatus(Plan plan) {
    repository.updateStatus(plan);
  }

  @Override
  public long getMaxCOrder(long storeId) {
    Long maxCOrder = repository.getMaxCOrder(storeId);
    return maxCOrder == null ? 0L : maxCOrder;
  }

  @Override
  public Long getCOrderAtPostion(long postion, long storeId) {
    return repository.getCOrderAtPostion(postion, storeId);
  }

  @Override
  public Plan update(Plan plan) {
    repository.update(plan);
    return plan;
  }

  @Override
  public Plan upsert(Plan plan) {
    repository.upsert(plan);
    return plan;
  }
}
