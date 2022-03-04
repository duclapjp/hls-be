package com.cnctor.hls.domain.repository.account;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AccountDisplay;
import com.cnctor.hls.domain.model.Director;

public interface AccountRepository {
  void insert(Account account);
  Account findByUserName(@Param("username") String userName);
  long countByUsername(@Param("username")String userName);
  long countByEmail(@Param("email")String email);
  Account findById (@Param("accountId") long accountId);
  long countBySearchCriteria(@Param("criteria") AccountSearchCriteria searchCriteria);
  void update(Account account);
  void updateAccountSetting(Account account);
  List<Account> searchCriteria(@Param("criteria") AccountSearchCriteria searchCriteria);
  Account findByMail(@Param("mail") String mail);
  void updatePassword(Account account);
  void updateMail(@Param("accountId") long accountId, @Param("mail") String mail);
  Account fetchByStore(long storeId);
  List<Director> listDirector();
  List<AccountDisplay> listRegister(@Param("sortBy") String sortBy);
  List<AccountDisplay> listUser(@Param("sortBy") String sortBy);
  List<AccountDisplay> getByIds(@Param("ids") long[] ids);
  List<AccountDisplay> getListUserByRole(@Param("roles") String[] roles, @Param("sortBy") String sortBy);
  List<Account> findByChainId(@Param("chainId") Long chainId);
  List<Long> getUserIdsByChainOrStore(@Param("chainId") Long chainId, @Param("storeId") Long storeId);
  List<AccountDisplay> getUserMentions(@Param("taskId") Long taskId, @Param("storeId") Long storeId, 
      @Param("sortBy") String sortBy, @Param("type") Long type);

}
