package com.cnctor.hls.domain.service.plan;

import java.util.List;
import com.cnctor.hls.domain.model.Plan;
import com.cnctor.hls.domain.repository.plan.PlanSearchCriteria;

public interface PlanService {
  Plan createPlan(Plan plan);
  Plan findOne(long planId);
  long countBySearchCriteria(PlanSearchCriteria searchCriteria);
  List<Plan> searchCriteria(PlanSearchCriteria searchCriteria);
  void updateStatus(Plan plan);
  long getMaxCOrder(long storeId);
  Long getCOrderAtPostion(long postion, long storeId);
  Plan update(Plan plan);
  Plan upsert(Plan plan);
}
