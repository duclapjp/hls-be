package com.cnctor.hls.domain.service.planitem;

import java.util.List;

public interface PlanItemService {
  void insert(long planId, long itemId);
  void delete(List<Long> itemIds, long planId);
  void upsert(long planId, long itemId, String itemJsonValue, String tab, Integer itemOrder);
}
