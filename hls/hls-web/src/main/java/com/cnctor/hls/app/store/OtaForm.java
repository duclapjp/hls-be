package com.cnctor.hls.app.store;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OtaForm {
  private long otaId;
  private long storeId;
  
  private String customStoreId;
  private String url;
  private String username;
  private String password;
  
  /*
   * @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd") private Date expiredDate;
   */
  private String note;
}
