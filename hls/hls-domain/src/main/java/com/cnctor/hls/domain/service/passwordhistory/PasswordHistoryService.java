package com.cnctor.hls.domain.service.passwordhistory;

import java.util.List;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.PasswordHistory;
import com.cnctor.hls.domain.repository.passwordhistory.PasswordHistorySearchCriteria;

public interface PasswordHistoryService {
  long countBySearchPasswordHistory(PasswordHistorySearchCriteria searchCriteria);
  List<PasswordHistory> searchPasswordHistory(PasswordHistorySearchCriteria searchCriteria);
  List<OTA> getOTAByStore(Long storeId);
  List<Account> getAccountOTAPasswordHistory(Long storeId);
  void insert(PasswordHistory passwordHistory);
}
