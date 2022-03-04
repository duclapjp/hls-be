package com.cnctor.hls.domain.repository.item;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Item;

public interface ItemRepository {
  long countBySearchCriteria(@Param("criteria") ItemSearchCriteria searchCriteria);
  List<Item> filter(@Param("criteria") ItemSearchCriteria criteria);
  Item findByItemCode(@Param("itemCode") String itemCode);
  Item findOne(@Param("itemId") long itemId);
  //void insert(Item item);
  void update(Item item);
  void deleteNotIn(@Param("items") List<Item> items);
  void upsert(Item item);
  void delete(@Param("items") List<Item> items);
}