package com.cnctor.hls.domain.service.item;

import java.util.List;
import com.cnctor.hls.domain.model.Item;
import com.cnctor.hls.domain.repository.item.ItemSearchCriteria;

public interface ItemService {
  long countBySearchCriteria(ItemSearchCriteria searchCriteria);
  List<Item> searchCriteria(ItemSearchCriteria searchCriteria);
  Item findByItemCode(String itemCode);
  Item findOne(long itemId);
  void upsert(List<Item> items);
  void delete(List<Item> items);
}
