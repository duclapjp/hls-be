package com.cnctor.hls.app.accountsetting;

import javax.inject.Inject;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AccountSettingRestController {
  
  @Inject
  AccountService accountService;
  
  @Inject
  AccountSettingHelper helper;
	
  @GetMapping("/accountsetting")
  public @ResponseBody HlsResponse getAccountSetting(){
	  
	  AccountUserDetails userDetails = (AccountUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  log.info("[DEBUG accountsetting2 ] - {} - {}", userDetails.getAccount().getAccountId(), userDetails.getUsername());
	  
	  long accountId = userDetails.getAccount().getAccountId();
	  Account account = accountService.findById(accountId);
	  
	  // first login -> update to false
	  if(account.isFirstLogin()) {
	    account.setFirstLogin(false);
	    accountService.update(account);
	  }
	  
    return HlsResponse.SUCCESS(account);
  }
  
  @PostMapping("/accountsetting")
  public @ResponseBody HlsResponse updateAccountSetting(@RequestBody AccountSettingForm accSettingForm){
	  
	  AccountUserDetails userDetails = (AccountUserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	  log.info("[DEBUG accountsetting2 ] - {} - {}", userDetails.getAccount().getAccountId(), userDetails.getUsername());
	  
	  long accountId = userDetails.getAccount().getAccountId();
	  Account account = accountService.findById(accountId);	  
	  
	  String newPassword = accSettingForm == null ? "" : accSettingForm.getPassword();
	  	  
	  boolean isValid = helper.checkValidPassword(newPassword);
	  if (!isValid) {
	      return HlsResponse.BADREQUEST(null, "Password is invalid");
	  }
	  
	  account = helper.accountMapper(account, accSettingForm);
	  
	  if (account == null) {
	    return HlsResponse.FORBIDDEN();
	  } else {
	    account = accountService.updateAccountSetting(account);
	  }	  
	  /*
	  if (account != null && newPassword != null && newPassword.length() >=6) {
	    helper.sendPasswordToEmail(newPassword, account);
	  }
	  */
	    
      return HlsResponse.SUCCESS(account);
  }
}
