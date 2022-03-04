package com.cnctor.hls.app.filters;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CORSFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    response.addHeader("Access-Control-Allow-Origin", "*");
    response.addHeader("Access-Control-Allow-Headers", "Authorization, X-Requested-StoreId");
    
    log.info("[DEBUG CORSFilter]");
    if (request.getHeader("Access-Control-Request-Method") != null
        && "OPTIONS".equals(request.getMethod())) {
      log.info("Sending Header....");
      response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
      response.addHeader("Access-Control-Allow-Headers", "Content-Type, X-Requested-With, X-Requested-StoreId");
      response.addHeader("Access-Control-Max-Age", "1");
    }
   
    filterChain.doFilter(request, response);
  }
}