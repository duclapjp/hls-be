package com.cnctor.hls.domain.service.userdetails;

import java.util.Collection;
import javax.inject.Inject;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.repository.account.AccountRepository;

public class AccountUserDetailsService implements UserDetailsService {

  @Inject
  AccountRepository accountRepository;

  @Override
  public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {

    Account account = accountRepository.findByMail(mail);
    if (account == null) {
      throw new UsernameNotFoundException(mail + " is not found.");
    }
    
    return new AccountUserDetails(account, getAuthorities(account));
  }

  private Collection<GrantedAuthority> getAuthorities(Account account) {
    if (account.isAdmin()) {
      return AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_ADMIN");
    } else if (account.isSubAdmin()) {
      return AuthorityUtils.createAuthorityList("ROLE_USER", "ROLE_SUBADMIN");
    } else if(account.isChain()){
      return AuthorityUtils.createAuthorityList("ROLE_CHAIN");
    } else if(account.isStore()){
      return AuthorityUtils.createAuthorityList("ROLE_STORE");
    } else {
      return AuthorityUtils.createAuthorityList("ROLE_USER");
    }
  }
}
