package com.cnctor.hls.domain.repository.planitem;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PlanItemRepository {
  void insert(@Param("planId") long planId, @Param("itemId") long itemId);
  void delete(@Param("itemIds") List<Long> itemIds, @Param("planId") long planId);
  void upsert(@Param("planId") long planId, @Param("itemId") long itemId, 
      @Param("itemJsonValue") String itemJsonValue, @Param("tab") String tab, @Param("itemOrder") Integer itemOrder);
}
