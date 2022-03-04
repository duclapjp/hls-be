package com.cnctor.hls.app.store;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
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
import com.cnctor.hls.app.notification.NotificationHelper;
import com.cnctor.hls.app.utils.Commons;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Account;
import com.cnctor.hls.domain.model.Director;
import com.cnctor.hls.domain.model.Notification;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.model.StoreOta;
import com.cnctor.hls.domain.repository.store.StoreSearchCriteria;
import com.cnctor.hls.domain.service.account.AccountService;
import com.cnctor.hls.domain.service.ota.OTAService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.storesota.StoreOtaService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class StoreRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  StoreService storeService;

  @Inject
  StoreHelper helper;
  
  @Inject
  NotificationHelper notificationHelper;
  
  @Inject
  AccountService accountService; 
  
  @Inject
  StoreOtaService soService;
  
  @Inject
  OTAService otaService;
  
  @PostMapping("/stores")
  public @ResponseBody HlsResponse getAllStore(HttpServletRequest request,
      @RequestBody(required = false) StoreSearchForm storeRequest) {
    log.info("[DEBUG API GetListStore] : {}", storeRequest);
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)
        || request.isUserInRole(Constants.ROLE_USER)) {
      
      Long chainId = (storeRequest == null ? null : storeRequest.getChainId());
      String filterQuery = storeRequest == null ? null : storeRequest.getFilter();
      
      if (chainId == null || chainId.equals(0L)) {
        List<Store> stores = storeService.getAllStore(filterQuery);
        int total = stores == null ? 0 : stores.size();
        StoreResultResponse response = new StoreResultResponse(stores, total);
        return HlsResponse.SUCCESS(response);
      } else {
        try {
          Long chainIdValue = Long.valueOf(chainId);
          List<Store> stores = storeService.findByChainId(chainIdValue);
          int total = stores == null ? 0 : stores.size();
          StoreResultResponse response = new StoreResultResponse(stores, total);
          return HlsResponse.SUCCESS(response);
        } catch (Exception e) {
          return HlsResponse.BADREQUEST();
        }
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PostMapping("/store")
  public @ResponseBody HlsResponse createStore(HttpServletRequest request, @RequestBody StoreForm storeForm) {
    log.info("[DEBUG create store - {} ]", storeForm );
    // if role ADMIN 
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN) ) {
      // email invalid
      if(StringUtils.isNotBlank(storeForm.getManagerMail()) &&  !Commons.isEmailValid(storeForm.getManagerMail())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-CREATESTORE-EMAIL-INVALID");
      }
      // status invalid
      if(!Commons.statusIsValid(storeForm.getContractStatus())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-CREATESTORE-CONTRACTSTATUS-INVALID");
      }
      
      Store retStore = helper.doCreate(storeForm);
      return HlsResponse.SUCCESS(retStore);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PostMapping("/store/search")
  public @ResponseBody HlsResponse searchStores(HttpServletRequest request,
      @RequestBody StoreSearchForm storeRequest) {
    log.info("[DEBUG API Search Store] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER)
        || request.isUserInRole(Constants.ROLE_CHAIN)) {
      StoreSearchCriteria searchCriteria = beanMapper.map(storeRequest, StoreSearchCriteria.class);
      boolean isChainRole = request.isUserInRole(Constants.ROLE_CHAIN);
      searchCriteria.setChainRole(isChainRole);
      if (isChainRole) {
        AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        Account account = userDetails.getAccount();
        if (account != null && account.getChainId() != null) {
          searchCriteria.setChainId(account.getChainId());
        }
        if (searchCriteria.getStoreId() != null) {
          searchCriteria.setSearchKeyword(null);
        }
      } else {
        searchCriteria.setStoreId(null);
      }

      long total = storeService.countBySearchCriteria(searchCriteria);

      if (total == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<Store> stores = storeService.searchCriteria(searchCriteria);
        StoreResultResponse response = new StoreResultResponse(stores, total);
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping("/stores/{id}")
  public @ResponseBody HlsResponse getStore(HttpServletRequest request, @PathVariable("id") long storeId) {

    // has ROLE_ADMIN or ROLE_CHAIN can view its chain
    if (hasViewEditStoreRole(request, storeId)) {
      
      log.info("[DEBUG getChain] - {}", storeId);
      Store retStore = storeService.findStore(storeId);
      if (retStore != null) {
        return HlsResponse.SUCCESS(retStore);
      } else {
        return HlsResponse.NOTFOUND();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PutMapping("/stores/{id}")
  public @ResponseBody HlsResponse updateStore(HttpServletRequest request,
      @PathVariable("id") long storeId, @RequestBody StoreForm storeForm) {

    if (hasViewEditStoreRole(request, storeId)) {
      log.info("[DEBUG updateStore] - {}", storeId);
      
      
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      long accountId = userDetails.getAccount().getAccountId();
      
      // email invalid
      if (StringUtils.isNotBlank(storeForm.getManagerMail())
          && !Commons.isEmailValid(storeForm.getManagerMail())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPDATESTORE-EMAIL-INVALID");
      }

      // get exist store
      Store store = storeService.findStore(storeId);
      if (store == null)
        return HlsResponse.NOTFOUND();

      boolean isPushNotification = helper.checkChangeOTAPasswordOrSiteControl(store, storeForm);
      
      // update exist store data from storeForm
      beanMapper.map(storeForm, store, "store_map_nonnull");

      // status invalid
      if(!Commons.statusIsValid(store.getContractStatus())) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPDATESTORE-CONTRACTSTATUS-INVALID");
      }

      try {
        
        // convert List<OtaForm> to List<StoreOta>
        List<StoreOta> storeOtas = new ArrayList<StoreOta>();
        if(CollectionUtils.isNotEmpty(storeForm.getOtas())) {
          
          for (OtaForm otaForm : storeForm.getOtas()) {
            StoreOta soModel = beanMapper.map(otaForm, StoreOta.class);
            soModel.setStoreId(store.getStoreId());
            storeOtas.add(soModel);
          }
        }
        
        storeService.updateStore(accountId, store, storeOtas);
        
        if (isPushNotification) {
          List<Long> accountNotificaionIds = accountService.getUserIdsByChainOrStore(store.getChainId(), store.getStoreId());
          if (accountNotificaionIds != null)
            for (Long recipientId : accountNotificaionIds) {
              
              Notification notification = new Notification();
              notification.setCreatorId(accountId);
              notification.setRecipientId(recipientId);
              notification.setActionId(store.getStoreId());
              notification.setTitle(Constants.NOTIFICATION_TYPE_CHANGE_PASS);
              
              notificationHelper.asyncSendNotification(notification);
            }
        }

        return HlsResponse.SUCCESS(store);
      } catch (Exception e) {
        e.printStackTrace();
        log.error("Update chain error : {}", e.getMessage());
        return HlsResponse.SERVER_ERROR();
      }

    } else {
      return HlsResponse.FORBIDDEN();
    }
  }

  @PostMapping("/directors")
  public @ResponseBody HlsResponse getListDirector(HttpServletRequest request) {
    log.info("[DEBUG API getListDirector] : {}");
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)
        || request.isUserInRole(Constants.ROLE_USER)) {
      try {
        List<Director> directors = storeService.getAllDirector();
        int total = directors == null ? 0 : directors.size();
        DirectorResultResponse response = new DirectorResultResponse(directors, total);
        return HlsResponse.SUCCESS(response);
      } catch (Exception e) {
        return HlsResponse.BADREQUEST();
      }

    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  private boolean hasViewEditStoreRole(HttpServletRequest request, long storeId) {
    if(request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER))
      return true;
    
    AccountUserDetails userDetails =
        (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Account account = userDetails.getAccount();
    if(account == null)
      return false;
    
    // if ROLE_CHAIN --> only view/edit its store
    if(request.isUserInRole(Constants.ROLE_CHAIN)) {
      if(account.getChainId() == null)
        return false;
      
      List<Store> stores = storeService.findByChainId(account.getChainId());
      for (Store store : stores) {
        if(store.getStoreId() == storeId)
          return true;
      }
      
    }
    
    // if ROLE_STORE --> only view/edit it self
    if(request.isUserInRole(Constants.ROLE_STORE)) {
      if(account.getStoreId() != null && storeId == account.getStoreId().longValue())
        return true;
    }
    
    return false;
  }
  
  @PutMapping("/stores/{id}/otas/{otaId}/renewalPassDate")
  public @ResponseBody HlsResponse updateOTAExpiredDate(HttpServletRequest request,
      @PathVariable("id") long storeId, @PathVariable("otaId") long otaId, @RequestBody OtaForm otaForm) {
    
    log.info("[DEBUG API updateOTAExpiredDate] : storeId - {} - otaId - {}", storeId, otaId);
    
    StoreOta storeOTA = soService.findOne(storeId, otaId);
    if(storeOTA == null)
      return HlsResponse.NOTFOUND();
    
    OTA ota = otaService.findOTA(otaId);
    if(ota == null) {
      return HlsResponse.BADREQUEST();
    }
    
    boolean isPushNotification = !StringUtils.equalsIgnoreCase(storeOTA.getPassword(), otaForm.getPassword());
    
    storeOTA = helper.updatePassDate(ota, storeOTA, otaForm);
    
    if (isPushNotification) {
      
      // get exist store
      Store store = storeService.findStore(storeId);
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      long accountId = userDetails.getAccount().getAccountId();
      
      List<Long> accountNotificaionIds = accountService.getUserIdsByChainOrStore(store.getChainId(), store.getStoreId());
      if (accountNotificaionIds != null)
        for (Long recipientId : accountNotificaionIds) {
          
          Notification notification = new Notification();
          notification.setCreatorId(accountId);
          notification.setRecipientId(recipientId);
          notification.setActionId(store.getStoreId());
          notification.setTitle(Constants.NOTIFICATION_TYPE_CHANGE_PASS);
          
          notificationHelper.asyncSendNotification(notification);
        }
    }
    
    return HlsResponse.SUCCESS(storeOTA);
  }
  
  @GetMapping("/stores/{id}/otas")
  public @ResponseBody HlsResponse getOTAs(HttpServletRequest request, @PathVariable("id") long storeId) {
    
    log.info("[DEBUG API getOTAs] : storeId - {} ", storeId);
    
    Store store = storeService.findStore(storeId);
    if(store == null) {
      return HlsResponse.NOTFOUND();
    }
    
    List<OTA> otas = soService.getStoreOTA(store.getStoreId());
    return HlsResponse.SUCCESS(otas);
  }
}
