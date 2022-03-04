package com.cnctor.hls.app.handlers;

import java.lang.reflect.Method;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleAsyncExceptionHandler implements AsyncUncaughtExceptionHandler{

  @Override
  public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    log.error("Async exception occurs", ex);
  }
}
