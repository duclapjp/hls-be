package com.cnctor.hls.app.selectstore;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.SelectStore;
import com.cnctor.hls.domain.repository.selectstore.SelectStoreSearchCriteria;
import com.cnctor.hls.domain.service.selectstore.SelectStoreService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class SelectStoreRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  SelectStoreService selectStoreService;


  @GetMapping("/selectstores")
  public @ResponseBody HlsResponse selectStores(HttpServletRequest request,
      @RequestParam(defaultValue = "store_id") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    log.info("[DEBUG API Select Store] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN)) {
      SelectStoreSearchCriteria searchCriteria = new SelectStoreSearchCriteria();
      if (sortBy != null)
        searchCriteria.setSortBy(sortBy);
      searchCriteria.setSortByType(sortByType);
      boolean isChainRole = request.isUserInRole(Constants.ROLE_CHAIN);
      searchCriteria.setChainRole(isChainRole);
      if (isChainRole) {
        AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        Account account = userDetails.getAccount();
        if (account != null && account.getChainId() != null) {
          searchCriteria.setChainId(account.getChainId());
        }
      } else {
        searchCriteria.setChainId(null);
      }

      long total = selectStoreService.countSelectStoreBySearchCriteria(searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<SelectStore> stores = selectStoreService.searchSelectStoreCriteria(searchCriteria);
        SelectStoreResultResponse response = new SelectStoreResultResponse(stores, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

}
