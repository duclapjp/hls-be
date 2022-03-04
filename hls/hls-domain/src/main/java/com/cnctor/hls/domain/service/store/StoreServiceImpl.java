package com.cnctor.hls.domain.service.store;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.Chain;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.model.PasswordHistory;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.model.StoreOta;
import com.cnctor.hls.domain.repository.passwordhistory.PasswordHistoryRepository;
import com.cnctor.hls.domain.repository.store.StoreRepository;
import com.cnctor.hls.domain.repository.store.StoreSearchCriteria;
import com.cnctor.hls.domain.repository.storeota.StoreOtaRepository;
import com.cnctor.hls.domain.repository.storesitecontroller.StoreSiteControllerRepository;

@Service
public class StoreServiceImpl implements StoreService {

  @Inject
  StoreRepository storeRepository;
  
  @Inject
  StoreSiteControllerRepository sscRepository;
  
  @Inject
  StoreOtaRepository soRepository;
  
  @Inject
  PasswordHistoryRepository passHistoryRepository;
  
  @Override
  public long countById(Long storeId) {
    return storeRepository.countById(storeId);
  }

  @Override
  public List<Store> getAllStore(String filterQuery) {
    
    String filterSQL = buildFilterSQL(filterQuery);
    return storeRepository.getAllStore(filterSQL);
  }

  @Override
  public List<Store> findByChainId(Long chainId) {
    return storeRepository.findByChainId(chainId);
  }

  @Override
  public long countBySearchCriteria(StoreSearchCriteria searchCriteria) {
    return storeRepository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<Store> searchCriteria(StoreSearchCriteria searchCriteria) {
    return storeRepository.searchCriteria(searchCriteria);
  }

  @Override
  public Store createStore(Store store) {
    storeRepository.insert(store);
    return store;
  }
  
  @Override
  public List<Director> getAllDirector() {
    return storeRepository.getAllDirector();
  }

  @Override
  public Store findStore(long storeId) {
    return storeRepository.findOne(storeId);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Store updateStore(long accountId, Store store, List<StoreOta> otas) {
    
    // update store 
    storeRepository.update(store);
    
    // add OTA password history    
    // if change store ota pass --> insert OTA pass history 
    for (StoreOta newStoreOta : otas) {
      for (StoreOta oldStoreOta : store.getOtas()) {
        if(newStoreOta.getOtaId() == oldStoreOta.getOtaId()  
            && !StringUtils.equals(newStoreOta.getPassword(), oldStoreOta.getPassword())) {
          
          PasswordHistory ph = new PasswordHistory();
          ph.setOtaId(newStoreOta.getOtaId());
          ph.setAccountId(accountId);
          ph.setPassword(newStoreOta.getPassword());
          ph.setUpdatedTime(new Date());
          ph.setStoreId(store.getStoreId());
          
          passHistoryRepository.insert(ph);
          
        }
      }
    }
    
    // upsert store otas using procedure
    for (StoreOta so : otas) {
      soRepository.upsertProcedure(so);
    }
    
    // delete store site controllers
    List<StoreOta> delOtas = subtractOta(store.getOtas(), otas);
    
    if(CollectionUtils.isNotEmpty(delOtas))
      soRepository.delete(delOtas, store.getStoreId());
    
    store.setOtas(otas);
    
    return store;
  }
  
  // return a-b collections
  private List<StoreOta> subtractOta(List<StoreOta> a, List<StoreOta> b) {

    List<StoreOta> ret = new ArrayList<StoreOta>();
    for (StoreOta sa : a) {

      boolean isContain = false;
      for (StoreOta sb : b) {
        if (sa.getOtaId() == sb.getOtaId()) {
          isContain = true;
        }
      }

      if (!isContain) {
        ret.add(sa);
      }
    }

    return ret;
  }

  @Override
  public void updateStoreByChainStatus(Chain chain) {
    storeRepository.updateStoreByChainStatus(chain.getChainId(), chain.getContractStatus());
  }
  
  private String buildFilterSQL(String filterParam) {
    String filterSQL = "";
    filterParam = StringUtils.trim(filterParam);
    if(StringUtils.isNotBlank(filterParam)) {
      
      // split to all queries 
      String[] queries = StringUtils.split(filterParam, SEMICOLON);
      
      // build each query
      for (int i = 0; i < queries.length; i++) {
        
        String query = StringUtils.trim(queries[i]);
        if(i > 0) {
          filterSQL += " OR ";
        }
          
        String[] args = StringUtils.split(query, COLON);
        
        // filter by chain_id
        if("chain_id".equals(args[0])) {
          if("null".equals(args[1])) {
            filterSQL += " chain_id is NULL ";
          } else if(StringUtils.isNotBlank(args[1])){
            filterSQL += " chain_id =" + args[1] + " ";
          }
        }
        
      }
    }
    return filterSQL;
  }
  
  @Override
  public List<Map<String, String>> storeMapByChainId(Long chainId) {
    return storeRepository.storeMapByChainId(chainId);
  }
  
  private final String COLON = ":";
  private final String SEMICOLON = ",";
}
