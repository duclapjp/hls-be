package com.cnctor.hls.domain.service.item;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.Item;
import com.cnctor.hls.domain.repository.item.ItemRepository;
import com.cnctor.hls.domain.repository.item.ItemSearchCriteria;

@Service
public class ItemServiceImpl implements ItemService {
  
  @Inject
  ItemRepository repository;
  
  @Override
  public long countBySearchCriteria(ItemSearchCriteria searchCriteria) {
    return repository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<Item> searchCriteria(ItemSearchCriteria searchCriteria) {
    return repository.filter(searchCriteria);
  }

  @Override
  public Item findByItemCode(String itemCode) {
    return repository.findByItemCode(itemCode);
  }

  @Override
  public Item findOne(long itemId) {
    return repository.findOne(itemId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void upsert(List<Item> items) {
    for (Item item : items) {
      repository.upsert(item);
    }
    /*
    for (Item item : items) {
      Item oldItem = repository.findByItemCode(item.getItemCode());
      if(oldItem == null) {
        repository.insert(item);
      } else {
        item.setItemId(oldItem.getItemId());
        repository.update(item);
      }
    }
    */
    //repository.deleteNotIn(items);
  }

  @Override
  public void delete(List<Item> items) {
    repository.delete(items);
  }
}