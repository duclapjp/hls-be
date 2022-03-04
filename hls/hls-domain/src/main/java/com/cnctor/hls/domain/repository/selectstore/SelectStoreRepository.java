package com.cnctor.hls.domain.repository.selectstore;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.SelectStore;

public interface SelectStoreRepository {
  long countSelectStoreBySearchCriteria(@Param("criteria") SelectStoreSearchCriteria searchCriteria);
  List<SelectStore> searchSelectStoreCriteria(@Param("criteria") SelectStoreSearchCriteria searchCriteria);
}
