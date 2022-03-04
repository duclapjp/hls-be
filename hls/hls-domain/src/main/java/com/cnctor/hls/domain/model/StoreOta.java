package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.Date;
import com.cnctor.hls.domain.common.utils.Timezone;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class StoreOta  implements Serializable{
  private static final long serialVersionUID = 1L;
  
  //primary keys
  private long storeId;
  private long otaId;
  
  private String url;
  private String customStoreId;
  private String username;
  private String password;
  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd", timezone = Timezone.DEFAULT_TIMEZONE)
  private Date expiredDate;
  private String note;
  
  // extra fields
  private Long otaTypeId;
  private String otaTypeName;
  private boolean isDisplayStoreId;
}
