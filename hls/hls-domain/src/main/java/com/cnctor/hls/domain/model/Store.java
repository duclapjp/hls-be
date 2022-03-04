package com.cnctor.hls.domain.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Store  implements Serializable{
  
  private static final long serialVersionUID = 1L;
  
  private long storeId;
  private Long chainId;
  private Long directorId;
  private String managerName;
  private String managerMail;
  private String managerPhone;
  private String name;
  private String contractStatus;
  private String note;
  
  private String chainName;
  private String directorName;
  
  private List<StoreOta> otas;
}
