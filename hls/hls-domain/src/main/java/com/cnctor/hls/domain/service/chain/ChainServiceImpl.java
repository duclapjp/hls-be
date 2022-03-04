package com.cnctor.hls.domain.service.chain;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.Chain;
import com.cnctor.hls.domain.repository.chain.ChainRepository;
import com.cnctor.hls.domain.repository.chain.ChainSearchCriteria;
import com.cnctor.hls.domain.repository.store.StoreRepository;

@Service
public class ChainServiceImpl implements ChainService {

  @Inject
  ChainRepository chainRepository;
  
  @Inject
  StoreRepository storeRepository;

  @Override
  public long countById(Long chainId) {
    return chainRepository.countById(chainId);
  }

  @Override
  public List<Chain> getAllChain() {
    return chainRepository.getAllChain();
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Chain createChain(Chain chain, long[] storeIds) {
    
    // insert chain 
    chainRepository.insert(chain);
    
    // update store chainId
    if(storeIds != null &&  storeIds.length > 0) {
      storeRepository.updateChain(storeIds, chain.getChainId());
    }
    
    return chain;
  }

  @Override
  public Chain findChain(long chainId) {
    return chainRepository.findOne(chainId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Chain updateChain(Chain chain, long[] addStoreIds, long[] delStoreIds) {
    
    // update chain 
    chainRepository.update(chain);
    
    // update store chainId
    if(addStoreIds != null &&  addStoreIds.length > 0) {
      storeRepository.updateChain(addStoreIds, chain.getChainId());
    }
    
    // remove store chainId
    if(delStoreIds != null &&  delStoreIds.length > 0) {
      storeRepository.removeChain(delStoreIds, chain.getChainId());
    }
    
    return chain;
  }

  @Override
  public long countBySearchCriteria(ChainSearchCriteria searchCriteria) {
    return chainRepository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<Chain> searchCriteria(ChainSearchCriteria searchCriteria) {
    return chainRepository.searchCriteria(searchCriteria);
  }
}
