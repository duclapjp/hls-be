package com.cnctor.hls.app.chain;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Commons;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Chain;
import com.cnctor.hls.domain.repository.chain.ChainSearchCriteria;
import com.cnctor.hls.domain.service.chain.ChainService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class ChainRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  ChainService chainService;

  @Inject
  StoreService storeService;

  @PostMapping("/chains")
  public @ResponseBody HlsResponse getAllChain(HttpServletRequest request) {
    log.info("[DEBUG API GetListChain] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)
        || request.isUserInRole(Constants.ROLE_USER)) {

      List<Chain> chains = chainService.getAllChain();
      int total = chains == null ? 0 : chains.size();
      ChainResultResponse response = new ChainResultResponse(chains, total);
      return HlsResponse.SUCCESS(response);

    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PostMapping("/chains/search")
  public @ResponseBody HlsResponse searchChains(HttpServletRequest request,
      @RequestBody ChainForm chainRequest) {
    log.info("[DEBUG API Filter List Chain] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN)
        || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      ChainSearchCriteria searchCriteria = beanMapper.map(chainRequest, ChainSearchCriteria.class);

      long total = chainService.countBySearchCriteria(searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<Chain> chains = chainService.searchCriteria(searchCriteria);
        ChainResultResponse response = new ChainResultResponse(chains, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PostMapping("/chain")
  public @ResponseBody HlsResponse createChain(HttpServletRequest request,
      @RequestBody ChainForm chainForm) {
    if (request.isUserInRole(Constants.ROLE_ADMIN)
        || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      log.info("[DEBUG createChain] - {}", chainForm);

      // email invalid
      if (StringUtils.isNotBlank(chainForm.getManagerMail())
          && !Commons.isEmailValid(chainForm.getManagerMail())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-CREATECHAIN-EMAIL-INVALID");
      }

      // status invalid
      if(!Commons.statusIsValid(chainForm.getContractStatus())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-CREATECHAIN-CONTRACTSTATUS-INVALID");
      }


      Chain chain = beanMapper.map(chainForm, Chain.class);
      try {
        log.info("[DEBUG createChain] chain - {}", chain);
        chainService.createChain(chain, chainForm.getStoreIds());
      } catch (Exception e) {
        log.error("Create chain error : {}", e.getMessage());
        return HlsResponse.SERVER_ERROR();
      }
      return HlsResponse.SUCCESS(chain);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @GetMapping("/chains/{id}")
  public @ResponseBody HlsResponse getChain(HttpServletRequest request,
      @PathVariable("id") long chainId) {

    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();

    // has ROLE_ADMIN or ROLE_CHAIN can view its chain
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || (request.isUserInRole(Constants.ROLE_CHAIN) && account.getChainId() != null && chainId == account.getChainId())) {
      log.info("[DEBUG getChain] - {}", chainId);
      Chain retChain = chainService.findChain(chainId);
      if (retChain != null) {
        return HlsResponse.SUCCESS(retChain);
      } else {
        return HlsResponse.NOTFOUND();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PutMapping("/chains/{id}")
  public @ResponseBody HlsResponse updateChain(HttpServletRequest request,
      @PathVariable("id") long chainId, @RequestBody ChainForm chainForm) {

    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || (request.isUserInRole(Constants.ROLE_CHAIN) && chainId == account.getChainId())) {

      log.info("[DEBUG updateChain] - {}", chainId);

      // email invalid
      if (StringUtils.isNotBlank(chainForm.getManagerMail())
          && !Commons.isEmailValid(chainForm.getManagerMail())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPDATECHAIN-EMAIL-INVALID");
      }

      // get exist chain
      Chain chain = chainService.findChain(chainId);
      if (chain == null)
        return HlsResponse.NOTFOUND();
      
      String oldContractStatus = chain.getContractStatus();
      
      // update exist chain data from chainForm
      beanMapper.map(chainForm, chain, "chain_map_nonnull");

      // status invalid
      if(!Commons.statusIsValid(chain.getContractStatus())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPDATECHAIN-CONTRACTSTATUS-INVALID");
      }

      try {
        chainService.updateChain(chain, chainForm.getAddStoreIds(), chainForm.getDelStoreIds());
        
        // if chain contract status from 契約中  --> 解約    or  停止
        // all it's stores  contract status
        if(StringUtils.equals(oldContractStatus, Constants.CONTRACT_STATUS_ACTIVE) 
            && !StringUtils.equalsIgnoreCase(chain.getContractStatus(), Constants.CONTRACT_STATUS_ACTIVE)) {
          storeService.updateStoreByChainStatus(chain);
        }
        
        
        return HlsResponse.SUCCESS(chain);
      } catch (Exception e) {
        log.error("Update chain error : {}", e.getMessage());
        return HlsResponse.SERVER_ERROR();
      }

    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
