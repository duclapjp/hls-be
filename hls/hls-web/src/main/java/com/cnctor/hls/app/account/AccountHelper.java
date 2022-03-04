package com.cnctor.hls.app.account;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.app.utils.Commons;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.PasswordGenerator;
import com.cnctor.hls.domain.common.mail.Mail;
import com.cnctor.hls.domain.common.mail.MailService;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.UserToken;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.chain.ChainService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.usertoken.UserTokenService;
import freemarker.core.ParseException;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;

@Component
public class AccountHelper {
  private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_-]{3,20}$";

  @Inject
  AccountService accountService;

  @Inject
  ChainService chainService;

  @Inject
  StoreService storeService;

  @Value("${mail.template.accountcreated.name}")
  private String accountCreatedMailTemplate;

  @Value("${mail.template.accountcreated.subject}")
  private String accountCreatedMailSubject;

  @Value("${mail.template.accountcreated.from}")
  private String from;
  
  @Value("${mail.template.changemail.name}")
  private String changeMailConfirmTemplate;

  @Value("${mail.template.changemail.subject}")
  private String changeMailConfirmSubject;

  @Value("${mail.template.changemail.from}")
  private String changeMailConfirmFrom;
  
  @Value("${mail.template.changemail.baseurl}")
  private String confirmMailUrl;
  
  @Inject
  private MailService mailService;
  
  @Inject
  UserTokenService userTokenService;

  public boolean isRoleValid(final String role) {
    if (role != null && (role.equals(Constants.ROLE_ADMIN) || 
        role.equals(Constants.ROLE_SUBADMIN) || role.equals(Constants.ROLE_CHAIN)
        || role.equals(Constants.ROLE_STORE) || role.equals(Constants.ROLE_USER))) {
      return true;
    }
    return false;
  }

  public boolean isAccountStatusValid(final String status) {
    if (status != null && (status.equals(Constants.ACCOUNT_STATUS_ACTIVE)
        || status.equals(Constants.ACCOUNT_STATUS_INACTIVE) || status.length() == 0)) {
      return true;
    }
    return false;
  }

  public boolean isUsernameValid(final String username) {
    if (username == null)
      return false;
    Pattern pattern = Pattern.compile(USERNAME_PATTERN);
    Matcher matcher = pattern.matcher(username);
    return matcher.matches();
  }
  
  public boolean isPhoneValid(final String phone) {
    if (phone == null)
      return true;
    return (phone.matches("[0-9]+") && (phone.length() == 10 || phone.length() == 11));
  }

  public String checkExistEmail(String email) {
    if (accountService.countByEmail(email) > 0) {
      return "ERROR-EMAIL-IS-EXIST";
    }
    return null;
  }

  public String doValidate(AccountForm accountRequest, boolean isInsert) {
    if (accountRequest.getMail() == null || !Commons.isEmailValid(accountRequest.getMail())) {
      return "ERROR-EMAIL-INVALID";
    }
    if (isInsert) {
      String checkEmail = checkExistEmail(accountRequest.getMail());
      if (checkEmail != null)
        return checkEmail;
    } else {
      Account updateAccount = accountService.findById(accountRequest.getAccountId());
      if (updateAccount == null) {
        return "ERROR-ACCOUNT-IS-NOT-EXIST";
      } else {
        if (!accountRequest.getMail().equals(updateAccount.getMail())) {
          String checkEmail = checkExistEmail(accountRequest.getMail());
          if (checkEmail != null)
            return checkEmail;
        }
      }
    }
    if (accountRequest.getRole() != null && !isRoleValid(accountRequest.getRole())) {
      return "ERROR-ROLE-INVALID";
    }
    if (accountRequest.getStatus() != null && !isAccountStatusValid(accountRequest.getStatus())) {
      return "ERROR-ACCOUNT-STATUS-INVALID";
    }
    if (!isPhoneValid(accountRequest.getPhone())) {
      return "ERROR-PHONE-INVALID";
    }
    
    if (accountRequest.getChainId() != null
        && chainService.countById(accountRequest.getChainId()) == 0) {
      return "ERROR-CHAIN-IS-NOT-EXIST";
    }
    if (accountRequest.getStoreId() != null
        && storeService.countById(accountRequest.getStoreId()) == 0) {
      return "ERROR-STORE-IS-NOT-EXIST";
    }
    return null;
  }

  public String checkHasPermission(String userRole, String role) {
    if (getRoleValue(userRole) >= getRoleValue(role))
      return null;
    else
      return Constants.ACCOUNT_ROLE_EDIT_FORBIDDEN;
  }

  public List<Account> filterResultAccountByRole(HttpServletRequest request,
      List<Account> accounts) {
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      return accounts;
    }
    if (request.isUserInRole(Constants.ROLE_CHAIN)) {
      accounts.removeIf(a -> (Constants.ROLE_ADMIN.equals(a.getRole()) || 
          Constants.ROLE_SUBADMIN.equals(a.getRole())));
    }
    if (request.isUserInRole(Constants.ROLE_STORE)) {
      accounts.removeIf(a -> Constants.ROLE_ADMIN.equals(a.getRole())
          || Constants.ROLE_SUBADMIN.equals(a.getRole())
          || Constants.ROLE_CHAIN.equals(a.getRole()));
    }
    return accounts;
  }

  public String getRole(HttpServletRequest request) {
    if (request.isUserInRole(Constants.ROLE_ADMIN))
      return Constants.ROLE_ADMIN;
    if (request.isUserInRole(Constants.ROLE_SUBADMIN))
      return Constants.ROLE_SUBADMIN;
    if (request.isUserInRole(Constants.ROLE_CHAIN))
      return Constants.ROLE_CHAIN;
    if (request.isUserInRole(Constants.ROLE_STORE))
      return Constants.ROLE_STORE;
    if (request.isUserInRole(Constants.ROLE_USER))
      return Constants.ROLE_USER;
    return null;
  }

  public int getRoleValue(String role) {
    if (Constants.ROLE_ADMIN.equals(role) || Constants.ROLE_SUBADMIN.equals(role))
      return 4;
    if (Constants.ROLE_CHAIN.equals(role))
      return 3;
    if (Constants.ROLE_STORE.equals(role))
      return 2;
    return 1;
  }

  public void sendMail(Account account, String rawPassword)
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
      MessagingException, IOException, TemplateException {
    Mail mail = new Mail();

    mail.setFrom(from);
    //mail.setTo(account.getMail());
    mail.setTo(from);
    
    mail.setSubject(accountCreatedMailSubject);
    mail.setTemplate(accountCreatedMailTemplate);

    Map<String, Object> m = new HashMap<>();
    m.put("accountMail", account.getMail());
    m.put("password", rawPassword);

    mail.setModel(m);

    mailService.sendMail(mail);
  }
  
  public void sendConfirmMail(String token, String newMail)
      throws TemplateNotFoundException, MalformedTemplateNameException, ParseException,
      MessagingException, IOException, TemplateException {
    Mail mail = new Mail();

    mail.setFrom(changeMailConfirmFrom);
    mail.setTo(newMail);

    mail.setSubject(changeMailConfirmSubject);
    mail.setTemplate(changeMailConfirmTemplate);

    Map<String, Object> m = new HashMap<>();
    m.put("newEmail", newMail);
    m.put("confirmEmailUrl", confirmMailUrl + token);

    mail.setModel(m);

    mailService.sendMail(mail);
  }
  
  @Transactional(rollbackFor = Exception.class)
  public Account doCreate(Account account, HttpServletRequest request) throws Exception {
    String status = account.getStatus();
    if (status == null || status.length() == 0) {
      account.setStatus(Constants.ACCOUNT_STATUS_ACTIVE);
    }
    
    // set chain_id, store_id for new account
    AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Account creatorAccount = userDetails.getAccount();
    // chain user --> set chain_id to new user
    if(request.isUserInRole(Constants.ROLE_CHAIN)) {
      account.setChainId(creatorAccount.getChainId());
    }
    
    // store user --> set chain_id, store_id to new user
    if(request.isUserInRole(Constants.ROLE_STORE)) {
      account.setChainId(creatorAccount.getChainId());
      account.setStoreId(creatorAccount.getStoreId());
      account.setRole(Constants.ROLE_STORE);
    }
    
    String password = PasswordGenerator.generateRandomPassword(10);
    accountService.create(account, password);
    sendMail(account, password);
    return account;
  }
  
  @Transactional(rollbackFor = Exception.class)
  public Account doUpdate(Account newAccount) throws Exception {
    
    Account oldAccount = accountService.findById(newAccount.getAccountId());
    
    // change email --> send mail confirm
    if(!StringUtils.equalsIgnoreCase(oldAccount.getMail(), newAccount.getMail())) {
      
      // create token
      String token = UUID.randomUUID().toString();
      
      // add an active user_token for action CHANGE_MAIL
      userTokenService.insert(newAccount.getAccountId(), token, UserToken.CHANGE_MAIL, newAccount.getMail());
      
      // send mail
      sendConfirmMail(token, newAccount.getMail());
      
    }
    
    accountService.update(newAccount);
    return newAccount;
  }
  
  public Account getCurrentAccount(HttpServletRequest request) {
    AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
        .getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    return account;
  }
  
  public Long fetchStoreIdFromRequest(HttpServletRequest request) throws Exception {
    
    String xStoreId = request.getHeader(X_REQUESTED_STORE_ID);
    if (StringUtils.isNotBlank(xStoreId)){
      if( NumberUtils.isCreatable(xStoreId)) {
        return NumberUtils.createLong(xStoreId);
      } else {
        throw new Exception("ERROR-STOREID-ERRORFORMAT");
      }
    }
    return null;
  }
  
  private static final String X_REQUESTED_STORE_ID = "X-Requested-StoreId";
}
