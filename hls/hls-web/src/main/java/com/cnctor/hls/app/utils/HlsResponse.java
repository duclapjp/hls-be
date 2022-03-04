package com.cnctor.hls.app.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HlsResponse {

  private int status;
  private Object data;
  private String message;

  public HlsResponse() {}

  public HlsResponse(Object data) {
    this.data = data;
  }

  public HlsResponse(int status, String mess) {
    this.status = status;
    this.message = mess;
  }

  public HlsResponse(int status, Object data) {
    this.status = status;
    this.data = data;
  }

  public static HlsResponse SUCCESS(Object data) {
    return new HlsResponse(SUCCESS, data, SUCCESSFULLY_MESS);
  }
  
  public static HlsResponse BADREQUEST(Object data, String msg) {
    return new HlsResponse(BADREQUEST,data, msg);
  }
  
  public static HlsResponse FORBIDDEN(Object data, String msg) {
    return new HlsResponse(FORBIDDEN,data, msg);
  }

  public static HlsResponse SUCCESS() {
    return new HlsResponse(SUCCESS, SUCCESSFULLY_MESS);
  }
  
  public static HlsResponse BADREQUEST() {
    return new HlsResponse(BADREQUEST, BADREQUEST_MESS);
  }

  public static HlsResponse AUTHEN_FAILED() {
    return new HlsResponse(AUTHEN_FAILED, AUTHEN_FAILED_MESS);
  }

  public static HlsResponse FORBIDDEN() {
    return new HlsResponse(FORBIDDEN, FORBIDDEN_MESS);
  }

  public static HlsResponse NOTFOUND() {
    return new HlsResponse(NOTFOUND, NOTFOUND_MESS);
  }

  public static HlsResponse SQL_FAILED() {
    return new HlsResponse(SQL_FAILED, SQL_FAILED_MESS);
  }

  public static HlsResponse SERVER_ERROR() {
    return new HlsResponse(SERVER_ERROR, SERVER_ERROR_MESS);
  }

  public static HlsResponse UNAVAILABLE() {
    return new HlsResponse(UNAVAILABLE, UNAVAILABLE_MESS);
  }

  public static HlsResponse NOTFOUND(Object data) {
    return new HlsResponse(NOTFOUND, data, FAILED_MESS);
  }

  public static final int SUCCESS = 200;
  public static final int BADREQUEST = 400;
  public static final int AUTHEN_FAILED = 401;
  public static final int FORBIDDEN = 403;
  public static final int NOTFOUND = 404;
  public static final int SQL_FAILED = 400;
  public static final int SERVER_ERROR = 500;
  public static final int UNAVAILABLE = 503;

  public static final String SUCCESSFULLY_MESS = "Successfully";
  public static final String BADREQUEST_MESS = "Bad Request";
  public static final String AUTHEN_FAILED_MESS = "Authentication Failed";
  public static final String FORBIDDEN_MESS = "Forbidden";
  public static final String NOTFOUND_MESS = "Not found";
  public static final String SQL_FAILED_MESS = "Sql Failed";
  public static final String SERVER_ERROR_MESS = "Some internal error";
  public static final String UNAVAILABLE_MESS = "Unavailable";

  public static final String FAILED_MESS = "Failed";

}
