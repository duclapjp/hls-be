package com.cnctor.hls.domain.repository.passwordhistory;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.PasswordHistory;

public interface PasswordHistoryRepository {
  long countBySearchPasswordHistory(@Param("criteria") PasswordHistorySearchCriteria searchCriteria);
  List<PasswordHistory> searchPasswordHistory(@Param("criteria") PasswordHistorySearchCriteria searchCriteria);
  List<OTA> getOTAByStore(@Param("storeId")Long storeId);
  void insert(PasswordHistory passwordHistory);
  List<Account> getAccountOTAPasswordHistory(@Param("storeId") Long storeId);
}
