package com.cnctor.hls.domain.service.userdetails;

import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.cnctor.hls.domain.model.Account;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountUserDetails implements UserDetails{

  private static final long serialVersionUID = 1L;

  private final Account account;
  private final Collection<GrantedAuthority> authorities;
  
  public AccountUserDetails(Account account, Collection<GrantedAuthority> authorities) {
    this.account = account;
    this.authorities = authorities;
  }
  
  public Account getAccount() {
    return this.account;
  }
  
  public String getAccountId() {
    log.info("[DEBUG AccountUserDetails] {}", this.account.getAccountId());
    return this.account.getAccountId()+"";
  }
  
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return this.account.getPassword();
  }

  @Override
  public String getUsername() {
    return this.getAccount().getMail();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return StringUtils.equalsIgnoreCase(this.account.getStatus(), "利用中");
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
