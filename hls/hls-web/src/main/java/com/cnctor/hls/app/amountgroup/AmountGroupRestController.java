package com.cnctor.hls.app.amountgroup;

import java.util.Arrays;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AmountGroup;
import com.cnctor.hls.domain.service.amountgroup.AmountGroupService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AmountGroupRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  AmountGroupService amountGroupService;
  
  @Inject
  AmountGroupHelper amountGroupHelper;


  @GetMapping("/amountgroups")
  public @ResponseBody HlsResponse findAmountGroups(HttpServletRequest request) {
    log.info("[DEBUG API Get List Amount Group] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account == null || account.getStoreId() == null)
        return HlsResponse.FORBIDDEN();
      
      List<AmountGroup> amountGroup = amountGroupService.findAmountGroups(account.getStoreId());
      AmountGroupResultResponse response = new AmountGroupResultResponse(amountGroup, amountGroup.size());
      return HlsResponse.SUCCESS(response);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PostMapping("/amountgroups")
  public @ResponseBody HlsResponse doSaveListAmountGroup(HttpServletRequest request, @RequestBody AmountGroupForm form) {
    
    log.info("[DEBUG Save List Amount Group - {} ]" );
    
    if (form == null)
      return HlsResponse.BADREQUEST();
    
    if(request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account == null || account.getStoreId() == null)
        return HlsResponse.FORBIDDEN();
      AmountGroup[] amountGroups = amountGroupHelper.doSaveListAmountGroup(form, account);
      List<AmountGroup> result = Arrays.asList(amountGroups);
      AmountGroupResultResponse response = new AmountGroupResultResponse(result, result.size());
      return HlsResponse.SUCCESS(response);
      
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping("/amountgroups/{id}")
  public @ResponseBody HlsResponse getAmountGroup(HttpServletRequest request,
      @PathVariable("id") Long amountGroupId) {
    
    if (amountGroupId == null)
      return HlsResponse.BADREQUEST();

    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();

    if (request.isUserInRole(Constants.ROLE_STORE) && account.getStoreId() != null) {
      log.info("[DEBUG getAmountGroup] - {}", amountGroupId);

      AmountGroup amountGroup = amountGroupService.getById(amountGroupId);
      AmountGroupRankForm data = amountGroupHelper.convert(amountGroup);
      
      if (data != null && account.getStoreId().equals(data.getStoreId())) {
        return HlsResponse.SUCCESS(data);
      } else {
        return HlsResponse.NOTFOUND();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PostMapping("/amountgroup")
  public @ResponseBody HlsResponse doSave(HttpServletRequest request, @RequestBody AmountGroupRankForm form) {
    
    log.info("[DEBUG Save Amount Group - {} ]" );
    
    if (form == null)
      return HlsResponse.BADREQUEST();
    
    if(request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account == null || account.getStoreId() == null)
        return HlsResponse.FORBIDDEN();
      //AmountGroup amountGroup = beanMapper.map(form, AmountGroup.class);
      String msg = amountGroupHelper.doValidate(form, account);

      if (msg != null) {
        return HlsResponse.BADREQUEST(null, msg);
      }
      
      AmountGroup amountGroup = amountGroupHelper.doSave(form, account);
      
      if (amountGroup != null && amountGroup.getAmountGroupId() != null) {
        form.setAmountGroupId(amountGroup.getAmountGroupId());
      }
      return HlsResponse.SUCCESS(form);
      
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

}
