package com.cnctor.hls.domain.service.chain;

import java.util.List;
import com.cnctor.hls.domain.model.Chain;
import com.cnctor.hls.domain.repository.chain.ChainSearchCriteria;

public interface ChainService {
  long countById(Long chainId);
  List<Chain> getAllChain();
  long countBySearchCriteria(ChainSearchCriteria searchCriteria);
  Chain createChain(Chain chain, long[] storeIds);
  Chain findChain(long chainId);
  Chain updateChain(Chain chain, long[] addStoreIds, long[] delStoreIds);
  List<Chain> searchCriteria(ChainSearchCriteria searchCriteria);
}
