package com.cnctor.hls.app.accountsetting;

import javax.inject.Inject;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.cnctor.hls.domain.model.Account;

@Component
public class AccountSettingHelper {
  @Inject
  PasswordEncoder passwordEncoder;

  public Account accountMapper(Account account, AccountSettingForm accSettingForm) {
    if (account == null || accSettingForm == null)
      return null;
    account.setDisplayName(accSettingForm.getDisplayName());
    account.setPhone(accSettingForm.getPhone());
    if (accSettingForm.getPassword() != null && accSettingForm.getPassword().length() >= 6) {
      String password = passwordEncoder.encode(accSettingForm.getPassword());
      account.setPassword(password);
    }
    account.setNotiDest(accSettingForm.getNotiDest());

    account.setMailSetting(accSettingForm.getMailSetting());
    account.setSlackSetting(accSettingForm.getSlackSetting());
    account.setLineSetting(accSettingForm.getLineSetting());
    account.setChatworkSetting(accSettingForm.getChatworkSetting());
    account.setViberRakutenSetting(accSettingForm.getViberRakutenSetting());

    return account;
  }

  public boolean checkValidPassword(String password) {
    if (password == null || password.length() == 0 || password.length() >= 6) {
      return true;
    }
    return false;
  }

  public void sendPasswordToEmail(String newPassword, Account account) {
    
  }


}
