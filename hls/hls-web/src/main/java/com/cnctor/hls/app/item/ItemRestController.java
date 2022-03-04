package com.cnctor.hls.app.item;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.app.utils.SearchResultResponse;
import com.cnctor.hls.domain.model.Item;
import com.cnctor.hls.domain.repository.item.ItemSearchCriteria;
import com.cnctor.hls.domain.service.item.ItemService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class ItemRestController {
  
  @Inject
  ItemService itemService;
  
  @GetMapping("/items")
  public @ResponseBody HlsResponse getListItem(HttpServletRequest request,
      @RequestParam(defaultValue = "0") int size, @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "item_id") String sortBy, @RequestParam(defaultValue = "0") Long sortByType,
      @RequestParam(required = false, name="name") String name, @RequestParam(required = false, name="itemCode") String itemCode,
      @RequestParam(required = false, name="type") String type
      ) {
    
    
    log.info("[DEBUG API getListItem]");
    
    
    ItemSearchCriteria criteria = new ItemSearchCriteria();
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    
    criteria.setSortByType(sortByType);
    criteria.setSize(size);
    criteria.setPage(page);
    
    criteria.setName(name);
    criteria.setItemCode(itemCode);
    criteria.setType(type);
    
    long total = itemService.countBySearchCriteria(criteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<Item> items = itemService.searchCriteria(criteria);
      SearchResultResponse response = new SearchResultResponse(items, total);
      return HlsResponse.SUCCESS(response);
    }
  }
}
