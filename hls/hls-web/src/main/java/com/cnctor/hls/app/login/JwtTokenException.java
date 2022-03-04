package com.cnctor.hls.app.login;

public class JwtTokenException extends Exception {
  private static final long serialVersionUID = 1L;

  public JwtTokenException(String errorMessage) {
    super(errorMessage);
  }
}
