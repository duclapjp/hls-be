package com.cnctor.hls.app.account;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.store.DirectorResultResponse;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AccountDisplay;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.model.UserToken;
import com.cnctor.hls.domain.repository.account.AccountSearchCriteria;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import com.cnctor.hls.domain.service.usertoken.UserTokenService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AccountRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  AccountService accountService;

  @Inject
  AccountHelper helper;

  @Inject
  UserTokenService userTokenService;

  @PostMapping("/accounts")
  public @ResponseBody HlsResponse search(HttpServletRequest request,
      @RequestBody AccountForm accountRequest) {
    log.info("[DEBUG API GetListAccount] : {}", request.isUserInRole("ROLE_ADMIN"));

    String userRole = helper.getRole(request);
    if (userRole != null && !Constants.ROLE_USER.equals(userRole)
        && !Constants.ROLE_SUBADMIN.equals(userRole)) {
      // Add column return by role
      List<String> params = new ArrayList<String>();
      params.add("account_id");
      params.add("role");
      params.add("mail");
      params.add("display_name");
      params.add("phone");
      params.add("chain_id");
      params.add("status");
      if (request.isUserInRole(Constants.ROLE_STORE)) {
        params.add("store_id");
      } else if (request.isUserInRole(Constants.ROLE_ADMIN)) {
        params.add("store_id");
        params.add("note");
      }

      accountRequest.setParams(params);

      AccountSearchCriteria searchCriteria =
          beanMapper.map(accountRequest, AccountSearchCriteria.class);
      
      AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
          .getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      
      if (request.isUserInRole(Constants.ROLE_CHAIN)) {
        searchCriteria.setChainId(account.getChainId());
      } else if (request.isUserInRole(Constants.ROLE_STORE)) {
        searchCriteria.setStoreId(account.getStoreId());
      }

      searchCriteria.setUserRole(userRole);
      long total = accountService.countBySearchCriteria(searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<Account> accounts = accountService.searchCriteria(searchCriteria);
        AccountResultResponse response = new AccountResultResponse(accounts, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @RequestMapping(value = "account/{id}", method = RequestMethod.GET)
  public @ResponseBody HlsResponse getAccountById(@PathVariable("id") Long accountId,
      HttpServletRequest request) {
    Account account = accountService.findById(accountId);
    if (account == null) {
      return HlsResponse.NOTFOUND();
    } else {
      String userRole = helper.getRole(request);
      if (helper.getRoleValue(userRole) >= helper.getRoleValue(account.getRole()))
        return HlsResponse.SUCCESS(account);
      else
        return HlsResponse.FORBIDDEN(null, Constants.ACCOUNT_ROLE_EDIT_FORBIDDEN);
    }
  }

  @PostMapping("/account")
  public @ResponseBody HlsResponse createAccount(HttpServletRequest request,
      @RequestBody(required = false) AccountForm accountRequest) {
    log.info("[DEBUG API Create/Edit Account] : {}", accountRequest);
    String userRole = helper.getRole(request);

    if (accountRequest == null)
      return HlsResponse.BADREQUEST();

    if (userRole != null && !Constants.ROLE_USER.equals(userRole)
        && !Constants.ROLE_SUBADMIN.equals(userRole)) {
      // Check insert or update account
      boolean isInsert = accountRequest.getAccountId() == null;

      String message = helper.doValidate(accountRequest, isInsert);
      if (message != null) {
        return HlsResponse.BADREQUEST(null, message);
      }
      message = helper.checkHasPermission(userRole, accountRequest.getRole());
      if (message != null) {
        return HlsResponse.FORBIDDEN(null, message);
      }
      Account account = beanMapper.map(accountRequest, Account.class);
      Account saved = null;
      try {
        if (isInsert) {
          saved = helper.doCreate(account, request);
        } else {
          saved = helper.doUpdate(account);
        }
      } catch (Exception e) {
        return HlsResponse.SERVER_ERROR();
      }
      return HlsResponse.SUCCESS(saved);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @GetMapping("/currentuser")
  public @ResponseBody HlsResponse getCurrentUser(HttpServletRequest request) {

    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    log.info("[DEBUG accountsetting2 ] - {} - {}", userDetails.getAccount().getAccountId(),
        userDetails.getUsername());

    long accountId = userDetails.getAccount().getAccountId();
    Account account = accountService.findById(accountId);
    
    Long xStoreId = null;
    try {
      xStoreId = helper.fetchStoreIdFromRequest(request);
    } catch (Exception e) {
      return HlsResponse.FORBIDDEN(null, e.getMessage());
    }
    if(account.isChain() && xStoreId != null && userDetails.getAccount().getStoreId() == null) {
      return HlsResponse.FORBIDDEN(null, "ERROR-STORE-NOTHASPERMISSION");
    }

    return HlsResponse.SUCCESS(account);
  }


  @GetMapping("/account/confirm-email")
  public @ResponseBody HlsResponse confirmEmail(@RequestParam String token) {

    // token valid --> update account mail
    if (userTokenService.tokenIsValid(token)) {

      UserToken userToken = userTokenService.findByToken(token);
      Account account = accountService.findById(userToken.getUserId());
      if (account == null)
        return HlsResponse.BADREQUEST();

      // set new mail to account
      accountService.updateMail(account.getAccountId(), userToken.getActionValue());

      // inactive token change mail
      userTokenService.updateInactive(token);

      return HlsResponse.SUCCESS();
    } else {
      return HlsResponse.BADREQUEST();
    }
  }

  @GetMapping("/listdirectors")
  public @ResponseBody HlsResponse listDirector(HttpServletRequest request) {
    log.info("[DEBUG API GetListAccount] : {}", request.isUserInRole("ROLE_ADMIN"));

    List<Director> directors = accountService.listDirector();
    int total = directors == null ? 0 : directors.size();

    DirectorResultResponse response = new DirectorResultResponse(directors, total);
    return HlsResponse.SUCCESS(response);
  }

  @GetMapping("/listregisters")
  public @ResponseBody HlsResponse listRegister(HttpServletRequest request,
      @RequestParam(defaultValue = "display_name") String sortBy) {
    log.info("[DEBUG API GetListRegister] : {}", request.isUserInRole("ROLE_ADMIN"));

    List<AccountDisplay> list = accountService.listRegister(sortBy);
    int total = list == null ? 0 : list.size();

    AccountDisplayResultResponse response = new AccountDisplayResultResponse(list, total);
    return HlsResponse.SUCCESS(response);
  }

  @GetMapping("/listusers")
  public @ResponseBody HlsResponse listUsers(HttpServletRequest request,
      @RequestParam(defaultValue = "display_name") String sortBy) {
    log.info("[DEBUG API GetListUser] : {}", request.isUserInRole("ROLE_ADMIN"));

    List<AccountDisplay> list = accountService.listUser(sortBy);
    int total = list == null ? 0 : list.size();

    AccountDisplayResultResponse response = new AccountDisplayResultResponse(list, total);
    return HlsResponse.SUCCESS(response);
  }

  @GetMapping("/users")
  public @ResponseBody HlsResponse getListUsers(HttpServletRequest request,
      @RequestParam(required = false) String[] roles,
      @RequestParam(defaultValue = "display_name") String sortBy) {
    log.info("[DEBUG API GetListUser] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (roles == null || roles.length == 0)
      return HlsResponse.SUCCESS(null);
    List<AccountDisplay> list = accountService.getListUserByRole(roles, sortBy);
    int total = list == null ? 0 : list.size();

    AccountDisplayResultResponse response = new AccountDisplayResultResponse(list, total);
    return HlsResponse.SUCCESS(response);
  }
}
