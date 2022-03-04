package com.cnctor.hls.app.ota;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.app.utils.SearchResultResponse;
import com.cnctor.hls.domain.model.OTA;
import com.cnctor.hls.domain.model.OTAType;
import com.cnctor.hls.domain.repository.ota.OTASearchCriteria;
import com.cnctor.hls.domain.service.ota.OTAService;
import com.cnctor.hls.domain.service.otatype.OTATypeService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class OTARestController {
  
  @Inject
  Mapper beanMapper;
  
  @Inject
  OTAService otaService;
  
  @Inject
  OTATypeService otaTypeService;
  
  @PostMapping("/ota")
  public @ResponseBody HlsResponse create(HttpServletRequest request, @RequestBody CreateOTAForm otaForm) {
    
    log.info("[DEBUG create ota - {} ]", otaForm );
    
    if (otaForm == null)
      return HlsResponse.BADREQUEST();
    
    // validate otaform
    String msg = doValidate(otaForm);
    if (msg != null) {
      return HlsResponse.BADREQUEST(null, msg);
    }
    
    if(request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
      
      OTA ota = beanMapper.map(otaForm, OTA.class);
      // default ota is enabled
      ota.setStatus(Constants.OTA_STATUS_ENABLED);
      try {
        otaService.createOTA(ota);
        return HlsResponse.SUCCESS(ota);
      } catch (Exception e) {
        log.error("Something error create OTA : {}", e.getMessage());
        return HlsResponse.SERVER_ERROR();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @PutMapping("/otas/{id}")
  public @ResponseBody HlsResponse update(HttpServletRequest request, @PathVariable("id") long otaId, @RequestBody CreateOTAForm otaForm) {

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {

      log.info("[DEBUG update ota ] - otaId : {}", otaId);

      // do validate 
      String msg = doValidate(otaForm);
      if (msg != null) {
        return HlsResponse.BADREQUEST(null, msg);
      }

      // get exist ota
      OTA ota = otaService.findOTA(otaId);
      if (ota == null)
        return HlsResponse.NOTFOUND();
      
      // update exist chain data from chainForm
      beanMapper.map(otaForm, ota);
 
      try {
        otaService.updateOTA(ota);
        return HlsResponse.SUCCESS(ota);
      } catch (Exception e) {
        log.error("Update ota error : {}", e.getMessage());
        return HlsResponse.SERVER_ERROR();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  
  @GetMapping("/otatypes")
  public @ResponseBody HlsResponse getOTATypeList(HttpServletRequest request,
      @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "ota_type_id") String sortBy, 
      @RequestParam(defaultValue = "1") Long sortByType) {
    
    OTASearchCriteria criteria = new OTASearchCriteria();
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    
    criteria.setSortByType(sortByType);
    criteria.setSize(size);
    criteria.setPage(page);
    
    long total = otaTypeService.count();
    
    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<OTAType> otaTypes = otaTypeService.filter(criteria);
      SearchResultResponse response = new SearchResultResponse(otaTypes, total);
      return HlsResponse.SUCCESS(response);
    }
  }
  
  
  @GetMapping("/otas")
  public @ResponseBody HlsResponse filterOTA(HttpServletRequest request,
      @RequestParam(defaultValue = "10") int size, @RequestParam(defaultValue = "1") int page,
      @RequestParam(defaultValue = "name") String sortBy, @RequestParam(defaultValue = "1") Long sortByType,
      @RequestParam(required = false, name="name") String name, @RequestParam(required = false, name = "ota_type_id") Long otaTypeId,
      @RequestParam(required = false, name="password_update_deadline") Long passUpdateDeadline, 
      @RequestParam(required = false, name = "status") String status) {
    
    OTASearchCriteria criteria = new OTASearchCriteria();
    if (sortBy != null)
      criteria.setSortBy(sortBy);
    
    criteria.setSortByType(sortByType);
    criteria.setSize(size);
    criteria.setPage(page);
    
    criteria.setName(name);
    criteria.setOtaTypeId(otaTypeId);
    criteria.setPasswordUpdateDeadline(passUpdateDeadline);
    criteria.setStatus(status);
    
    long total = otaService.countBySearchCriteria(criteria);

    if (total == 0) {
      return HlsResponse.SUCCESS(null);
    } else {
      List<OTA> otas = otaService.searchCriteria(criteria);
      SearchResultResponse response = new SearchResultResponse(otas, total);
      return HlsResponse.SUCCESS(response);
    }
  }
  
  
  @PutMapping("/otas/{id}/status")
  public @ResponseBody HlsResponse updateStatus(HttpServletRequest request, @PathVariable("id") long otaId, 
      @RequestBody Map<String, String> reqMap) {
    
    // check permission to view
    if(!(request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN))) {
      return HlsResponse.FORBIDDEN();
    }
    String updateStatus = reqMap.get("status");
    log.info("[DEBUG update ota status ] - otaId : {} - status : {}", otaId, updateStatus);
    
    if(! Arrays.asList(Constants.OTA_STATUS_LIST).contains(updateStatus)) {
      return HlsResponse.BADREQUEST(null, "ERROR-OTA-STATUS-INVALID");
    }
    
 // get exist ota
    OTA ota = otaService.findOTA(otaId);
    if (ota == null)
      return HlsResponse.NOTFOUND();
    
    try {
      ota.setStatus(updateStatus);
      otaService.updateOTA(ota);
      return HlsResponse.SUCCESS(ota);
    } catch (Exception e) {
      log.error("Update ota error : {}", e.getMessage());
      return HlsResponse.SERVER_ERROR();
    }
  }
  
  @GetMapping("/otas/{id}")
  public @ResponseBody HlsResponse getOTADetail(HttpServletRequest request,
      @PathVariable("id") long otaId) {

    log.info("[DEBUG getOTADetail] - {}", otaId);
    
    OTA ota = otaService.findOTA(otaId);
    if (ota == null) {
      return HlsResponse.NOTFOUND();
    }
    
    return HlsResponse.SUCCESS(ota);
  }
  
  /**
   * validate ota form
   * @param otaForm
   * @return
   */
  private String doValidate(CreateOTAForm otaForm) {
    if(otaForm.getOtaTypeId() == null) {
      return "ERROR-OTA-OTATYPE-REQUIRED";
    }
    
    if(StringUtils.isBlank(otaForm.getName())) {
      return "ERROR-OTA-NAME-REQUIRED";
    }
    
    if(StringUtils.isBlank(otaForm.getLoginUrlFixed1()) && StringUtils.isBlank(otaForm.getLoginUrlFixed2()) ) {
      return "ERROR-OTA-URL-REQUIRED";
    }
    
    /*
    if(StringUtils.isBlank(otaForm.getLoginId())) {
      return "ERROR-OTA-LOGINID-REQUIRED";
    }
    
    if(StringUtils.isBlank(otaForm.getPassword())) {
      return "ERROR-OTA-PASSWORD-REQUIRED";
    }
    */
    return null;
  }
}
