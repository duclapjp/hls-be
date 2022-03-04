package com.cnctor.hls.domain.service.account;

import java.util.List;
import javax.inject.Inject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AccountDisplay;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.model.UserToken;
import com.cnctor.hls.domain.repository.account.AccountRepository;
import com.cnctor.hls.domain.repository.account.AccountSearchCriteria;
import com.cnctor.hls.domain.repository.usertoken.UserTokenRepository;

@Service
public class AccountServiceImpl implements AccountService {

  @Inject
  AccountRepository accountRepository;

  @Inject
  PasswordEncoder passwordEncoder;

  @Inject
  UserTokenRepository userTokenRepository;

  @Override
  public Account create(Account account, String rawPassword) {
    String password = passwordEncoder.encode(rawPassword);
    account.setPassword(password);
    account.setFirstLogin(true);
    accountRepository.insert(account);
    return account;
  }

  @Override
  public Account findByUserName(String userName) {
    return accountRepository.findByUserName(userName);
  }

  @Override
  public long countByUsername(String userName) {
    return accountRepository.countByUsername(userName);
  }

  @Override
  public long countByEmail(String email) {
    return accountRepository.countByEmail(email);
  }

  @Override
  public Account findById(long accountId) {
    return accountRepository.findById(accountId);
  }

  @Override
  public Account changePassword(String rawPassword, String token) {

    UserToken userToken = userTokenRepository.findByToken(token);

    Account account = accountRepository.findById(userToken.getUserId());

    String password = passwordEncoder.encode(rawPassword);
    account.setPassword(password);
    accountRepository.updatePassword(account);

    // inactive url token change pass
    userTokenRepository.updateInactive(token);

    return account;
  }

  @Override
  public long countBySearchCriteria(AccountSearchCriteria searchCriteria) {
    return accountRepository.countBySearchCriteria(searchCriteria);
  }

  @Override
  public List<Account> searchCriteria(AccountSearchCriteria searchCriteria) {
    return accountRepository.searchCriteria(searchCriteria);
  }

  @Override
  public Account findByMail(String mail) {
    return accountRepository.findByMail(mail);
  }

  @Override
  public Account update(Account account) {
    accountRepository.update(account);
    return account;
  }

  @Override
  public Account updateAccountSetting(Account account) {
    accountRepository.updateAccountSetting(account);
    return account;
  }

  @Override
  public void updateMail(long accountId, String mail) {
    accountRepository.updateMail(accountId, mail);
  }

  @Override
  public Account fetchByStore(long storeId) {
    return accountRepository.fetchByStore(storeId);
  }

  @Override
  public List<Director> listDirector() {
    return accountRepository.listDirector();
  }

  @Override
  public List<AccountDisplay> listRegister(String sortBy) {
    return accountRepository.listRegister(sortBy);
  }

  @Override
  public List<AccountDisplay> listUser(String sortBy) {
    return accountRepository.listUser(sortBy);
  }

  @Override
  public List<AccountDisplay> getByIds(long[] ids) {
    return accountRepository.getByIds(ids);
  }

  @Override
  public List<AccountDisplay> getListUserByRole(String[] roles, String sortBy) {
    return accountRepository.getListUserByRole(roles, sortBy);
  }

  @Override
  public List<Account> findByChainId(Long chainId) {
    return accountRepository.findByChainId(chainId);
  }

  @Override
  public List<Long> getUserIdsByChainOrStore(Long chainId, Long storeId) {
    return accountRepository.getUserIdsByChainOrStore(chainId, storeId);
  }

  @Override
  public List<AccountDisplay> getUserMentions(Long taskId, Long storeId, String sortBy, Long type) {
    return accountRepository.getUserMentions(taskId, storeId, sortBy, type);
  }
}
