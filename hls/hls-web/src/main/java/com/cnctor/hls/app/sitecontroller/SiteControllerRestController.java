package com.cnctor.hls.app.sitecontroller;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.SiteController;
import com.cnctor.hls.domain.service.storesitecontroller.StoreSiteControllerService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class SiteControllerRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  StoreSiteControllerService service;



  @PostMapping("/sitecontrollers")
  public @ResponseBody HlsResponse getSiteControllers(HttpServletRequest request) {
    log.info("[DEBUG API getSiteControllers] : {}");
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_CHAIN) || request.isUserInRole(Constants.ROLE_STORE)
        || request.isUserInRole(Constants.ROLE_USER)) {
      try {
        List<SiteController> siteControllers = service.getAllSiteController();
        int total = siteControllers == null ? 0 : siteControllers.size();
        SiteControllerResultResponse response =
            new SiteControllerResultResponse(siteControllers, total);
        return HlsResponse.SUCCESS(response);
      } catch (Exception e) {
        return HlsResponse.BADREQUEST();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
