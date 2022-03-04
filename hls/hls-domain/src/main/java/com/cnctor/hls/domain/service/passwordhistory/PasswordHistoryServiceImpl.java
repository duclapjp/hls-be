package com.cnctor.hls.domain.service.passwordhistory;

import java.util.List;
import javax.inject.Inject;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.PasswordHistory;
import com.cnctor.hls.domain.repository.passwordhistory.PasswordHistoryRepository;
import com.cnctor.hls.domain.repository.passwordhistory.PasswordHistorySearchCriteria;

@Service
public class PasswordHistoryServiceImpl implements PasswordHistoryService {

  @Inject
  PasswordHistoryRepository passwordHistoryRepository;
  

  @Override
  public List<PasswordHistory> searchPasswordHistory(PasswordHistorySearchCriteria searchCriteria) {
    return passwordHistoryRepository.searchPasswordHistory(searchCriteria);
  }

  @Override
  public long countBySearchPasswordHistory(PasswordHistorySearchCriteria searchCriteria) {
    return passwordHistoryRepository.countBySearchPasswordHistory(searchCriteria);
  }


  @Override
  public List<OTA> getOTAByStore(Long storeId) {
    return passwordHistoryRepository.getOTAByStore(storeId);
  }

  @Override
  public List<Account> getAccountOTAPasswordHistory(Long storeId) {
    return passwordHistoryRepository.getAccountOTAPasswordHistory(storeId);
  }

  @Override
  public void insert(PasswordHistory passwordHistory) {
    passwordHistoryRepository.insert(passwordHistory);
  }
}
