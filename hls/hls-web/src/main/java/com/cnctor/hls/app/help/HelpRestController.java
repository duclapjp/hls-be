package com.cnctor.hls.app.help;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.cnctor.hls.app.task.FileResponseHelper;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Help;
import com.cnctor.hls.domain.model.Store;
import com.cnctor.hls.domain.service.help.HelpService;
import com.cnctor.hls.domain.service.store.StoreService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class HelpRestController {
  
  @Inject
  HelpService helpService;
  
  @Inject
  FileResponseHelper fileHelper;
  
  @Inject
  StoreService storeService;
  
  @PostMapping(value = "/help/manual/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public @ResponseBody HlsResponse upload(HttpServletRequest request,
      @RequestParam("manualFile") MultipartFile manualFile) {

    if (request.isUserInRole(Constants.ROLE_ADMIN)
        || request.isUserInRole(Constants.ROLE_SUBADMIN)) {

      log.info("[DEBUG manual upload ] - upload file : {}, contenttype : {}", manualFile.getOriginalFilename(), manualFile.getContentType());
      
      if(!StringUtils.equalsIgnoreCase(manualFile.getContentType(), MediaType.APPLICATION_PDF_VALUE)) {
        return new HlsResponse(HlsResponse.BADREQUEST, "ERROR-UPLOADMANUAL-FILETYPE-INVALID");
      }
      
      try {
        
        Help help = helpService.uploadManual(manualFile.getOriginalFilename(), manualFile.getBytes());
        
        return HlsResponse.SUCCESS(help);
      } catch (Exception e) {
        e.printStackTrace();
        return HlsResponse.SERVER_ERROR();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping(value = "/help/manual/download")
  public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    log.info("[DEBUG manual download]");
    
    // has permission in task to download file 
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)) {
      
      Help existHelp = helpService.fetchOne();
      
      // existHelp is deleted -> not found
      if(existHelp == null) {
        response.setStatus(HttpStatus.SC_NOT_FOUND);
      } else {
        fileHelper.retrieveFile(existHelp.getManualName(), existHelp.getManualUrl(), response, request);
      }
    } else {
      response.setStatus(HttpStatus.SC_FORBIDDEN);
    }
  }
  
  @GetMapping(value = "/help/manual/store-info")
  public @ResponseBody HlsResponse getStoreInfo(HttpServletRequest request, @RequestParam("storeId") Long storeId) {
    
    if (request.isUserInRole(Constants.ROLE_STORE)) {
      log.info("[DEBUG getStoreInfo] storeId : {}", storeId);
      Store retStore = storeService.findStore(storeId);
      if (retStore != null) {
        /*
        AccountUserDetails userDetails = (AccountUserDetails) SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        Account account = userDetails.getAccount();
        
        if(retStore.getStoreId() != account.getStoreId().longValue()) {
          return HlsResponse.FORBIDDEN();
        }
        */
        Map<String, String> retMap = new HashMap<String, String>();
        retMap.put("manager_name", retStore.getManagerName());
        retMap.put("manager_mail", retStore.getManagerMail());
        retMap.put("manager_phone", retStore.getManagerPhone());
        
        return HlsResponse.SUCCESS(retMap);
      } else {
        return HlsResponse.NOTFOUND();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping(value = "/help/manual/chain-info")
  public @ResponseBody HlsResponse getChainInfo(HttpServletRequest request, @RequestParam("chainId") Long chainId) {
    if (request.isUserInRole(Constants.ROLE_CHAIN)) {
      log.info("[DEBUG getChainInfo] chainId : {}", chainId);
      
      List<Map<String, String>> stores = storeService.storeMapByChainId(chainId);
      return HlsResponse.SUCCESS(stores);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
  
  @GetMapping(value = "/help/manual/filedetail")
  public @ResponseBody HlsResponse getFileDetail(HttpServletRequest request, HttpServletResponse response){

    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)) {

      log.info("[DEBUG manual getFileDetail ] ");
      
      Help existHelp = helpService.fetchOne();
      if(existHelp == null) {
        return HlsResponse.NOTFOUND();
      }
      
      return HlsResponse.SUCCESS(existHelp);
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
