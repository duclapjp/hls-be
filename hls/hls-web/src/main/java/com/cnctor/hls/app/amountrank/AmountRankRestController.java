package com.cnctor.hls.app.amountrank;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.AmountRank;
import com.cnctor.hls.domain.service.amountrank.AmountRankService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AmountRankRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  AmountRankService amountRankService;
  
  @Inject
  AmountRankHelper amountRankHelper;

  @Inject
  StoreService storeService;

  @GetMapping("/amountranks")
  public @ResponseBody HlsResponse findAmountRanks(HttpServletRequest request,
      @RequestParam(required = false, defaultValue = "false") boolean enable) {
    log.info("[DEBUG API Get List Amount Rank] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account == null || account.getStoreId() == null)
        return HlsResponse.FORBIDDEN();
      
      List<AmountRank> amountRank = amountRankService.findAmountRanks(account.getStoreId(), enable);
      AmountRankResultResponse response = new AmountRankResultResponse(amountRank, amountRank.size());
      return HlsResponse.SUCCESS(response);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PostMapping("/amountranks")
  public @ResponseBody HlsResponse doSave(HttpServletRequest request, @RequestBody AmountRankForm form) {
    
    log.info("[DEBUG Save List Amount Rank - {} ]" );
    
    if (form == null)
      return HlsResponse.BADREQUEST();
    
    if(request.isUserInRole(Constants.ROLE_STORE)) {
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      Account account = userDetails.getAccount();
      if (account == null || account.getStoreId() == null)
        return HlsResponse.FORBIDDEN();
      AmountRank[] amountRanks = amountRankHelper.doSave(form, account);
      List<AmountRank> result = Arrays.asList(amountRanks);
      AmountRankResultResponse response = new AmountRankResultResponse(result, result.size());
      return HlsResponse.SUCCESS(response);
      
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

}
