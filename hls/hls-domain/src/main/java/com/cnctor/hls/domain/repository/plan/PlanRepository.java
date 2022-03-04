package com.cnctor.hls.domain.repository.plan;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Plan;

public interface PlanRepository {
  void insert(Plan plan);
  Plan findOne(long planId);
  void update(Plan plan);
  void updateStatus(Plan plan);
  long countBySearchCriteria(@Param("criteria") PlanSearchCriteria searchCriteria);
  List<Plan> filter(@Param("criteria") PlanSearchCriteria criteria);
  Long getMaxCOrder(@Param("storeId") long storeId);
  Long getCOrderAtPostion(@Param("postion") long postion, @Param("storeId") long storeId);
  void upsert(Plan plan);
}
