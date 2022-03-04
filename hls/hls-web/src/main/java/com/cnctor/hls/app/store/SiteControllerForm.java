package com.cnctor.hls.app.store;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SiteControllerForm {
  private String url;
  private String storeCode;
  private String username;
  private String password;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
  private Date expiredDate;
  
  private String note;
  private long siteControllerId;
  private long storeId;
}
