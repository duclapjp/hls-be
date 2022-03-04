package com.cnctor.hls.domain.service.selectstore;

import java.util.List;
import com.cnctor.hls.domain.model.SelectStore;
import com.cnctor.hls.domain.repository.selectstore.SelectStoreSearchCriteria;

public interface SelectStoreService {
  long countSelectStoreBySearchCriteria(SelectStoreSearchCriteria searchCriteria);
  List<SelectStore> searchSelectStoreCriteria(SelectStoreSearchCriteria searchCriteria);
}
