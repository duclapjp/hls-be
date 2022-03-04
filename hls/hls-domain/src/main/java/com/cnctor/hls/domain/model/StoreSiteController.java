package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreSiteController implements Serializable{
  private static final long serialVersionUID = 1L;
  private String url;
  private String storeCode;
  private String username;
  private String password;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date expiredDate;
  private String note;
  
  // primary keys
  private long storeId;
  private long siteControllerId;
  
  // extra fields
  private String siteControllerName;
}
