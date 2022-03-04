package com.cnctor.hls.app.login;

import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Commons;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.common.mail.MailService;
import com.cnctor.hls.domain.common.mail.ResetPassService;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.usertoken.UserTokenService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class LoginRestController {

  @Autowired
  AuthenticationManager myAuthenticationManager;

  @Autowired
  JwtTokenProvider tokenProvider;

  @Inject
  Mapper beanMapper;

  @Inject
  AccountService accountService;

  @Inject
  MailService mailService;

  @Inject
  UserTokenService userTokenService;
  
  @Inject
  ResetPassService resetMailService;

  @PostMapping("/login")
  public @ResponseBody HlsResponse authenticateUser(@Valid @RequestBody LoginForm loginRequest) {

    try {
      Authentication authentication =
          myAuthenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              loginRequest.getMail(), loginRequest.getPassword()));
      SecurityContextHolder.getContext().setAuthentication(authentication);

      String jwt = tokenProvider.generateToken((AccountUserDetails) authentication.getPrincipal());
      Map<String, String> retToken = new HashMap<String, String>();
      retToken.put("token", jwt);
      return HlsResponse.SUCCESS(retToken);
    } catch (UsernameNotFoundException une) {
      une.printStackTrace();
      log.info("User not found  :", une.getMessage());
      return new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-LOGIN-POST-USERNOTFOUND");
      
    } catch (LockedException le) {
      le.printStackTrace();
      log.info("User is inactive  :", le.getMessage());
      return new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-LOGIN-POST-USERINACTIVE");
    } catch (Exception e) {
      e.printStackTrace();
      log.info("Login failed :", e.getMessage());
      return new HlsResponse(HlsResponse.AUTHEN_FAILED, "ERROR-LOGIN-POST-LOGINFAILED");
    }

  }

  @PostMapping("/resetpass")
  public @ResponseBody HlsResponse resetPassword(@RequestBody LoginForm loginRequest)
      throws Exception {
    log.info("[DEBUG API resetPassword] - {} ", loginRequest.getResetmail());

    String userMail = loginRequest.getResetmail();
    
    // email invalid
    if(!Commons.isEmailValid(userMail)) {
      return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-RESETMAIL-POST-EMAIL-INVALID");
    }
    
    Account account = accountService.findByMail(userMail);

    // email not exist
    if (account == null)
      return new HlsResponse(HlsResponse.UNAVAILABLE, "ERROR-RESETMAIL-POST-EMAIL-NOT-EXIST");
    
    // send mail reset
    resetMailService.doSendMail(account);
    Map<String, String> res = new HashMap<String, String>();
    res.put("mail", account.getMail());
    
    return HlsResponse.SUCCESS(res);
  }

  @GetMapping("/isexpired_token")
  public @ResponseBody HlsResponse isExpiredToken(@RequestParam String token) {
    Map<String, Boolean> retVal = new HashMap<String, Boolean>();
    retVal.put("isValid", userTokenService.tokenIsValid(token));
    return HlsResponse.SUCCESS(retVal);
  }

  @PutMapping("/account/password")
  public @ResponseBody HlsResponse changePassword(@RequestParam("token") String token, @RequestBody LoginForm loginRequest) {
    log.info("[DEBUG API RESET PASS] - {}", loginRequest.getNewPassword());

    if (userTokenService.tokenIsValid(token)) {
      accountService.changePassword(loginRequest.getNewPassword(), token);
      return HlsResponse.SUCCESS();
    } else {
      return HlsResponse.BADREQUEST();
    }
  }
  
  /*
   * chain user switch to store account 
   */
  @PostMapping("/stores/{id}/login")
  public @ResponseBody HlsResponse storeLogin(HttpServletRequest request, @PathVariable("id") long storeId) {
    if(request.isUserInRole(Constants.ROLE_CHAIN)) {
      
      Account storeAccount = accountService.fetchByStore(storeId);
      if(storeAccount == null)
        return new HlsResponse(HlsResponse.NOTFOUND, "ERROR-LOGINSTORE-NOTFOUND-STOREUSER"); 
      
      String jwt = tokenProvider.generateToken(storeAccount.getMail());
      Map<String, String> retToken = new HashMap<String, String>();
      retToken.put("token", jwt);
      return HlsResponse.SUCCESS(retToken);
    }
    return new HlsResponse(HlsResponse.FORBIDDEN, "ERROR-LOGINSTORE-NOTHAS-ROLECHAIN");
  }
}
