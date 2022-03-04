package com.cnctor.hls.app.slack;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.HlsResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class SlackIntegrateRestController {
  
  @GetMapping("/slack")
  public @ResponseBody HlsResponse slackIntegrate(HttpServletRequest request,
      @RequestParam("code") String code) {
    log.info("[DEBUG API slackIntegrate] : {}");
    
    log.info("[DEBUG API code ] : {}", code);
    
    return null;
  }
}
