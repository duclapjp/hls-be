package com.cnctor.hls.domain.repository.store;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.model.Store;

public interface StoreRepository {
  long countById(@Param("storeId")Long storeId);
  List<Store> getAllStore(@Param("filterSQL") String filterSQL);
  List<Store> findByChainId(@Param("chainId") Long chainId);
  void updateChain(@Param("storeIds") long[] storeIds, @Param("chainId") long chainId);
  void removeChain(@Param("delStoreIds") long[] delStoreIds, @Param("chainId") long chainId);
  void insert(Store store);
  long countBySearchCriteria(@Param("criteria") StoreSearchCriteria searchCriteria);
  List<Store> searchCriteria(@Param("criteria")StoreSearchCriteria searchCriteria);
  Store findOne(long storeId);
  List<Director> getAllDirector();
  void update(Store store);
  void updateStoreByChainStatus(@Param("chainId")long chainId, @Param("contractStatus") String contractStatus);
  List<Map<String, String>> storeMapByChainId(@Param("chainId") Long chainId);
}
