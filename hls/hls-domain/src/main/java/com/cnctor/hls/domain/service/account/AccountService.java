package com.cnctor.hls.domain.service.account;

import java.util.List;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AccountDisplay;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.repository.account.AccountSearchCriteria;

public interface AccountService {
  Account create(Account account, String rawPassword);
  Account findByUserName(String userName);
  long countByUsername(String userName);
  long countByEmail(String email);
  long countBySearchCriteria(AccountSearchCriteria searchCriteria);
  Account findById(long accountId);
  List<Account> searchCriteria(AccountSearchCriteria searchCriteria);
  List<Account> findByChainId(Long chainId);
  List<Director> listDirector();
  List<AccountDisplay> listRegister(String sortBy);
  List<AccountDisplay> listUser(String sortBy);
  Account changePassword(String rawPassword, String token);
  Account findByMail(String mail);
  Account update(Account account);
  Account updateAccountSetting(Account account);
  void updateMail(long accountId, String mail);
  Account fetchByStore(long storeId);
  List<AccountDisplay> getByIds(long[] ids);
  List<AccountDisplay> getListUserByRole(String[] roles, String sortBy);
  List<Long> getUserIdsByChainOrStore(Long chainId, Long storeId);
  List<AccountDisplay> getUserMentions(Long taskId, Long storeId, String sortBy, Long type);
}
