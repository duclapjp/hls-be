package com.cnctor.hls.app.exception;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;
import com.cnctor.hls.app.utils.HlsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonDelegatingAccessDeniedHandler implements AccessDeniedHandler {

  private final RequestMatcher jsonRequestMatcher;
  private final AccessDeniedHandler delegateHandler;

  public JsonDelegatingAccessDeniedHandler(RequestMatcher jsonRequestMatcher,
      AccessDeniedHandler delegateHandler) {
    this.jsonRequestMatcher = jsonRequestMatcher;
    this.delegateHandler = delegateHandler;
  }

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    // TODO Auto-generated method stub
    log.info(accessDeniedException.getMessage());
    accessDeniedException.printStackTrace();
    log.info("[JsonDelegatingAccessDeniedHandler ] : {}", jsonRequestMatcher.matches(request));
    if (jsonRequestMatcher.matches(request)) {
      // response error information of JSON format
      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
      returnJson(response, HttpStatus.OK, HlsResponse.FORBIDDEN());
    } else {
      // response error page of HTML format
      delegateHandler.handle(request, response, accessDeniedException);
    }
  }
  
  private void returnJson(HttpServletResponse response, HttpStatus status, Object data) {
    response.setStatus(status.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    try {
      new ObjectMapper().writeValue(response.getWriter(), data);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
