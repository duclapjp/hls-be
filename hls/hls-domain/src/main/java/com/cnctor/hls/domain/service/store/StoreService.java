package com.cnctor.hls.domain.service.store;

import java.util.List;
import java.util.Map;
import com.cnctor.hls.domain.model.Chain;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.model.StoreOta;
import com.cnctor.hls.domain.repository.store.StoreSearchCriteria;

public interface StoreService {
  long countById(Long storeId);
  List<Store> getAllStore(String filterQuery);
  List<Store> findByChainId(Long chainId);
  long countBySearchCriteria(StoreSearchCriteria searchCriteria);
  List<Store> searchCriteria(StoreSearchCriteria searchCriteria);
  Store createStore(Store store);
  List<Director> getAllDirector();
  Store findStore(long storeId);
  Store updateStore(long accountId, Store store, List<StoreOta> otas);
  void updateStoreByChainStatus(Chain chain);
  List<Map<String, String>> storeMapByChainId(Long chainId);
}
