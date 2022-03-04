package com.cnctor.hls.app.passwordhistory;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.account.AccountResultResponse;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.PasswordHistory;
import com.cnctor.hls.domain.repository.passwordhistory.PasswordHistorySearchCriteria;
import com.cnctor.hls.domain.service.passwordhistory.PasswordHistoryService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class PasswordHistoryRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  PasswordHistoryService passwordHistoryService;

  @PostMapping("/passwordhistories")
  public @ResponseBody HlsResponse searchPasswordHistory(HttpServletRequest request,
      @RequestBody PasswordHistoryForm form) {
    log.info("[DEBUG API searchPasswordHistory] : {}");

    if (form == null || form.getStoreId() == 0L) {
      return HlsResponse.BADREQUEST();
    }

    PasswordHistorySearchCriteria searchCriteria =
        beanMapper.map(form, PasswordHistorySearchCriteria.class);

    long total = passwordHistoryService.countBySearchPasswordHistory(searchCriteria);
    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<PasswordHistory> list = passwordHistoryService.searchPasswordHistory(searchCriteria);
      PasswordHistoryResultResponse response = new PasswordHistoryResultResponse(list, total);
      return HlsResponse.SUCCESS(response);
    }
  }

  @PostMapping("/otas")
  public @ResponseBody HlsResponse getListOTA(HttpServletRequest request,
      @RequestBody(required = false) OTAForm form ) {
    log.info("[DEBUG API getListOTA] : {}");
    Long storeId = null;
    if (form != null && form.getStoreId() != null)
      storeId = form.getStoreId();
    
    List<OTA> otas = passwordHistoryService.getOTAByStore(storeId);
    int total = otas == null ? 0 : otas.size();
    OTAResultResponse response = new OTAResultResponse(otas, total);
    return HlsResponse.SUCCESS(response);
  }
  
  @PostMapping("/otaaccounts")
  public @ResponseBody HlsResponse getListAccountOTAPasswordHistory(HttpServletRequest request,
      @RequestBody(required = false) OTAForm form ) {
    log.info("[DEBUG API get Account Changed Password of OTA] : {}");
      try {
        Long storeId = null;
        if (form != null && form.getStoreId() != null)
          storeId = form.getStoreId();
        
        List<Account> accounts = passwordHistoryService.getAccountOTAPasswordHistory(storeId);
        int total = accounts == null ? 0 : accounts.size();
        AccountResultResponse response = new AccountResultResponse(accounts, total);
        return HlsResponse.SUCCESS(response);
      } catch (Exception e) {
        return HlsResponse.BADREQUEST();
      }
  }
}
