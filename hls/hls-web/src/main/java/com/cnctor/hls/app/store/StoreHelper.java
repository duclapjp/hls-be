package com.cnctor.hls.app.store;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.PasswordHistory;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.model.StoreOta;
import com.cnctor.hls.domain.service.passwordhistory.PasswordHistoryService;
import com.cnctor.hls.domain.service.store.StoreService;
import com.cnctor.hls.domain.service.storesitecontroller.StoreSiteControllerService;
import com.cnctor.hls.domain.service.storesota.StoreOtaService;
import com.cnctor.hls.domain.service.userdetails.AccountUserDetails;

@Component
public class StoreHelper {
  
  @Inject
  Mapper beanMapper;
  
  @Inject
  StoreService storeService;
  
  @Inject
  StoreSiteControllerService sscService;
  
  @Inject
  StoreOtaService soService;
  
  @Inject
  PasswordHistoryService phService;
  
  @Transactional(rollbackFor = Exception.class)
  public Store doCreate(StoreForm storeForm) {
    
    // create store
    Store store= beanMapper.map(storeForm, Store.class);
    Store resStore = storeService.createStore(store);
    
    // convert List<OtaForm> to List<StoreOta>
    List<StoreOta> storeOtas = new ArrayList<StoreOta>();
    if(CollectionUtils.isNotEmpty(storeForm.getOtas())) {
      
      for (OtaForm otaForm : storeForm.getOtas()) {
        StoreOta soModel = beanMapper.map(otaForm, StoreOta.class);
        soModel.setStoreId(resStore.getStoreId());
        storeOtas.add(soModel);
      }
    }
    
    for (StoreOta so : storeOtas) {
      soService.insert(so);
    }
    resStore.setOtas(storeOtas);
    return resStore;
  }

  public boolean checkChangeOTAPasswordOrSiteControl(Store oldStore, StoreForm newStore) {
    List<StoreOta> oldOTAs = oldStore.getOtas();
    //List<StoreSiteController> oldSiteControllers = oldStore.getSiteControllers();

    List<OtaForm> newOTAs = newStore.getOtas();
    //List<SiteControllerForm> newSiteControllers = newStore.getSiteControllers();
    
    if (oldOTAs == null || newOTAs == null ) 
      return false;
    boolean result = false;
    
    for (int i=0; i< oldOTAs.size(); i++) {
      StoreOta oldOta  = oldOTAs.get(i);
      result = newOTAs.stream().anyMatch(newOta -> (oldOta.getOtaId() == newOta.getOtaId()
          && ((oldOta.getPassword() == null && newOta.getPassword() != null ) || (
              oldOta.getPassword() != null && !oldOta.getPassword().equals(newOta.getPassword())
              ))
          ));
      if (result)
        break;
    }
    /*
     * if (!result) { for (int i=0; i< oldSiteControllers.size(); i++) { StoreSiteController
     * oldSiteController = oldSiteControllers.get(i); result =
     * newSiteControllers.stream().anyMatch(newSiteController ->
     * (oldSiteController.getSiteControllerId() == newSiteController.getSiteControllerId() &&
     * ((oldSiteController.getPassword() == null && newSiteController.getPassword() != null ) || (
     * oldSiteController.getPassword() != null &&
     * !oldSiteController.getPassword().equals(newSiteController.getPassword()) )) )); if (result)
     * break; } }
     */
    return result;
  }
  
  @Transactional(rollbackFor = Exception.class)
  public StoreOta updatePassDate(OTA ota, StoreOta storeOTA, OtaForm otaForm) {
    
    Long updateDeadline = ota.getPasswordUpdateDeadline();
    if(updateDeadline != null && updateDeadline > 0) {
      Calendar now = Calendar.getInstance();
      now.add(Calendar.DATE, updateDeadline.intValue());
      storeOTA.setExpiredDate(now.getTime());
    }
    
    // is password changed 
    boolean isPassChanged = false;
    if(!StringUtils.equalsIgnoreCase(storeOTA.getPassword(), otaForm.getPassword())) {
      isPassChanged = true;
    }
    
    storeOTA.setUrl(otaForm.getUrl());
    storeOTA.setUsername(otaForm.getUsername());
    storeOTA.setPassword(otaForm.getPassword());
    storeOTA.setCustomStoreId(otaForm.getCustomStoreId());
    storeOTA.setNote(otaForm.getNote());
    
    soService.upsert(storeOTA);
    
    if(isPassChanged) {
      
      AccountUserDetails userDetails =
          (AccountUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      long accountId = userDetails.getAccount().getAccountId();
      
      PasswordHistory ph = new PasswordHistory();
      
      ph.setOtaId(ota.getOtaId());
      ph.setAccountId(accountId);
      ph.setPassword(otaForm.getPassword());
      ph.setUpdatedTime(new Date());
      ph.setStoreId(storeOTA.getStoreId());
      
      phService.insert(ph);
    }
    
    return storeOTA;
  }
}
