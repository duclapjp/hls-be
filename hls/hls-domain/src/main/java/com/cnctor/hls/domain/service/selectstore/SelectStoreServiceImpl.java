package com.cnctor.hls.domain.service.selectstore;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.SelectStore;
import com.cnctor.hls.domain.repository.selectstore.SelectStoreRepository;
import com.cnctor.hls.domain.repository.selectstore.SelectStoreSearchCriteria;

@Service
public class SelectStoreServiceImpl implements SelectStoreService {

  @Inject
  SelectStoreRepository storeRepository;
  
  @Override
  public long countSelectStoreBySearchCriteria(SelectStoreSearchCriteria searchCriteria) {
    return storeRepository.countSelectStoreBySearchCriteria(searchCriteria);
  }

  @Override
  public List<SelectStore> searchSelectStoreCriteria(SelectStoreSearchCriteria searchCriteria) {
    return storeRepository.searchSelectStoreCriteria(searchCriteria);
  }
  
}
